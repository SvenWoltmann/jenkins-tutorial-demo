import javaposse.jobdsl.dsl.jobs.MavenJob
import util.ReleaseUtils
import util.ScmUtils

final String repoUrl = 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
final String projectPath = 'library1/'

MavenJob job = mavenJob('Jenkins Tutorial Demo - Library 1 - Release (DSL with Utils)') {
    description 'Release job for Jenkins Tutorial / Library 1'

    logRotator {
        numToKeep 5
    }

    rootPOM "${projectPath}pom.xml"
    goals 'clean install'
}

ScmUtils.setupGit(job, false, repoUrl, projectPath)

ReleaseUtils.setupReleaseParams(job)
ReleaseUtils.setupPreBuildSteps(job, projectPath)
ReleaseUtils.setupPostBuildSteps(job, projectPath, repoUrl)
