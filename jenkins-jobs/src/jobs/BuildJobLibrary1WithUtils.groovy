import javaposse.jobdsl.dsl.jobs.MavenJob
import util.ScmUtils

final String repoUrl = 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
final String projectPath = 'library1/'

MavenJob job = mavenJob('Jenkins Tutorial Demo - Library 1 (DSL with Utils)') {
    description 'Build job for Jenkins Tutorial / Library 1'

    logRotator {
        numToKeep 5
    }

    triggers {
        scm 'H/15 * * * *'
        snapshotDependencies true
    }

    rootPOM "${projectPath}pom.xml"
    goals 'clean install'
}

ScmUtils.setupGit(job, true, repoUrl, projectPath)
