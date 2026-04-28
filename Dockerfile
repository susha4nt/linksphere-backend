# Use Java 17
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy project files
COPY . .

# Give permission to mvnw (important for Linux)
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package -DskipTests

# Expose port (Render uses PORT env)
EXPOSE 10000

# Run the app
CMD ["sh", "-c", "java -jar target/*.jar"]