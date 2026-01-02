# ============================================
# Stage 1: Build
# ============================================
FROM gradle:8.14-jdk21 AS build

WORKDIR /app

# Copy build files first (for better caching)
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies (cached layer) - use pre-installed gradle, no wrapper download needed
RUN gradle dependencies --no-daemon || true

# Copy source code
COPY src/ src/

# Build the application
RUN gradle bootJar --no-daemon -x test

# ============================================
# Stage 2: Runtime
# ============================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appgroup /app

USER appuser

# Expose port
EXPOSE 9901

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:9901/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
