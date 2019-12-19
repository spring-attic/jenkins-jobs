package scstappstarters

import org.springframework.jenkins.scstappstarters.ci.SpringScstAppStartersPhasedBuildMaker
import javaposse.jobdsl.dsl.DslFactory

DslFactory dsl = this

String releaseType = "" // possible values are - "", milestone or ga

// Master CI
new SpringScstAppStartersPhasedBuildMaker(dsl).build(true, "milestone")

// 1.3.x CI
new SpringScstAppStartersPhasedBuildMaker(dsl).build(false, "", "1.3.x")

// 2.0.x CI
new SpringScstAppStartersPhasedBuildMaker(dsl).build(false, "", "2.0.x")

// 2.1.x CI
new SpringScstAppStartersPhasedBuildMaker(dsl).build(false, "", "2.1.x")
