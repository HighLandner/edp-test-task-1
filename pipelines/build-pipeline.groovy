properties (
        [
                parameters([
                        string(name: 'GIT_URL', defaultValue: 'git@github.com:HighLandner/edp-test-task-1.git', description: 'Git repo url'),
                        string(name: 'GIT_BRANCH', defaultValue: 'task-3', description: 'Review'),
                        string(name: 'GIT_CREDENTIALS', defaultValue: 'edp-private-key', description: 'Git repo credentials'),
                        string(name: 'REGISTRY', defaultValue: 'dockerddddd12tfgqv', description: 'Custom docker registry'),
                        string(name: 'REGISTRY_CREDENTIALS', defaultValue: '', description: 'Docker registry token'),
                        string(name: 'ENV', defaultValue: 'dev', description: 'Build environment')
                ]),

                [$class: 'BuildDiscarderProperty',
                 strategy: [
                         $class: 'LogRotator',
                         artifactDaysToKeepStr: '',
                         artifactNumToKeepStr: '',
                         daysToKeepStr: '',
                         numToKeepStr: '10'
                 ]
                ]
        ]
)

node("jenkins-jenkins-agent") {
    try {
        stage("checkout") {
            git branch: params.GIT_BRANCH,
                    credentialsId: params.GIT_CREDENTIALS,
                    url: params.GIT_URL
        }

        stage("version-tag") {
            def IMAGE_TAG = sh(script:"""cat version.txt""", returnStdout: true) + "." + BUILD_NUMBER
            if (params.ENV == 'prod') {
                currentBuild.displayName = 'RC-' + IMAGE_TAG
            } else {
                currentBuild.displayName = 'SNAPSHOT-' + IMAGE_TAG
            }
        }

        stage("docker-build") {
            def dockerImage = docker.build '${REGISTRY}/custom-jenkins:${IMAGE_TAG}'
        }
        stage("docker-push") {
            docker.withRegistry( '', credentials(params.REGISTRY_CREDENTIALS) ) {
                dockerImage.push()
            }
        }
    } catch (e) {
        echo 'Build failed ...'
        throw e
    } finally {
        deleteDir()
    }
}