curl -H "Authorization: Bearer $ACCESS_TOKEN" -k -X POST https://localhost:8443/api/v1/applications \
-H "Content-Type: application/json" \
-d '{
"homeId": "550e8400-e29b-41d4-a716-446655440000",
"personal": {
"fullName": "John Doe",
"email": "john.doe@example.com",
"phone": "+1-555-0123"
},
"identity": {
"dob": "1990-01-01",
"ssn": "999-00-1234"
},
"request": {
"loanAmount": 350000.00,
"loanPurpose": "Home Purchase"
}
}'

# Check the applications table
docker exec -it easy-apply-app-db psql -U home-user-prod -d app-db -c "SELECT id, application_number, status FROM applications"
# Check if the credit report was generated
docker exec -it easy-apply-assessment-pg psql -U home-user-prod -d assessment-db -c "SELECT * FROM assessments"

docker exec -it easy-apply-notification-db psql -U home-user-prod -d notification-db -c "SELECT * FROM notifications"

# Check the outbox table for the trigger event
docker exec -it easy-apply-app-db psql -U home-user-prod -d app-db -c "SELECT type, aggregate_id FROM outbox ORDER BY created_at DESC LIMIT 1;"


# List all active connectors (should see app-service, credit-service, and notification-service connectors)
curl -s localhost:8083/connectors

# List all available topics
docker exec -it easy-apply-kafka \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--list
# Confirm topic details
docker exec -it easy-apply-kafka \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--describe \
--topic application.application_submitted
# Consume only the latest events
docker exec -it easy-apply-kafka \
/opt/kafka/bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic application.loan_application

# Consume messages from the topic
docker exec -it easy-apply-kafka \
/opt/kafka/bin/kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic application.loan_application \
--from-beginning


# Check the Kafka topic for the application event
docker exec -it easy-apply-kafka /usr/bin/kafka-console-consumer --bootstrap-server localhost:9092 --topic application.loan_application --from-beginning --max-messages 2

unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq -r .access_token)
echo $ACCESS_TOKEN

curl -X GET "https://localhost:8443/api/v1/applications?email=test@example.com" \
-H "Authorization: Bearer $ACCESS_TOKEN" \
-H "Accept: application/json"