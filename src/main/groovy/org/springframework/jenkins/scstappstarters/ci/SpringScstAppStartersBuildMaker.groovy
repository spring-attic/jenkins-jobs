package org.springframework.jenkins.scstappstarters.ci

import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.common.job.Cron
import org.springframework.jenkins.common.job.JdkConfig
import org.springframework.jenkins.common.job.Maven
import org.springframework.jenkins.common.job.TestPublisher
import org.springframework.jenkins.scstappstarters.common.SpringScstAppStarterJobs

import static org.springframework.jenkins.common.job.Artifactory.artifactoryMaven3Configurator
import static org.springframework.jenkins.common.job.Artifactory.artifactoryMavenBuild
/**
 * @author Marcin Grzejszczak
 * @author Soby Chacko
 */
class SpringScstAppStartersBuildMaker implements JdkConfig, TestPublisher,
        Cron, SpringScstAppStarterJobs, Maven {

    private final DslFactory dsl
    final String organization
    final String project
    final String repository

    String branchToBuild = "master"

    String jdkVersion = jdk8()

    SpringScstAppStartersBuildMaker(DslFactory dsl, String organization, String repository,
                                    String project, String branchToBuild) {
        this.dsl = dsl
        this.organization = organization
        this.repository = repository
        this.project = project
        this.branchToBuild = branchToBuild
    }

    void deploy(boolean appsBuild = true, boolean checkTests = true,
                boolean dockerHubPush = true, boolean githubPushTrigger = true,
                boolean docsBuild = false, boolean isRelease = false,
                String releaseType = "", String cdToApps = "") {

        dsl.job("${prefixJob(project)}-${branchToBuild}-ci") {
            if (githubPushTrigger && !isRelease) {
                triggers {
                    githubPush()
                }
            }
            scm {
                git {
                    remote {
                        url "https://github.com/${organization}/${repository}"
                        branch branchToBuild
                    }
                }
            }

            jdk jdk8()
            wrappers {
                colorizeOutput()
                maskPasswords()
                credentialsBinding {
                    usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                }
                if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
                    credentialsBinding {
                        file('FOO_SEC', "spring-signing-secring.gpg")
                        file('FOO_PUB', "spring-signing-pubring.gpg")
                        string('FOO_PASSPHRASE', "spring-gpg-passphrase")
                        usernamePassword('SONATYPE_USER', 'SONATYPE_PASSWORD', "oss-token")
                        usernamePassword('DOCKER_HUB_USERNAME', 'DOCKER_HUB_PASSWORD', "hub.docker.com-springbuildmaster")
                    }
                }
            }

            steps {

//                if (appsBuild) {
//                    shell(removeAppsDirectory())
//                }
                if (isRelease) {
                    if (docsBuild) {
                        shell(cleanAndInstall(isRelease, releaseType))
                    }
                    else if (appsBuild) {
                        shell(cleanAndDeployWithGenerateApps(isRelease, releaseType, cdToApps))
                    }
                    else {
                        shell(cleanAndDeploy(isRelease, releaseType))
                    }
                }
                else {
                    //maven {
                        //mavenInstallation(maven35())
                        if (docsBuild) {
                            maven {
                                goals('clean install -U -Pspring')
                            }
                        }
                        else if (appsBuild) {
                            shell("""set -e
                            #!/bin/bash -x
                            export MAVEN_PATH=${mavenBin()}
                            ${setupGitCredentials()}
                            echo "Building app generator"
                            cd ${cdToApps}
                            rm -rf apps
                            ./mvnw clean package -U
                            ${cleanGitCredentials()}
                            """)
                            //goals('clean deploy -U -Pspring')
                        }
                        else {
                            maven {
                                goals('clean deploy -U -Pspring')
                            }
                        }
                    //}
                }

                if (appsBuild) {
                    if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
                        shell("""set -e
                        #!/bin/bash -x
                        export MAVEN_PATH=${mavenBin()}
                        ${setupGitCredentials()}
                        echo "Building apps"
                        cd ${cdToApps}
                        cd apps
                        set +x
                        ./mvnw clean deploy -Pspring -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
                            gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
                        set -x
                        ${cleanGitCredentials()}
                        """)
                    }
                    else {
                        shell("""set -e
                        #!/bin/bash -x
                        export MAVEN_PATH=${mavenBin()}
                        ${setupGitCredentials()}
                        echo "Building apps"
                        cd ${cdToApps}
                        cd apps
                        ./mvnw clean deploy -U
                        ${cleanGitCredentials()}
                        """)
                    }
                }
                if (dockerHubPush) {
                    shell("""set -e
                    #!/bin/bash -x
					export MAVEN_PATH=${mavenBin()}
					${setupGitCredentials()}
					echo "Pushing to Docker Hub"
                    cd ${cdToApps}
                    cd apps
                    set +x
                    ./mvnw -U --batch-mode clean package docker:build docker:push -DskipTests -Ddocker.username="\$${dockerHubUserNameEnvVar()}" -Ddocker.password="\$${dockerHubPasswordEnvVar()}"
					set -x
					${cleanGitCredentials()}
					""")
                }
            }
            configure {

                if (docsBuild) {
                    artifactoryMavenBuild(it as Node) {
//                        mavenVersion(maven33())
//                        if (releaseType != null && releaseType.equals("milestone")) {
//                            goals('clean install -U -Pfull -Pspring -Pmilestone')
//                        }
//                        else {
//                            goals('clean install -U -Pfull -Pspring')
//                        }
                        mavenVersion(maven35())
                        goals('clean install -U -Pfull -Pspring')
                    }
                    artifactoryMaven3Configurator(it as Node) {
                        if (isRelease && releaseType != null && releaseType.equals("milestone")) {
                            deployReleaseRepository("libs-milestone-local")
                        }
                        else if (isRelease) {
                            deployReleaseRepository("libs-release-local")
                        }
                    }
                }

            }




//                if (!appsBuild) {
//                    maven {
//                        mavenInstallation(maven35())
//                        goals('clean deploy -U -Pspring')
//                    }
//                }
//
//                if (appsBuild) {
//                    if (isRelease && releaseType != null && !releaseType.equals("milestone")) {
////                        shell("""set -e
////                        #!/bin/bash -x
////                        export MAVEN_PATH=${mavenBin()}
////                        ${setupGitCredentials()}
////                        echo "Building apps"
////                        cd apps
////                        set +x
////                        ../mvnw clean deploy -Pspring -Dgpg.secretKeyring="\$${gpgSecRing()}" -Dgpg.publicKeyring="\$${
////                            gpgPubRing()}" -Dgpg.passphrase="\$${gpgPassphrase()}" -DSONATYPE_USER="\$${sonatypeUser()}" -DSONATYPE_PASSWORD="\$${sonatypePassword()}" -Pcentral -U
////                        set -x
////                        ${cleanGitCredentials()}
////                        """)
//                    }
//                    else {
//                        shell("""set -e
//                        #!/bin/bash -x
//                        export MAVEN_PATH=${mavenBin()}
//                        ${setupGitCredentials()}
//                        echo "Building apps"
//                        cd ${cdToApps}
//                        if [ -e docker-compose.sh ]
//                        then
//                            ./docker-compose.sh
//                        else
//                            echo "skipping docker step"
//                        fi
//                        rm -rf apps
//                        ./mvnw clean package -U
//                        if [ -e docker-compose-stop.sh ]
//                        then
//                            ./docker-compose-stop.sh
//                        else
//                            echo "skipping docker step"
//                        fi
//                        cd apps
//                        ../mvnw clean deploy -U
//                        ${cleanGitCredentials()}
//                        """)
//                    }
//                }
//                if (dockerHubPush) {
//                    shell("""set -e
//                    #!/bin/bash -x
//					export MAVEN_PATH=${mavenBin()}
//					${setupGitCredentials()}
//					echo "Pushing to Docker Hub"
//                    cd ${cdToApps}
//                    cd apps
//                    set +x
//                    #../mvnw -U --batch-mode clean package docker:build docker:push -DskipTests -Ddocker.username="\$${dockerHubUserNameEnvVar()}" -Ddocker.password="\$${dockerHubPasswordEnvVar()}"
//                    ../mvnw -U clean package jib:dockerBuild -DskipTests -Djib.to.auth.username="\$${dockerHubUserNameEnvVar()}" -Djib.to.auth.password="\$${dockerHubPasswordEnvVar()}"
//					set -x
//
//					${cleanGitCredentials()}
//					""")
//                }
//            }
//            configure {
//
//                if (docsBuild) {
//                    artifactoryMavenBuild(it as Node) {
//                        mavenVersion(maven35())
//                        if (releaseType != null && releaseType.equals("milestone")) {
//                            goals('clean install -U -Pfull -Pspring -Pmilestone')
//                        }
//                        else {
//                            goals('clean install -U -Pfull -Pspring')
//                        }
//                    }
//                    artifactoryMaven3Configurator(it as Node) {
//                        if (isRelease && releaseType != null && releaseType.equals("milestone")) {
//                            deployReleaseRepository("libs-milestone-local")
//                        }
//                        else if (isRelease) {
//                            deployReleaseRepository("libs-release-local")
//                        }
//                    }
//                }
//
//            }

            publishers {
                //mailer('scdf-ci@pivotal.io', true, true)
//                if (checkTests) {
//                    archiveJunit mavenJUnitResults()
//                }
            }
        }
    }
}
