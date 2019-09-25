import javaposse.jobdsl.dsl.jobs.MavenJob
import util.ProjectUtils
import util.ReleaseUtils
import util.ScmUtils

final String repoUrl = 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'

List<String> projectPaths = ProjectUtils.findProjectPaths(__FILE__)

projectPaths.each { projectPath ->
    MavenJob job = mavenJob('Jenkins Tutorial Demo - Release ' + ProjectUtils.removeTrailingSlash(projectPath)) {
        description "Automatically generated release job for Maven project detected in folder $projectPath."

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
}
