node {
    parameters {
        string(name: 'GIT_URL', defaultValue: '', description: 'Git repo url')
        string(name: 'GIT_BRANCH', defaultValue: 'task-3', description: 'Review')
        string(name: 'GIT_CREDENTIALS', defaultValue: 'git-creds', description: 'Git repo credentials')
        string(name: 'ENV', defaultValue: 'dev', description: 'Build environment')
    }

    stages {
        stage("test") {
            echo "test"
        }
        stage("checkout") {
            checkout scm: [
                    $class           : 'GitSCM',
                    userRemoteConfigs: [[url: params.GIT_URL , credentialsId: params.GIT_CREDENTIALS]],
                    branches         : [[name: "refs/heads/${params.GIT_BRANCH}"]],
            ], poll: false
        }
        stage("hadolint-check") {
            // hadolint Dockerfile
        }
    }
}