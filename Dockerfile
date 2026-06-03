# Build stage
FROM openjdk:21-jdk-slim as builder
WORKDIR /app
COPY gradle/ gradle/
COPY gradlew .
COPY gradlew.bat .
COPY build.gradle .
COPY settings.gradle .
COPY src/ src/

RUN chmod +x gradlew && ./gradlew clean build -x test

# Runtime stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 9095
CMD ["java", "-jar", "app.jar"]