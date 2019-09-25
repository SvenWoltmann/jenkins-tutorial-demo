import javaposse.jobdsl.dsl.jobs.MavenJob
import util.ReleaseUtils
import util.ScmUtils

final String repoUrl = 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
final String projectPath = 'application1/'

MavenJob job = mavenJob('Jenkins Tutorial Demo - Application 1 - Release (DSL with Utils)') {
    description 'Release job for Jenkins Tutorial / Application 1'

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
