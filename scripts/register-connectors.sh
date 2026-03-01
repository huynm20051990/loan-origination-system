#!/bin/sh

echo "Waiting for Kafka Connect to start..."
while [ $(curl -s -o /dev/null -w "%{http_code}" http://connect:8083/connectors) -ne 200 ]; do
  sleep 2
done

echo "Kafka Connect is up! Registering connectors via POST..."

# Function to register via POST (Deletes first so it doesn't error if already exists)
register_connector() {
  CONNECTOR_NAME=$1
  FILE_PATH=$2

  echo "Registering $CONNECTOR_NAME..."
  # Delete existing connector if it exists to avoid 409 Conflict
  curl -s -X DELETE http://connect:8083/connectors/$CONNECTOR_NAME

  # POST the full JSON file
  curl -i -X POST -H "Content-Type:application/json" \
    http://connect:8083/connectors \
    -d @$FILE_PATH
}

# Register each service
register_connector "app-service-connector" "/connectors/app-service-connector.json"
register_connector "assessment-service-connector" "/connectors/assessment-service-connector.json"
register_connector "notification-service-connector" "/connectors/notification-service-connector.json"

echo "All connectors registered successfully!"