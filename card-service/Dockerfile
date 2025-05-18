# Stage 1: Build application dependencies
FROM maven:3.8.4-eclipse-temurin-17 AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

# Stage 2: Build the application
FROM maven:3.8.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
RUN mvn clean package -DskipTests

# Stage 3: Create the final image
FROM eclipse-temurin:17-jre AS final
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
