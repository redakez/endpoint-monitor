FROM maven:3.8.6-openjdk-11-slim AS build
COPY src app/src
COPY pom.xml app
RUN mvn -f app/pom.xml clean package

FROM openjdk:11-jre-slim
COPY --from=build app/target/endpoint-monitor-1.0.jar endpoint-monitor-1.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","endpoint-monitor-1.0.jar"]