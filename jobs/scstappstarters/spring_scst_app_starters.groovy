package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStartersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - "", milestone or ga

// Master CI
new SpringScstAppStartersPhasedBuildMaker(dsl).build(false, "")
