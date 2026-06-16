# Étape 1 : build du jar avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

# Étape 2 : image finale légère basée sur Ubuntu
FROM ubuntu:22.04
RUN apt-get update && \
    apt-get install -y openjdk-21-jre-headless && \
    rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/target/habit-tracker-*.jar app.jar
EXPOSE 9090
CMD ["java", "-jar", "app.jar", "--server.port=9090"]