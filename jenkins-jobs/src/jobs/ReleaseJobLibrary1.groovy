mavenJob('Jenkins Tutorial Demo - Library 1 - Release (DSL)') {
    description 'Release job for Jenkins Tutorial / Library 1'

    logRotator {
        numToKeep 5
    }

    parameters {
        stringParam('releaseVersion', '',
                'The release version for the artifact. If you leave this empty, the current SNAPSHOT version ' +
                        'will be used with the "-SNAPSHOT" suffix removed (example: if the current version is ' +
                        '"1.0-SNAPSHOT", the release version will be "1.0").')

        stringParam('nextSnapshotVersion', '',
                'The snapshot version to be used after the release. If you leave this empty, the minor version ' +
                        'of the release will be incremented by one (example: if the release is "1.0", the next ' +
                        'snapshot version will be "1.1-SNAPSHOT").')
    }

    scm {
        git {
            remote {
                url 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
            }

            branch 'origin/master'

            // Add extensions 'SparseCheckoutPaths' and 'PathRestriction'
            def nodeBuilder = NodeBuilder.newInstance()
            def sparseCheckout = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.SparseCheckoutPaths')
            sparseCheckout.appendNode('sparseCheckoutPaths')
                    .appendNode('hudson.plugins.git.extensions.impl.SparseCheckoutPath')
                    .appendNode('path', 'library1/')
            def pathRestrictions = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.PathRestriction')
            pathRestrictions.appendNode('includedRegions', 'library1/.*')
            extensions {
                extensions << sparseCheckout
                extensions << pathRestrictions
            }

            extensions {
                localBranch 'master'
            }
        }
    }

    preBuildSteps {
        systemGroovyCommand '''\
                import hudson.model.StringParameterValue
                import hudson.model.ParametersAction
                
                def env = build.getEnvironment(listener)
                String releaseVersion = env.get('releaseVersion')
                String nextSnapshotVersion = env.get('nextSnapshotVersion')
                
                if (!releaseVersion) {
                    String pomPath = build.workspace.toString() + '/library1/pom.xml'
                    def pom = new XmlSlurper().parse(new File(pomPath))
                    releaseVersion = pom.version.toString().replace('-SNAPSHOT', '')
                    println "releaseVersion (calculated) = $releaseVersion"
                    def param = new StringParameterValue('releaseVersion', releaseVersion)
                    build.replaceAction(new ParametersAction(param))
                }
                
                if (!nextSnapshotVersion) {
                    def tokens = releaseVersion.split('\\\\.')
                    nextSnapshotVersion = tokens[0] + '.' + (Integer.parseInt(tokens[1]) + 1) + '-SNAPSHOT'
                    println "nextSnapshotVersion (calculated) = $nextSnapshotVersion"
                    def param1 = new StringParameterValue('releaseVersion', releaseVersion)
                    def param2 = new StringParameterValue('nextSnapshotVersion', nextSnapshotVersion)
                    build.replaceAction(new ParametersAction(param1, param2))
                }
				'''.stripIndent()

        maven {
            mavenInstallation 'Latest'
            goals 'versions:set ' +
                    '-DnewVersion=${releaseVersion} ' +
                    '-DgenerateBackupPoms=false'
            rootPOM "library1/pom.xml"
        }

        maven {
            mavenInstallation 'Latest'
            goals 'versions:use-releases ' +
                    '-DgenerateBackupPoms=false ' +
                    '-DprocessDependencyManagement=true'
            rootPOM "library1/pom.xml"
        }

        shell '''\
              if find library1/ -name 'pom.xml' | xargs grep -n "SNAPSHOT"; then
                echo 'SNAPSHOT versions not allowed in a release\'
                exit 1
              fi
              '''.stripIndent()
    }

    rootPOM 'library1/pom.xml'
    goals 'clean install'

    postBuildSteps('SUCCESS') {
        maven {
            mavenInstallation 'Latest'
            goals 'scm:checkin ' +
                    '-Dmessage="Release version ${project.artifactId}:${releaseVersion}" ' +
                    '-DdeveloperConnectionUrl=scm:git:git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
            rootPOM "library1/pom.xml"
        }

        maven {
            mavenInstallation 'Latest'
            goals 'scm:tag ' +
                    '-Dtag=${project.artifactId}-${releaseVersion} ' +
                    '-DdeveloperConnectionUrl=scm:git:git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
            rootPOM "library1/pom.xml"
        }

        maven {
            mavenInstallation 'Latest'
            goals 'versions:set ' +
                    '-DnewVersion=${nextSnapshotVersion} ' +
                    '-DgenerateBackupPoms=false'
            rootPOM "library1/pom.xml"
        }

        maven {
            mavenInstallation 'Latest'
            goals 'scm:checkin ' +
                    '-Dmessage="Switch to next snapshot version: ${project.artifactId}:${nextSnapshotVersion}" ' +
                    '-DdeveloperConnectionUrl=scm:git:git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
            rootPOM "library1/pom.xml"
        }
    }
}
