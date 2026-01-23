# Test Case 1 — Obtain Access Token

## Description
Retrieve an OAuth2 access token using the **client_credentials** grant type with scopes:

- `product:read`
- `product:write`

## Command
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN
```
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN
```

# Test Case 2 --- Create Product

## Description

Create a new product using the product-composite API endpoint.\
This test verifies that: - The access token works for authorized POST
requests. - The product is successfully created when valid JSON is sent.

## Command

``` bash
curl -X POST -k "https://minikube.me/product-composite" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "productId": 1234,
    "name": "Product Name A",
    "description": "Product description here",
    "ratings": [
      { "ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1" },
      { "ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2" },
      { "ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3" }
    ],
    "reviews": [
      { "reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1" },
      { "reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2" },
      { "reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3" }
    ]
  }'
```
``` bash
curl -X POST -k "https://localhost:8443/product-composite" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "productId": 1234,
    "name": "Product Name A",
    "description": "Product description here",
    "ratings": [
      { "ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1" },
      { "ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2" },
      { "ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3" }
    ],
    "reviews": [
      { "reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1" },
      { "reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2" },
      { "reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3" }
    ]
  }'
```

# Test Case 3 --- Retrieve Product

## Description

Retrieve the product created in Test Case 2.

## Command

``` bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  -k 'https://minikube.me/product-composite/1234' \
  -s | jq .
```

``` bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  -k 'https://localhost:8443/product-composite/1234' \
  -s | jq .
```

# Test Case 4 --- Delete Product

## Description

Delete product with ID 1234.

## Command

``` bash
curl -X DELETE \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -k https://minikube.me/product-composite/1234 \
  -w "%{http_code}\n" \
  -o /dev/null \
  -s
```
``` bash
curl -X DELETE \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -k https://localhost:8443/product-composite/1234 \
  -w "%{http_code}\n" \
  -o /dev/null \
  -s
```

# Test Case 5 — Discover The Log Records From Microservices

## Description
Go to Kibana -> Discover -> Add time, spring.level, \
kubernetes.namespace_name, kubernetes.container_name, spring.trace, log.\
Search product.id=1234

# Test Case 6 --- Performing root cause analyses

## Description

This test case retrieves a product by ID \
while intentionally triggering a 100% fault injection using the query parameter:
``` bash
faultPercent=100
```
This is typically used to test resilience, fallback logic, \
or circuit breaker behavior in a microservices system.
A valid OAuth2 access token is required.

## Command

``` bash
curl -H "Authorization: Bearer $ACCESS_TOKEN" \
  -k "https://minikube.me/product-composite/1234?faultPercent=100" \
  -s | jq .
```

## Expected Result
``` bash
{
  "timestamp": "2025-12-05T07:32:45.073+00:00",
  "path": "/product-composite/1234",
  "status": 500,
  "error": "Internal Server Error",
  "requestId": "9de9776d-1039",
  "message": "500 Internal Server Error from GET http://product/product/1234"
}
```

# Test Case 7 --- View Metrics using Kiali

## Description

1. Run below command.
2. Go to https://kiali.minikube.me.
3. Log in with admin/admin if required.
4. Workloads tab
5. Select the product-composite
6. Select the Outbound Metrics tab

## Command
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

siege https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" -c1 -d1 -v
```

# Test Case 8 --- View Metrics using Grafana

## Description

1. Run below command.
2. Go to https://grafana.minikube.me.
3. Home -> Dashboards -> Istio -> Istio Mesh Dashboard
4. Go back to Istio -> Istio Workload Dashboard
5. Select the loan-origination namespace -> product-composite workload -> Outbound Services tab

## Command
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

siege https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" -c1 -d1 -v
```

# Test Case 9 --- Importing existing Grafana dashboards

## Description

1. View reusable dashboard at https://grafana.com/grafana/dashboards.
2. We will try out a dashboard called JVM (Micrometer) - Kubernetes - Prometheus by Istio \
   that’s tailored to get a lot of valuable JVM-related metrics \
   from Spring Boot applications in a Kubernetes environment.
3. Import the dashboard named JVM (Micrometer) by following these steps:
   Home -> Dashboard -> New -> Import -> enter the dashboard ID 11955 \
   -> click Load -> Select Prometheus -> Click on the Import button. 
4. Inspect the JVM (Micrometer) dashboard by following these steps:
      On the dropdown to the right ->  select Last 5 minutes, and select a refresh rate of 5s \
      -> Application drop-down menu -> select the product-composite microservice \

# Test Case 10 --- Create a new panel for the circuit breaker metric

## Description

1. Create an empty dashboard: Dashboard -> New Dashboard -> Name Loan-Origination-System Dashboard
2. Creating a new panel for the circuit breaker metric
   Panel options -> Title: Circuit Breaker -> Tooltip mode to All -> Metric: resilience4j_circuitbreaker_state \
   -> Label to state equal to closed -> Verify raw query resilience4j_circuitbreaker_state{state="closed"} \
   -> Expand the Options tab, and in the Legend drop-down box, select Custom. In the Legend
   field, specify the value {{state}}. This will create a legend in the panel where the names of
   the different states are displayed. \
   -> Add new query for Open state - resilience4j_circuitbreaker_state{state="open"}
   -> Add new query for Open state - resilience4j_circuitbreaker_state{state="half_open"}
3. Testing the circuit breaker metrics 

## Command
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

curl -X POST -k https://minikube.me/product-composite   -H "Content-Type: application/json"   -H "Authorization: Bearer $ACCESS_TOKEN"   --data '{"productId":1234,"name":"Product name 1234","description": "New product 1234"}'

for ((n=0; n<4; n++)); do curl -o /dev/null -skL -w "%{http_code}\n" https://minikube.me/product-composite/1234?delay=3 -H "Authorization: Bearer $ACCESS_TOKEN" -s; done
```

# Test Case 11 --- Export and Import Dashboard in Grafana

## Description

1. Identify the uid of your dashboard. Ex: https://grafana.minikube.me/d/ff6ct90xjcydcb/loan-origination-system-dashboard
2. In a terminal window, create a variable with its value. ID=ff6ct90xjcydcb
3. Export the dashboard to a JSON file with the following command:
```bash
curl -sk https://grafana.minikube.me/api/dashboards/uid/$ID | jq '.dashboard.id=null' > "Hands-on-Dashboard.json"
```
4. Delete the dashboard.
5. Recreate the dashboard by importing it from the JSON file with the following command:
```bash
curl -i -XPOST -H 'Accept: application/json' -H 'Content-Type: application/json' -k \
  'https://grafana.minikube.me/api/dashboards/db' \
  -d @Hands-on-Dashboard.json
```
6. Verify that the imported dashboard

# Test Case 12 --- Deploying v1 and v2 versions of the microservices with routing to the v1 version

## Description
1. List all installed Helm releases
```bash
helm list -A
```
2. Uninstall the development environment
```bash
helm uninstall loan-origination-system-prod-env -n loan-origination-system
```
3. Check the termination of Pods
```bash
kubectl get pods -n loan-origination-system
```
4. Start MySQL, MongoDB, and RabbitMQ outside of Kubernetes
```bash
eval $(minikube docker-env) -- use docker host in minikube
docker-compose up -d mongodb mysql rabbitmq
```
5. Tag the Docker images with v1 and v2 versions
```bash
docker tag loan-origination-system/auth-server loan-origination-system/auth-server:v1
docker tag loan-origination-system/product-composite-service loan-origination-system/product-composite-service:v1
docker tag loan-origination-system/product-service loan-origination-system/product-service:v1
docker tag loan-origination-system/rating-service loan-origination-system/rating-service:v1
docker tag loan-origination-system/review-service loan-origination-system/review-service:v1
docker tag loan-origination-system/product-service loan-origination-system/product-service:v2
docker tag loan-origination-system/rating-service loan-origination-system/rating-service:v2
docker tag loan-origination-system/review-service loan-origination-system/review-service:v2
```
6. Deploy the system landscape using Helm
```bash
kubectl delete namespace loan-origination-system
kubectl apply -f kubernetes/loan-origination-system.yml
kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system
for f in kubernetes/helm/components/*; do helm dep up $f; done
for f in kubernetes/helm/environments/*; do helm dep up $f; done
helm install loan-origination-system-prod-env \
  kubernetes/helm/environments/prod-env \
  -n loan-origination-system --wait
  
kubectl get virtualservice -A
```
```bash
kubectl delete namespace loan-origination-system
kubectl apply -f kubernetes/loan-origination-system.yml
kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system
for f in kubernetes/helm/components/*; do helm dep up $f; done
for f in kubernetes/helm/environments/*; do helm dep up $f; done
helm install loan-origination-system-dev-env \
kubernetes/helm/environments/dev-env \
-n loan-origination-system \
--wait
```
7. Verify that we have v1 and v2 Pods up and running
```bash
kubectl get pods -n loan-origination-system
```
```bash
kubectl describe pod product-v2-78d4fc4449-jb6v5 -n loan-origination-system
```
```bash
kubectl logs product-v2-78d4fc4449-jb6v5 -n loan-origination-system
```
8. Remove soft link if incorrect
```bash
ls -l ~/loan-origination-system/kubernetes/helm/components/product-green/config-repo

rm ~/loan-origination-system/kubernetes/helm/components/product-green/config-repo/application.yml
```
9. Create soft link if required
```bash
ln -s ../../../../../config-repo/application.yml config-repo/application.yml
ln -s ../../../../../config-repo/product.yml config-repo/product.yml
ln -s ../../../../../config-repo/application.yml config-repo/application.yml
ln -s ../../../../../config-repo/rating.yml config-repo/rating.yml
ln -s ../../../../../config-repo/application.yml config-repo/application.yml
ln -s ../../../../../config-repo/review.yml config-repo/review.yml
```
10. Verifying that all traffic initially goes to the v1 version of the microservices
Get a new access token
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

siege https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" -c1 -d1 -v
```

11. Create new product
``` bash
curl -X POST -k "https://minikube.me/product-composite" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{
    "productId": 1,
    "name": "Product Name A",
    "description": "Product description here",
    "ratings": [
      { "ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1" },
      { "ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2" },
      { "ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3" }
    ],
    "reviews": [
      { "reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1" },
      { "reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2" },
      { "reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3" }
    ]
  }'
```
12. Query DB
```bash
docker exec -it loan-origination-system-mysql-1 \
  mysql -u mysql-user-prod -pmysql-pwd-prod
  
show databases;

USE review-db;

SHOW TABLES;

select * from reviews;

docker exec -it loan-origination-system-mongodb-1 \
  mongosh -u mongodb-user-prod -p mongodb-pwd-prod --authenticationDatabase admin

show dbs
use yourDatabaseName
show collections
db.products.find().pretty()

```
# Test Case 13 --- Running canary tests

## Description
1. Perform a normal request to verify that the request is routed to the v1 version of the microservices
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

curl -ks https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" | jq .serviceAddresses
```
Expected result
```bash
{
  "cmp": "product-composite-57759cccd8-ndcfk/10.244.2.51:80",
  "pro": "product-v1-6988949db5-6msl2/10.244.2.58:80",
  "rat": "rating-v1-b77c97997-bltj6/10.244.2.40:80",
  "rev": "review-v1-6597596465-nt2br/10.244.2.47:80"
}
```
2. Add the X-group=test header, we expect the request to be served by v2 versions of the core microservices.
```bash
curl -ks https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" -H "X-group: test" | jq .serviceAddresses
```
Expected result
```bash
{
  "cmp": "product-composite-57759cccd8-ndcfk/10.244.2.51:80",
  "pro": "product-v2-78d4fc4449-52hb2/10.244.2.61:80",
  "rat": "rating-v2-6bb946b555-b98dh/10.244.2.45:80",
  "rev": "review-v2-8554869dcf-2sf8k/10.244.2.44:80"
}
```

# Test Case 14 --- Running a blue-green deployment

## Description
1. Get the state of the virtual service of the product microservice
```bash
kubectl get vs product -o yaml
```
2. A sample patch command that changes the weight 
distribution of the routing to the v1 and v2 Pods in the review microservice
```bash
kubectl patch virtualservice review --type=json -p='[
  {"op": "add", "path": "/spec/http/1/route/0/weight", "value": 80},
  {"op": "add", "path": "/spec/http/1/route/1/weight", "value": 20}
]
```
3. Verifying that all traffic initially goes to the v1 version of the microservices
    Get a new access token
```bash
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@minikube.me/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

# Load test
siege https://minikube.me/product-composite/1 -H "Authorization: Bearer $ACCESS_TOKEN" -c1 -d1 -v
```
4. Allow 20% of users to be routed to the new v2 version
```bash
chmod +x kubernetes/routing-tests/split-traffic-between-old-and-new-services.bash

kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system

./kubernetes/routing-tests/split-traffic-between-old-and-new-services.bash 80 20
```

5. Allow 100% of users to be routed to the new v2 version
```bash
./kubernetes/routing-tests/split-traffic-between-old-and-new-services.bash 0 100
```

6. If something goes terribly wrong following the upgrade to v2, revert all traffic to the v1 version
```bash
./kubernetes/routing-tests/split-traffic-between-old-and-new-services.bash 100 0
```

# Test Case 15 --- Tests with Docker Compose

## Description
1. Run test automatically
```bash
USE_K8S=false HOST=localhost PORT=8443 HEALTH_URL=https://localhost:8443 ./test-em-all.bash start stop
```
2. Run test manually
```bash
docker compose down -v
# Use RabbitMQ with partitions
# export COMPOSE_FILE=docker-compose-partitions.yml
# unset COMPOSE_FILE
docker-compose build && docker-compose up -d
docker ps --format "{{.Names}}"
curl -k https://localhost:8443/actuator/health | jq -r .status

ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)

echo ACCESS_TOKEN=$ACCESS_TOKEN

HOST=localhost
PORT=8443
composite='{
  "productId": 1,
  "name": "Product Name A",
  "description": "Product description here",
  "ratings": [
    { "ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1" },
    { "ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2" },
    { "ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3" }
  ],
  "reviews": [
    { "reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1" },
    { "reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2" },
    { "reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3" }
  ]
}'

curl -X POST -s -k \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  --data "$composite" \
  "https://$HOST:$PORT/product-composite" \
  -w "%{http_code}\n"

curl -X GET -s -k \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://localhost:8443/product-composite/1" \
  -w "\n%{http_code}\n"
  
curl -X DELETE -s -k \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://$HOST:$PORT/product-composite/1" \
  -w "%{http_code}\n"

composite2='{
  "productId": 2,
  "name": "Product Name B",
  "description": "Product description here",
  "ratings": [
    { "ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1" },
    { "ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2" },
    { "ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3" }
  ],
  "reviews": [
    { "reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1" },
    { "reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2" },
    { "reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3" }
  ]
}'

curl -X POST -s -k \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  --data "$composite2" \
  "https://$HOST:$PORT/product-composite" \
  -w "%{http_code}\n"

curl -X GET -s -k \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://localhost:8443/product-composite/2" \
  -w "\n%{http_code}\n"
  
curl -X DELETE -s -k \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  "https://$HOST:$PORT/product-composite/2" \
  -w "%{http_code}\n"
  
docker exec -it loan-origination-system-mysql-1 \
  mysql -u mysql-user-prod -pmysql-pwd-prod
  
show databases;

USE review-db;

SHOW TABLES;

select * from reviews;

docker exec -it loan-origination-system-mongodb-1 \
  mongosh -u mongodb-user-prod -p mongodb-pwd-prod --authenticationDatabase admin

show dbs
use yourDatabaseName
show collections
db.products.find().pretty()
  
# Chage below
# management.endpoint.health.group.readiness.include: readinessState, kafka
# Use Kafka with two partitions per topic
# export COMPOSE_FILE=docker-compose-kafka.yml
# docker-compose build && docker-compose up -d
# Repeat the tests from the previous section: create two products, one with the product ID set to 1 and
# one with the product ID set to 2

# See a list of topics
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

# See the partitions in a specific topic
docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic products

# See all the messages in a specific partition
docker-compose exec kafka kafka-console-consumer \
--bootstrap-server localhost:9092 \
--topic products --from-beginning \
--timeout-ms 1000 --partition 1

```

# Test Case 16 --- Test Circuit Breaker
```bash
./gradlew build && docker-compose build
USE_K8S=false HOST=localhost PORT=8443 HEALTH_URL=https://localhost:8443 ./test-em-all.bash start stop
unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token \
  -d grant_type=client_credentials \
  -d scope="product:read product:write" \
  -s | jq .access_token -r)
echo ACCESS_TOKEN=$ACCESS_TOKEN

curl -H "Authorization: Bearer $ACCESS_TOKEN" \
-k https://localhost:8443/product-composite/1 \
-w "%{http_code}\n" -o /dev/null -s

# Verify that the circuit breaker is closed
docker-compose exec product-composite curl \
-s http://product-composite:4004/actuator/health | jq \
-r .components.circuitBreakers.details.product.details.state

kubectl exec -n loan-origination-system \
  product-composite-57759cccd8-jd92k \
  -c product-composite -- \
  curl -s http://localhost:4004/actuator/health | jq \
  -r '.components.circuitBreakers.details.product.details.state'


# Force the circuit breaker to open
# Call the API three times and direct the product service to cause a timeout on every call, 
# that is, delay the response by 3 seconds
curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1?delay=3 -s | jq .

for i in {1..3}; do
  curl -H "Authorization: Bearer $ACCESS_TOKEN" \
    -k "https://minikube.me/product-composite/1?delay=3" -s | jq .
done

# Wait 10 seconds for the circuit breaker to transition to half-open, 
sleep 10
# and then run the following command to verify that the circuit is now in a half-open state
docker-compose exec product-composite curl -s http://product-composite:4004/actuator/health | jq \
-r .components.circuitBreakers.details.product.details.state

kubectl exec -n loan-origination-system \
  product-composite-57759cccd8-jd92k \
  -c product-composite -- \
  curl -s http://localhost:4004/actuator/health | jq \
  -r '.components.circuitBreakers.details.product.details.state'


# Close the circuit breaker again
# Submit three normal requests
curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1 -w "%{http_code}\n" -o /dev/null -s

for i in {1..3}; do
  curl -H "Authorization: Bearer $ACCESS_TOKEN" \
    -k "https://minikube.me/product-composite/1" \
    -w "%{http_code}\n" -o /dev/null -s
done

# Verify that the circuit breaker is closed
docker-compose exec product-composite curl \
-s http://product-composite:4004/actuator/health | jq \
-r .components.circuitBreakers.details.product.details.state

kubectl exec -n loan-origination-system \
  product-composite-57759cccd8-jd92k \
  -c product-composite -- \
  curl -s http://localhost:4004/actuator/health | jq \
  -r '.components.circuitBreakers.details.product.details.state'


# List the last three state transitions
docker-compose exec product-composite curl -s http://product-composite:4004/actuator/circuitbreakerevents/product/STATE_TRANSITION | jq \
-r '.circuitBreakerEvents[-3].stateTransition, .circuitBreakerEvents[-2].stateTransition, .circuitBreakerEvents[-1].stateTransition'

kubectl exec -n loan-origination-system \
  product-composite-57759cccd8-jd92k \
  -c product-composite -- \
  curl -s http://localhost:4004/actuator/circuitbreakerevents/product/STATE_TRANSITION | jq \
  -r '.circuitBreakerEvents[-3].stateTransition,
      .circuitBreakerEvents[-2].stateTransition,
      .circuitBreakerEvents[-1].stateTransition'


```

# Test Case 17 --- Test Retry
```bash
# Force a random error to occur
# Run the following command a couple of times
time curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1?faultPercent=25 \
-w "%{http_code}\n" -o /dev/null -s

time curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://minikube.me/product-composite/1?faultPercent=25 \
-w "%{http_code}\n" -o /dev/null -s

# After we have noticed a response time of 1 second, indicating that the request required one retry to succeed, 
# Run the following command to see the last two retry events
# We should be able to see the failed request and the next successful attempt
docker-compose exec product-composite curl -s http://product-composite:4004/actuator/retryevents | \
jq '.retryEvents[-2], .retryEvents[-1]'

kubectl -n loan-origination-system exec product-composite-57759cccd8-jd92k -c product-composite -- \
curl -s http://localhost:4004/actuator/retryevents | \
jq '.retryEvents[-2], .retryEvents[-1]'

```






