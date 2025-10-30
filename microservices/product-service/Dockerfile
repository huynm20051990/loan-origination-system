# ======================
#  Stage 1: Builder
# ======================

# Use Eclipse Temurin (OpenJDK) base image with JRE 21 for the build stage
FROM eclipse-temurin:21-jre as builder
# Set the working directory inside the container to /extracted
WORKDIR extracted
# Copy the application JAR (built from Gradle or Maven) into the container and rename it as app.jar
# The wildcard *.jar allows any JAR file in ./build/libs/ to match
ADD ./build/libs/*.jar app.jar
# Use Spring Boot’s "layertools" feature to extract the JAR into multiple layers
# (dependencies, loader, snapshot-dependencies, application)
# This helps with Docker caching so that unchanged layers don’t rebuild every time
RUN java -Djarmode=layertools -jar app.jar extract

# ======================
#  Stage 2: Runtime
# ======================

# Use a fresh lightweight JRE base image for running the application
FROM eclipse-temurin:21-jre
# Set working directory to /application
WORKDIR application
# Copy each extracted layer from the builder stage into the runtime image
# These correspond to the directories created by layertools in Stage 1
# Common libraries (rarely change)
# Common libraries (rarely change)
COPY --from=builder extracted/dependencies/ ./
# Spring Boot loader classes
COPY --from=builder extracted/spring-boot-loader/ ./
# Snapshot dependencies (change more often)
COPY --from=builder extracted/snapshot-dependencies/ ./
# Your application code (changes most often)
COPY --from=builder extracted/application/ ./

# Expose port 8080 to the outside world
EXPOSE 8080

# Define the container startup command:
# Run the app using Spring Boot's JarLauncher (part of spring-boot-loader)
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]