properties(
        [
                parameters([
                        string(name: 'GIT_URL', defaultValue: 'git@github.com:HighLandner/edp-test-task-1.git', description: 'Git repo url'),
                        string(name: 'GIT_BRANCH', defaultValue: 'task-3', description: 'Review'),
                        string(name: 'GIT_CREDENTIALS', defaultValue: 'edp-private-key', description: 'Git repo credentials'),
                        string(name: 'REGISTRY', defaultValue: 'dockerddddd12tfgqv', description: 'Custom docker registry'),
                        string(name: 'ENV', defaultValue: 'dev', description: 'Build environment')
                ]),

                [$class  : 'BuildDiscarderProperty',
                 strategy: [
                         $class               : 'LogRotator',
                         artifactDaysToKeepStr: '',
                         artifactNumToKeepStr : '',
                         daysToKeepStr        : '',
                         numToKeepStr         : '10'
                 ]
                ]
        ]
)

podTemplate(yaml: '''
kind: Pod
spec:
  containers:
  - name: git
    image: bitnami/git:latest
    command:
    - cat
    tty: true
  - name: kaniko
    image: gcr.io/kaniko-project/executor:debug
    imagePullPolicy: Always
    command:
    - sleep
    args:
    - 9999999
    volumeMounts:
      - name: jenkins-docker-cfg
        mountPath: /kaniko/.docker
  volumes:
  - name: jenkins-docker-cfg
    projected:
      sources:
      - secret:
          name: kaniko-secret
          items:
            - key: .dockerconfigjson
              path: config.json
''') {

    node(POD_LABEL) {
        try {
            container("git") {
                stage("checkout") {
                    git branch: params.GIT_BRANCH,
                            credentialsId: params.GIT_CREDENTIALS,
                            url: params.GIT_URL
                }
            }

            stage("version-tag") {
                container("git") {
                    IMAGE_TAG = sh(script: """cat version.txt""", returnStdout: true).trim() + "." + BUILD_NUMBER
                    if (params.ENV == 'prod') {
                        currentBuild.displayName = 'RC-' + IMAGE_TAG
                    } else {
                        currentBuild.displayName = 'SNAPSHOT-' + IMAGE_TAG
                    }
                }
            }

            stage("docker-build-push") {
                container(name: "kaniko", shell: "/busybox/sh") {
                    DOCKER_USER = params.REGISTRY
                    sh '''#!/busybox/sh
                              cat Dockerfile
                              /kaniko/executor --context `pwd` --destination ''' + DOCKER_USER + '''/nginx-custom:''' + IMAGE_TAG + '''
                          '''
                }
            }
        } catch (e) {
            echo 'Build failed ...'
            throw e
        } finally {
            deleteDir()
        }
    }
}