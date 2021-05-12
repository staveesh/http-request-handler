# Build stage
FROM maven:3.6.3-jdk-8-slim AS builder

MAINTAINER Taveesh Sharma <shrtav001@myuct.ac.za>

RUN mkdir -p /build
WORKDIR /build
COPY pom.xml /build
# Download all required dependencies into one layer
RUN mvn -B dependency:resolve dependency:resolve-plugins
# Copy source code
COPY src /build/src
RUN mvn package -DskipTests

# Run stage

FROM openjdk:8-jdk-alpine

COPY --from=builder /build/target/*.jar app.jar

EXPOSE 7800

ENTRYPOINT ["java","-jar","app.jar"]