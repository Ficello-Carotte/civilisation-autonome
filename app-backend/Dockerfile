FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
RUN apk add --no-cache maven

COPY pom.xml checkstyle.xml ./
RUN mvn dependency:go-offline -B

COPY src src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache wget \
    && adduser -D -u 1000 appuser
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# Stage dev : JDK + live reload (loader.path) + JFR. Build avec: docker build --target dev .
FROM eclipse-temurin:21-jdk-alpine AS dev
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# loader.path et JFR sont passés via command dans docker-compose.dev
ENTRYPOINT ["java", "-jar", "app.jar"]
