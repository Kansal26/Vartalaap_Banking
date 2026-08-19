# Stage 1: Build stage
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application (with memory limits for Render free tier)
ENV MAVEN_OPTS="-Xmx256m"
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the jar from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Create directories for data and uploads
RUN mkdir -p /app/data /app/uploads

# Expose the application port
EXPOSE 8080

# Run the application with memory constraints
# (Constructs a clean Spring Boot jdbc URL from Render DB components)
ENTRYPOINT ["sh", "-c", "if [ ! -z \"$DB_HOST\" ]; then export DB_URL=jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME; export DB_DRIVER=org.postgresql.Driver; export DB_DIALECT=org.hibernate.dialect.PostgreSQLDialect; fi && java -Xmx300m -jar app.jar"]
