package util

import javaposse.jobdsl.dsl.Job

class ReleaseUtils {

    private static final String MAVEN_INSTALLATION = 'Latest'

    static void setupReleaseParams(Job job) {
        job.with {
            parameters {
                stringParam('releaseVersion', '',
                        'The release version for the artifact. If you leave this empty, the current SNAPSHOT version ' +
                                'will be used with the "-SNAPSHOT" suffix removed (example: if the current version ' +
                                'is "1.0-SNAPSHOT", the release version will be "1.0").')

                stringParam('nextSnapshotVersion', '',
                        'The snapshot version to be used after the release. If you leave this empty, the minor ' +
                                'version of the release will be incremented by one (example: if the release is "1.0", ' +
                                'the next snapshot version will be "1.1-SNAPSHOT").')
            }
        }
    }

    static void setupPreBuildSteps(Job job, String projectPath) {
        // Define release and next SNAPSHOT versions
        job.with {
            preBuildSteps {
                systemGroovyCommand """\
                        import hudson.model.StringParameterValue
                        import hudson.model.ParametersAction
                        
                        def env = build.getEnvironment(listener)
                        String releaseVersion = env.get('releaseVersion')
                        String nextSnapshotVersion = env.get('nextSnapshotVersion')
                        
                        if (!releaseVersion) {
                            String pomPath = build.workspace.toString() + '/${projectPath}pom.xml'
                            def pom = new XmlSlurper().parse(new File(pomPath))
                            releaseVersion = pom.version.toString().replace('-SNAPSHOT', '')
                            println "releaseVersion (calculated) = \$releaseVersion"
                            def param = new StringParameterValue('releaseVersion', releaseVersion)
                            build.replaceAction(new ParametersAction(param))
                        }
                        
                        if (!nextSnapshotVersion) {
                            def tokens = releaseVersion.split('\\\\.')
                            nextSnapshotVersion = tokens[0] + '.' + (Integer.parseInt(tokens[1]) + 1) + '-SNAPSHOT'
                            println "nextSnapshotVersion (calculated) = \$nextSnapshotVersion"
                            def param1 = new StringParameterValue('releaseVersion', releaseVersion)
                            def param2 = new StringParameterValue('nextSnapshotVersion', nextSnapshotVersion)
                            build.replaceAction(new ParametersAction(param1, param2))
                        }
                        """.stripIndent()

                String rootPOMValue = "${projectPath}pom.xml"

                // Set release version
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION // will look in MavenContext if not scoped
                    goals 'versions:set ' +
                            '-DnewVersion=${releaseVersion} ' +
                            '-DgenerateBackupPoms=false'
                    rootPOM rootPOMValue
                }

                // Use release versions in dependencies
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION // will look in MavenContext if not scoped
                    goals 'versions:use-releases ' +
                            '-DgenerateBackupPoms=false ' +
                            '-DprocessDependencyManagement=true'
                    rootPOM rootPOMValue
                }

                // Enforce no SNAPSHOTs
                shell """\
                      if find $projectPath -name 'pom.xml' | xargs grep -n "SNAPSHOT"; then
                        echo 'SNAPSHOT versions not allowed in a release'
                        exit 1
                      fi
                      """.stripIndent()
            }
        }
    }

    static void setupPostBuildSteps(Job job, String projectPath, String repoUrl) {
        job.with {
            postBuildSteps('SUCCESS') {
                String rootPOMValue = "${projectPath}pom.xml"

                // Check in release version
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION
                    goals "scm:checkin " +
                            "-Dmessage=\"Release version \${project.artifactId}:\${releaseVersion}\" " +
                            "-DdeveloperConnectionUrl=scm:git:$repoUrl"
                    rootPOM rootPOMValue
                }

                // Tag release version
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION // will look in MavenContext if not scoped
                    goals "scm:tag " +
                            "-Dtag=\${project.artifactId}-\${releaseVersion} " +
                            "-DdeveloperConnectionUrl=scm:git:$repoUrl"
                    rootPOM rootPOMValue
                }

                // Set next SNAPSHOT version
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION // will look in MavenContext if not scoped
                    goals 'versions:set ' +
                            '-DnewVersion=${nextSnapshotVersion} ' +
                            '-DgenerateBackupPoms=false'
                    rootPOM rootPOMValue
                }

                // Check in SNAPSHOT version
                maven {
                    mavenInstallation ReleaseUtils.MAVEN_INSTALLATION // will look in MavenContext if not scoped
                    goals "scm:checkin " +
                            "-Dmessage=\"Switch to next snapshot version: \${project.artifactId}:\${nextSnapshotVersion}\" " +
                            "-DdeveloperConnectionUrl=scm:git:$repoUrl"
                    rootPOM rootPOMValue
                }
            }
        }
    }

}
