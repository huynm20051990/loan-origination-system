curl -H "Authorization: Bearer $ACCESS_TOKEN" -k -X POST https://localhost:8443/api/v1/applications \
-H "Content-Type: application/json" \
-d '{
"homeId": "550e8400-e29b-41d4-a716-446655440000",
"personal": {
"fullName": "John Doe",
"email": "john.doe@example.com",
"phone": "+1-555-0123",
"ssn": "999-00-1234"
},
"financial": {
"annualIncome": 85000.00,
"employer": "Tech Solutions Inc"
},
"request": {
"loanAmount": 350000.00,
"loanPurpose": "Home Purchase"
}
}'
