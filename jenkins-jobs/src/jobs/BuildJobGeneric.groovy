import javaposse.jobdsl.dsl.jobs.MavenJob
import util.ProjectUtils
import util.ScmUtils

final String repoUrl = 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'

List<String> projectPaths = ProjectUtils.findProjectPaths(__FILE__)

projectPaths.each { projectPath ->
    MavenJob job = mavenJob('Jenkins Tutorial Demo - Build ' + ProjectUtils.removeTrailingSlash(projectPath)) {
        description "Automatically generated build job for Maven project detected in folder $projectPath."

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
}
