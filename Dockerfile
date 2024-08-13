FROM openjdk:17-slim AS build

RUN apt-get update && apt-get install -y maven

COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

COPY --from=build /target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
