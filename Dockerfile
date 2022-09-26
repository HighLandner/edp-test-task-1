# dockerddddd12tfgqv/custom-nginx:tagname
FROM nginx:1.23.1 as base
RUN apt-get update && apt-get upgrade -y
RUN echo "This is custom page!" > /usr/share/nginx/html/index.html
EXPOSE 80:80
CMD ["nginx", "-g", "daemon off;"]

# USER jenkins
# COPY default-user.groovy /usr/share/jenkins/ref/init.groovy.d/
# COPY plugins.txt /usr/share/jenkins/ref/
# RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt