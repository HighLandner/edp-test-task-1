# edp-test-task-2
1) <b>helm with input values</b><br>
    helm install --set *[variable]*=*[value]* *[chart name]*<br>
2) <b>helm usage benefits</b><br>
    - IaC - ability of helm charts to store reuseable application structure
    - keeping code of cluster resources deploy in helm charts separately (e.g. deployments of jenkins, sonar, etc)
    - versioning
3) <b>port-forwarding</b> except for service can be applied to deployments and pods directly:<br>
    - kubectl port-forward deployment/*[deployment name]* *[port]*
    - kubectl port-forward pod/*[pod name]* *[port]*