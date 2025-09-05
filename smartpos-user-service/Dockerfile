# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Build args for private repo credentials
ARG MVN_REPO_USER
ARG MVN_REPO_PASS

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Generate Maven settings.xml with secrets
RUN envsubst < settings.xml.template > ${maven.home}/conf/settings.xml

# Build the Spring Boot JAR
RUN mvn -s /root/.m2/settings.xml clean package -DskipTests

# ---- Runtime stage ----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]
