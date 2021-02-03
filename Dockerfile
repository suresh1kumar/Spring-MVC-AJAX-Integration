# For Java 11, try this
FROM adoptopenjdk/openjdk11:alpine-jre

WORKDIR /microservices/userManagement

EXPOSE 9091

COPY target/UserManagement-0.0.1-SNAPSHOT.jar /microservices/userManagement
COPY target/application.properties /microservices/userManagement
COPY target/application-uat.properties /microservices/userManagement
COPY target/application-prod.properties /microservices/userManagement
COPY target/log4j2.xml /microservices/userManagement
RUN mkdir -p /microservices/userManagement/logs

ENTRYPOINT ["java","-Dspring.profiles.active=uat -Dlogging.config=/microservices/userManagement/log4j2.xml","-jar","UserManagement-0.0.1-SNAPSHOT.jar"]