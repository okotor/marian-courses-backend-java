# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and source code
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

# Grant execution rights to the Maven wrapper
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file to the container
RUN mkdir target/dependency && (cd target/dependency; jar -xf ../*.jar)
COPY target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]