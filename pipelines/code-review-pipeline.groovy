properties (
        [
                parameters([
                        string(name: 'GIT_URL', defaultValue: 'git@github.com:HighLandner/edp-test-task-1.git', description: 'Git repo url'),
                        string(name: 'GIT_BRANCH', defaultValue: 'task-3', description: 'Review'),
                        string(name: 'GIT_CREDENTIALS', defaultValue: 'edp-private-key', description: 'Git repo credentials')
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

podTemplate(yaml: '''
    apiVersion: v1
    kind: Pod
    spec:
      containers:
      - name: agent
        image: dockerddddd12tfgqv/jenkins-custom:jdk-11-test
        command:
        - sleep
        args:
        - 99d
''') {

    node(POD_LABEL) {

        container('agent') {
            stage("checkout") {
                git branch: params.GIT_BRANCH,
                        credentialsId: params.GIT_CREDENTIALS,
                        url: params.GIT_URL
            }
            stage("hadolint-check") {
                sh 'hadolint Dockerfile'
            }
        }
    }
}
