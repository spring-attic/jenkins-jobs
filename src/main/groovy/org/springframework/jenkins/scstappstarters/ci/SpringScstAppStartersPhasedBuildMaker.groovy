package org.springframework.jenkins.scstappstarters.ci

import org.springframework.jenkins.scstappstarters.common.AllScstAppStarterJobs
import javaposse.jobdsl.dsl.DslFactory
import org.springframework.jenkins.scstappstarters.common.SpringScstAppStarterJobs

/**
 * @author Soby Chacko
 */
class SpringScstAppStartersPhasedBuildMaker implements SpringScstAppStarterJobs {

    private final DslFactory dsl

    SpringScstAppStartersPhasedBuildMaker(DslFactory dsl) {
        this.dsl = dsl
    }

    void build(boolean isRelease, String releaseType, String branchToBuild = "master") {
        buildAllRelatedJobs(isRelease, releaseType, branchToBuild)
        dsl.multiJob("spring-scst-apps-builds" + "-" + branchToBuild) {
            steps {
                if (!isRelease) {
                    phase('core-phase', 'COMPLETED') {
                        triggers {
                            githubPush()
                        }
                        scm {
                            git {
                                remote {
                                    url "https://github.com/spring-cloud-stream-app-starters/core"
                                    branch branchToBuild
                                }
                            }
                        }
                        String prefixedProjectName = prefixJob("core")
                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                            currentJobParameters()
                        }
                    }
                }

                int counter = 1

                (AllScstAppStarterJobs.ALL_JOBS).each { List<String> ph ->
                        phase("apps-ci-group-${counter}", 'COMPLETED') {
                            ph.each {
                                String projectName ->
                                    String prefixedProjectName = prefixJob(projectName)
                                    phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
                                            currentJobParameters()
                                    }
                            }
                        }
                        counter++;
                }

//                if (!isRelease) {
//                    phase('app-starters-release-phase') {
//                        String prefixedProjectName = prefixJob("app-starters-release")
//                        if (branchToBuild.equals("1.3.x")) {
//                            branchToBuild = "Celsius"
//                        }
//                        else if (branchToBuild.equals("2.0.x")) {
//                            branchToBuild = "Darwin"
//                        }
//                        else if (branchToBuild.equals("2.1.x")) {
//                            branchToBuild = "Einstein"
//                        }
//                        phaseJob("${prefixedProjectName}-${branchToBuild}-ci".toString()) {
//                            currentJobParameters()
//                        }
//                    }
//                }
            }
        }
    }

    void buildAllRelatedJobs(boolean isRelease, String releaseType, String branchToBuild) {
        if (isRelease) {
//            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core", branchToBuild)
//                    .deploy(false, false, false, false, false, isRelease, releaseType)
//
//            if (branchToBuild.equals("1.3.x")) {
//                AllScstAppStarterJobs.CELSIUS_ALL_JOBS.each {
//                    new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it, branchToBuild)
//                            .deploy(true, true,
//                            true, false, false, isRelease, releaseType)
//                }
//            } else if (branchToBuild.equals("2.0.x")) {
//                AllScstAppStarterJobs.DARWIN_ALL_JOBS.each {
//                    new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it, branchToBuild)
//                            .deploy(true, true,
//                            true, false, false, isRelease, releaseType)
//                }
//            } else { //master branch
//                AllScstAppStarterJobs.ALL_JOBS.each {
//                    new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", it, branchToBuild)
//                            .deploy(true, true,
//                            true, false, false, isRelease, releaseType)
//                }
//            }
//            if (branchToBuild.equals("1.3.x")) {
//                branchToBuild = "Celsius"
//            } else if (branchToBuild.equals("2.0.x")) {
//                branchToBuild = "Darwin"
//            }
//            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release", branchToBuild)
//                    .deploy(false, false, false, false, true, isRelease, releaseType)
        }
        else {
            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "core", "core", branchToBuild)
                    .deploy(false, false, false, true, false, isRelease, releaseType)

            // master branch
            AllScstAppStarterJobs.PHASE1_JOBS.each { k,v ->
                new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "stream-apps", "${k}", branchToBuild).deploy(true, true,
                        true, true, false, isRelease, releaseType, "${v}")
            }

            new SpringScstAppStartersBuildMaker(dsl, "spring-cloud-stream-app-starters", "app-starters-release", "app-starters-release", branchToBuild)
                    .deploy(false, false, false, true, true, isRelease, releaseType)
        }
    }
}