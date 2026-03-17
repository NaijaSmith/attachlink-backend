# Stage 1: Build the application
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app

# Copy maven wrapper and pom.xml first to leverage Docker cache
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Ensure the wrapper is executable
RUN chmod +x mvnw

# Download dependencies (this layer is cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 1. Create a non-root user and group
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 2. Create the upload directory and give ownership to the 'spring' user
# This fixes the "AccessDeniedException" you saw in the logs
RUN mkdir -p /app/upload-dir && chown -R spring:spring /app/upload-dir

# 3. Switch to the non-root user for security
USER spring:spring

# Copy the jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
