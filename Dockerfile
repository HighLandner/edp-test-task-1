# dockerddddd12tfgqv/custom-nginx:tagname
FROM nginx:1.23.1 as base
RUN apt-get update && apt-get upgrade -y
RUN tee "This is custom page!" /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
