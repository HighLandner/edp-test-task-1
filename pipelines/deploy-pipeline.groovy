properties(
        [
                parameters([
                        string(name: 'GIT_URL', defaultValue: 'git@github.com:HighLandner/nginx-custom-chart.git', description: 'Git repo url'),
                        string(name: 'GIT_BRANCH', defaultValue: 'main', description: ''),
                        string(name: 'GIT_CREDENTIALS', defaultValue: 'edp-private-key', description: 'Git repo credentials'),
                        string(name: 'CHARTMUSEUM_CREDENTIALS', defaultValue: 'chartmuseum', description: 'Git repo credentials'),
                        string(name: 'NS', defaultValue: 'yb-onboarding', description: ''),
                        string(name: 'ENV', defaultValue: 'dev', description: 'Build environment'),
                        string(name: 'CHART_NAME', defaultValue: 'nginx-custom', description: ''),
                        string(name: 'CHART_URL', defaultValue: 'http://museum-chartmuseum:8080', description: '')
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
  - name: agent
    image: dockerddddd12tfgqv/jenkins-custom:jdk-11-test
    serviceAccountName: default
    tty: true
    command:
    - cat
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
            container("agent") {
                stage("chartmuseum-push") {
                    withCredentials([usernamePassword(credentialsId: 'chartmuseum', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh '''
                            helm repo add --username $USERNAME --password $PASSWORD chartmuseum ''' + CHART_URL + '''
                            helm repo update
                            helm list
                            helm package .
                            CHART_VERSION=`ls | grep ''' + CHART_NAME + '''*.tgz`
                            curl -L --user $USERNAME:$PASSWORD --data-binary "@$CHART_VERSION" ''' + CHART_URL + '''
                        '''
                    }
                }
                stage("chart-deploy") {
                    if (params.ENV == 'prod') {
                        REPLICA_COUNT = 3
                    } else {
                        REPLICA_COUNT = 1
                    }
                    NAMESPACE=params.NS + '-' + params.ENV
                    withCredentials([usernamePassword(credentialsId: 'chartmuseum', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh '''
                            helm repo add --username $USERNAME --password $PASSWORD nginx-custom ''' + CHART_URL + '''
                            helm repo update
                            helm search repo nginx-custom
                            helm upgrade --install test nginx-custom/nginx-custom --set replicaCount=''' + REPLICA_COUNT + ''' --set namespace=''' + NAMESPACE + ''' --set serviceAccount.name=nginx -n ''' + NAMESPACE + '''   
                        '''
                    }
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