# Build stage
FROM maven:3.6.3-jdk-8 AS build

MAINTAINER Taveesh Sharma <shrtav001@myuct.ac.za>

COPY pom.xml /app/
COPY src /app/src
RUN mvn -f /app/pom.xml clean package

# Run stage

FROM openjdk:8-jdk-alpine

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 7800 7000

ENTRYPOINT ["java","-jar","/app/app.jar"]