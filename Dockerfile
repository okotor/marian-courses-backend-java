# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk AS build

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and source files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Grant execution rights to the Maven wrapper
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Use a minimal runtime image
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
