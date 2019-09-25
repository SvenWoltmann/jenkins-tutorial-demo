package util

class ProjectUtils {

    private static final String WORKSPACE_HOME = '/var/jenkins_home/workspace/'

    static List<String> findProjectPaths(String jobPath) {
        String workspacePath = extractWorkspacePath(jobPath)

        String lastRootProject = 'A FOLDER THAT DOESNT EXIST'

        return new FileNameFinder().getFileNames(workspacePath, '**/pom.xml')
                .collect {
                    it.replace(workspacePath, '').replace('pom.xml', '')
                }
                .sort()
                .findAll {
                    // although 'jenkins-jobs/' contains a pom.xml,
                    // it's not actually a Maven project we want to create a Jenkins job for
                    if (it.equals('jenkins-jobs/')) {
                        return false
                    }

                    // filter out sub modules of Maven multi-module projects
                    if (it.startsWith(lastRootProject)) {
                        return false
                    }
                    lastRootProject = it
                    return true
                }
    }

    private static String extractWorkspacePath(String jobPath) {
        // the job path is for example:
        // '/var/jenkins_home/workspace/Jenkins Tutorial Demo - Seed Job/jenkins-jobs/src/jobs/BuildJobGeneric.groovy'

        // from that, we want to extract the workspace path:
        // '/var/jenkins_home/workspace/'

        if (!jobPath.startsWith(WORKSPACE_HOME)) {
            throw new IllegalArgumentException("jobPath '$jobPath' doesn't start with '$WORKSPACE_HOME'")
        }

        int endOfWorkspacePath = jobPath.indexOf('/', WORKSPACE_HOME.length())
        if (endOfWorkspacePath == -1) {
            throw new IllegalArgumentException("jobPath '$jobPath' starts with '$WORKSPACE_HOME', but no '/' is following")
        }

        return jobPath.substring(0, endOfWorkspacePath + 1)
    }

    static String removeTrailingSlash(String path) {
        int len = path.length()
        if (path.charAt(len - 1) == '/') {
            path = path.substring(0, len - 1)
        }
        return path
    }

}
