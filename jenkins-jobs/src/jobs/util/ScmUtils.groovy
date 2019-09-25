package util

import javaposse.jobdsl.dsl.Job

class ScmUtils {

    static void setupGit(Job job, boolean addBranchParameter, String repoUrl, String sparseCheckoutPath = null) {
        job.with {
            if (addBranchParameter) {
                parameters {
                    gitParam('Branch') {
                        description 'The Git branch to checkout'
                        type 'BRANCH'
                        defaultValue 'origin/master'
                    }
                }
            }

            scm {
                git {
                    remote {
                        url repoUrl
                    }

                    branch addBranchParameter ? '$Branch' : 'origin/master'

                    // Add extensions 'SparseCheckoutPaths' and 'PathRestriction'
                    if (sparseCheckoutPath) {
                        def nodeBuilder = NodeBuilder.newInstance()
                        def sparseCheckout = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.SparseCheckoutPaths')
                        sparseCheckout.appendNode('sparseCheckoutPaths')
                                .appendNode('hudson.plugins.git.extensions.impl.SparseCheckoutPath')
                                .appendNode('path', sparseCheckoutPath)
                        def pathRestrictions = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.PathRestriction')
                        pathRestrictions.appendNode('includedRegions', "${sparseCheckoutPath}.*")
                        extensions {
                            extensions << sparseCheckout
                            extensions << pathRestrictions
                        }
                    }

                    extensions {
                        // "**" means: remove "origin/" prefix from the remote branch,
                        // so "origin/master" would become the local branch "master";
                        // "origin/feature/HC-1000" would become the local branch "feature/HC-1000".
                        localBranch('**')
                    }
                }
            }
        }
    }

}
