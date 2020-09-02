FROM openjdk:8-jdk-alpine

MAINTAINER Taveesh Sharma <shrtav001@myuct.ac.za>

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 7800 7000

ENTRYPOINT ["java","-jar","/app.jar"]