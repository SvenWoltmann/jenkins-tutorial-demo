mavenJob('Jenkins Tutorial Demo - Application 1 (DSL)') {
    description 'Build job for Jenkins Tutorial / Application 1'

    logRotator {
        numToKeep 5
    }

    parameters {
        gitParam('Branch') {
            description 'The Git branch to checkout'
            type 'BRANCH'
            defaultValue 'origin/master'
        }
    }

    scm {
        git {
            remote {
                url 'git@gitlab.com:SvenWoltmann/jenkins-tutorial-demo.git'
            }

            branch '$Branch'

            // Add extensions 'SparseCheckoutPaths' and 'PathRestriction'
            def nodeBuilder = NodeBuilder.newInstance()
            def sparseCheckout = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.SparseCheckoutPaths')
            sparseCheckout.appendNode('sparseCheckoutPaths')
                    .appendNode('hudson.plugins.git.extensions.impl.SparseCheckoutPath')
                    .appendNode('path', 'application1/')
            def pathRestrictions = nodeBuilder.createNode('hudson.plugins.git.extensions.impl.PathRestriction')
            pathRestrictions.appendNode('includedRegions', 'application1/.*')
            extensions {
                extensions << sparseCheckout
                extensions << pathRestrictions
            }
        }
    }

    triggers {
        scm 'H/15 * * * *'
        snapshotDependencies true
    }

    rootPOM 'application1/pom.xml'
    goals 'clean install'
}
