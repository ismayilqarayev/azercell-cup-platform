# --- Build stage ---
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

# --- Run stage ---
FROM eclipse-temurin:21-jre
# util-linux: şagird kodunu başqa istifadəçi ilə və limitlərlə işə salan prlimit/setpriv (bax: JudgeSandbox).
RUN apt-get update && apt-get install -y --no-install-recommends g++ util-linux \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
