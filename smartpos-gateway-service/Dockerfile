# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the project’s jar file into the container at /app
COPY target/gateway-service-0.0.1-SNAPSHOT.jar /app/gateway-service.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
