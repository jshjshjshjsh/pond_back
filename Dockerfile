# Build stage
FROM gradle:8.5.0-jdk17-jammy AS build
WORKDIR /home/gradle/project
COPY --chown=gradle:gradle src build.gradle settings.gradle . .
RUN gradle build --no-daemon -x test

# Package stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /home/gradle/project/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
