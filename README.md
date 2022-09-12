# edp-test-task-1   
<i>Deploy Keycloak, Jenkins, SonarQube using docker containers. Keycloak should work on port 8432. Jenkins should work on port 8332. SonarQube should work on port 9000</i>

First, deploy docker container with <b>postgres</b> and create database inside it for keycloak integration
```aidl
docker run -itd --name=postgres -e POSTGRES_PASSWORD=password postgres:14
docker exec -it postgres /bin/bash
psql -U postgres 
create database keycloak;
exit
```
Second, deploy docker container with <b>keycloak</b> itself, providing env file to it. Inside the container turn off TSL cert check
```aidl
docker run -itd --name=keycloak --env-file=.env.keycloak -p 8432:8080 -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=password jboss/keycloak:16.1.1
docker exec -it keycloak /bin/bash
/opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080/auth --realm master --user admin
/opt/keycloak/bin/kcadm.sh update realms/master -s sslRequired=NONE
```
Third, deploy <b>jenkins</b> container 
```aidl
docker run -itd --name=jenkins -p 8080:8080 jenkins:2.60.1
```
Fourth, deploy <b>sonarqube</b> container
```aidl
docker run -itd --name=sonar -p 9000:9000 sonarqube:9.6.1-community
```

