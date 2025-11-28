#!/bin/bash
# =============================================
# Automate Podman pod & Spring Boot app
# =============================================

APP_NAME="surest-app"
POD_NAME="surest-pod"
JAR_NAME="app.jar"

echo "🔨 Step 1: Build Spring Boot app"
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ Gradle build failed"
    exit 1
fi

# Copy the jar to generic name
cp build/libs/*.jar $JAR_NAME

echo "🐳 Step 2: Build Podman image"
podman build -t $APP_NAME .

if [ $? -ne 0 ]; then
    echo "❌ Podman image build failed"
    exit 1
fi

# Check if pod exists, if yes, delete it
if podman pod exists $POD_NAME; then
    echo "♻️ Removing existing pod..."
    podman pod rm -f $POD_NAME
fi

echo "🚀 Step 3: Create Podman pod"
podman pod create --name $POD_NAME -p 8080:8080

echo "🟢 Step 4: Run Spring Boot container in pod"
podman run -d \
  --pod $POD_NAME \
  --name $APP_NAME \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.containers.internal:5432/surest \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  $APP_NAME

echo "✅ Podman setup complete! Spring Boot app is running at http://localhost:8080"
podman ps
