FROM eclipse-temurin:17-jdk-alpine AS builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew bootJar -x test

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN addgroup -S spring && adduser -S spring -G spring \
  && chown spring:spring /app/app.jar
USER spring
ENTRYPOINT ["java", "-jar", "app.jar"]
