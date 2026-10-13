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
# iptables: həmin uid-lərin şəbəkə çıxışını bağlayır (bax: docker-entrypoint.sh).
RUN apt-get update && apt-get install -y --no-install-recommends g++ util-linux iptables \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
# Windows-da CRLF ilə yazılıbsa belə skript işləsin; icra icazəsi də burada verilir.
RUN sed -i 's/\r$//' /usr/local/bin/docker-entrypoint.sh && chmod 755 /usr/local/bin/docker-entrypoint.sh
EXPOSE 8080
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
