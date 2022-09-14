# edp-test-task-2
1) <b>helm with input values</b><br>
    helm install --set *[variable]*=*[value]* *[chart name]*<br>
2) <b>helm usage benefits</b><br>
    - IaC - ability of helm charts to store reuseable application structure
    - keeping code of cluster resources deploy in helm charts separately (e.g. deployments of jenkins, sonar, etc)
    - versioning
3) <b>port-forwarding</b> except for service can be applied to deployments and pods directly:<br>
```aidl
kubectl port-forward deployment/[deployment name] [port]
kubectl port-forward pod/[pod name] [port]
```
4) <b>Jenkins</b> StaticSet was deployed via helm chart:
```aidl
helm repo add jenkins https://charts.jenkins.io
helm repo update
helm repo list
helm upgrade --install jenkins jenkins/jenki
```