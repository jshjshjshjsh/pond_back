# Build stage
FROM gradle:8.5.0-jdk17-jammy AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle src build.gradle settings.gradle . .
RUN gradle build --no-daemon -x test

RUN apt-get update && apt-get install -y wget && \
    wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar


# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
COPY --from=build /home/gradle/project/opentelemetry-javaagent.jar .
ENTRYPOINT ["java", "-jar", "app.jar"]
ENTRYPOINT ["java", "-javaagent:opentelemetry-javaagent.jar", "-jar", "app.jar"]
