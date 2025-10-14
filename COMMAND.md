./gradlew build && docker-compose build && docker-compose up -d

./gradlew build --full-stacktrace --info && docker-compose build && docker-compose up -d

./gradlew test --tests ProductCompositeServiceApplicationTests --info

./gradlew test --tests RatingServiceApplicationTests --info

./gradlew test --tests MessagingTests --info

docker logs loan-origination-system-product-composite-1
docker-compose exec mongodb mongosh product-db --quiet --eval "db.products.find()"
docker-compose exec mongodb mongosh rating-db --quiet --eval "db.ratings.find()"
docker-compose exec mysql mysql -uuser -p review-db -e "select * from reviews"

curl -s localhost:8080/actuator/health | jq -r .status

body='{
"productId": 1,
"name": "product name C",
"description": "Product description here",
"ratings": [
{"ratingId": 1, "author": "author 1", "rate": 1, "content": "content 1"},
{"ratingId": 2, "author": "author 2", "rate": 2, "content": "content 2"},
{"ratingId": 3, "author": "author 3", "rate": 3, "content": "content 3"}
],
"reviews": [
{"reviewId": 1, "author": "author 1", "subject": "subject 1", "content": "content 1"},
{"reviewId": 2, "author": "author 2", "subject": "subject 2", "content": "content 2"},
{"reviewId": 3, "author": "author 3", "subject": "subject 3", "content": "content 3"}
]
}'
curl -X POST localhost:8080/product-composite \
-H "Content-Type: application/json" \
--data "$body"

body='{
"productId": 2,
"name": "product name D",
"description": "Another product description",
"ratings": [
{"ratingId": 4, "author": "author 4", "rate": 4, "content": "content 4"},
{"ratingId": 5, "author": "author 5", "rate": 5, "content": "content 5"}
],
"reviews": [
{"reviewId": 4, "author": "author 4", "subject": "subject 4", "content": "content 4"},
{"reviewId": 5, "author": "author 5", "subject": "subject 5", "content": "content 5"}
]
}'
curl -X POST localhost:8080/product-composite \
-H "Content-Type: application/json" \
--data "$body"

curl -s localhost:8080/product-composite/1 | jq

curl -s localhost:8080/product-composite/2 | jq

curl -X DELETE localhost:8080/product-composite/1

curl -X DELETE localhost:8080/product-composite/2

export COMPOSE_FILE=docker-compose-kafka.yml
docker-compose build && docker-compose up -d

docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list

docker-compose exec kafka kafka-topics --bootstrap-server localhost:9092 --describe --topic products

docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000 --partition 0
docker-compose exec kafka kafka-console-consumer --bootstrap-server localhost:9092 --topic products --from-beginning --timeout-ms 1000 --partition 1

unset COMPOSE_FILE

unset COMPOSE_FILE
./test-em-all.bash start stop

export COMPOSE_FILE=docker-compose-partitions.yml
./test-em-all.bash start stop
unset COMPOSE_FILE

export COMPOSE_FILE=docker-compose-kafka.yml
./test-em-all.bash start stop
unset COMPOSE_FILE

docker-compose up -d --scale review=3
docker-compose logs review | grep Started
curl -H "accept:application/json" localhost:8761/eureka/apps -s | jq -r .applications.application[].instance[].instanceId
curl localhost:8080/product-composite/1 -s | jq -r .serviceAddresses.rev
docker-compose logs review | grep "Response size"
docker-compose up -d --scale review=2
curl localhost:8080/product-composite/1 -m 2

docker-compose up -d --scale review=2 --scale eureka=0
curl localhost:8080/product-composite/1 -s | jq -r .serviceAddresses.rev
docker-compose up -d --scale review=2 --scale eureka=0 --scale product=2
curl localhost:8080/product-composite/1 -s | jq -r .serviceAddresses.pro
docker-compose up -d --scale review=1 --scale eureka=1 --scale product=2
curl localhost:8080/product-composite/1 -s | jq -r .serviceAddresses

docker-compose ps gateway eureka product-composite product rating review

curl localhost:8080/actuator/gateway/routes -s | jq '.[] | {"\(.route_id)": "\(.uri)"}' | grep -v '{\|}'
docker-compose logs -f --tail=0 gateway
curl http://localhost:8080/product-composite/1
curl -H "accept:application/json" \localhost:8080/eureka/api/apps -s | \jq -r .applications.application[].instance[].instanceId
curl http://localhost:8080/headerrouting -H "Host: i.feel.lucky:8080"
curl http://localhost:8080/headerrouting -H "Host: im.a.teapot:8080"
curl http://localhost:8080/headerrouting

curl -H "accept:application/json" https://u:p@localhost:8443/eureka/api/apps -ks | jq -r .applications.application[].instance[].instanceId

curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq .

curl -k https://reader:secret-reader@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read" -s | jq .

curl https://dev-usr:dev-pwd@localhost:8443/config/product/docker -ks | jq .

curl -k https://dev-usr:dev-pwd@localhost:8443/config/encrypt --data urlencode "hello world"

curl -k https://dev-usr:dev-pwd@localhost:8443/config/decrypt -d d91001603dcdf3eb1392ccbd40ff201cdcf7b9af2fcaab3da39e37919033b206

unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq -r .access_token)
echo $ACCESS_TOKEN

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1 -w "%{http_code}\n" -o /dev/null -s

docker-compose exec product-composite curl -s http://product-composite:8080/actuator/health | jq -r .components.circuitBreakers.details.product.details.state

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1?delay=3 -s | jq .

docker-compose exec product-composite curl -s http://product-composite:8080/actuator/health | jq -r .components.circuitBreakers.details.product.details.state

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1 -w "%{http_code}\n" -o /dev/null -s

docker-compose exec product-composite curl -s http://product-composite:8080/actuator/health | jq -r .components.circuitBreakers.details.product.details.state

docker-compose exec product-composite curl -s http://product-composite:8080/actuator/circuitbreakerevents/product/STATE_TRANSITION \
| jq -r '.circuitBreakerEvents[-3].stateTransition, .circuitBreakerEvents[-2].stateTransition, .circuitBreakerEvents[-1].stateTransition'

time curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1?faultPercent=25 -w "%{http_code}\n" -o /dev/null -s

docker-compose exec product-composite curl -s http://product-composite:8080/actuator/retryevents | jq '.retryEvents[-2], .retryEvents[-1]'

unset ACCESS_TOKEN
ACCESS_TOKEN=$(curl -k https://writer:secret-writer@localhost:8443/oauth2/token -d grant_type=client_credentials -d scope="product:read product:write" -s | jq -r .access_token)
echo $ACCESS_TOKEN

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1 -w "%{http_code}\n" -o /dev/null -s

docker-compose restart product-composite

docker-compose logs -f --tail 0 product-composite

curl -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/1?delay=3 -w "%{http_code}\n" -o /dev/null -s

curl -X DELETE -H "Authorization: Bearer $ACCESS_TOKEN" -k https://localhost:8443/product-composite/12345 -w "%{http_code}\n" -o /dev/null -s

unset KUBECONFIG
minikube start \
--profile=loan-origination-system \
--memory=6144 \
--cpus=4 \
--disk-size=30g \
--kubernetes-version=v1.31.0 \
--driver=docker \
--ports=8080:80 --ports=8443:443 \
--ports=30080:30080 --ports=30443:30443
minikube profile loan-origination-system
minikube addons enable ingress
minikube addons enable metrics-server

kubectl get nodes
kubectl get pods --all-namespaces

kubectl create namespace first-attempts
kubectl config set-context $(kubectl config current-context) --namespace=first attempts

kubectl apply -f kubernetes/first-attempts/nginx-deployment.yaml

kubectl get pod --watch
kubectl delete pod --selector app=nginx-app

kubectl apply -f kubernetes/first-attempts/nginx-service.yaml
kubectl get svc

kubectl run -i --rm --restart=Never curl-client --image=curlimages/curl --command -- curl -s 'http://nginx-service:80'

kubectl delete namespace first-attempts

minikube stop
minikube start
minikube delete --profile loan-origination-system

helm dependency update .
helm template . -s templates/configmap_from_file.yaml

for f in components/*; do helm dependency update $f; done
helm dependency update environments/dev-env
helm template environments/dev-env -s templates/secrets.yaml

helm dependency update components/product
helm template components/product -s templates/service.yaml

helm dependency update components/gateway
helm template components/gateway -s templates/service.yaml

helm dependency update components/product
helm template components/product -s templates/deployment.yaml

helm dependency update components/mongodb
helm template components/mongodb -s templates/deployment.yaml

./gradlew build
eval $(minikube docker-env)
docker-compose build

for f in kubernetes/helm/components/*; do helm dep up $f; done
for f in kubernetes/helm/environments/*; do helm dep up $f; done
helm dep ls kubernetes/helm/environments/dev-env/

eval $(minikube docker-env)
docker pull mysql:8.4.0
docker pull mongo:7.0.9
docker pull rabbitmq:3.13.7-management
docker pull openzipkin/zipkin:3.5.1

helm template kubernetes/helm/environments/dev-env

minikube start --profile=loan-origination-system

helm install --dry-run --debug loan-origination-system-dev-env \
kubernetes/helm/environments/dev-env

helm install loan-origination-system-dev-env \
kubernetes/helm/environments/dev-env \
-n loan-origination-system \
--create-namespace

kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system

kubectl get pods --watch

kubectl wait --timeout=600s --for=condition=ready pod --all

kubectl get pods -o json | jq .items[].spec.containers[].image

kubectl delete namespace loan-origination-system

eval $(minikube docker-env)
docker-compose down
eval $(minikube docker-env -u)

docker run -it --rm loan-origination-system/product-service:latest

helm repo add jetstack https://charts.jetstack.io

helm repo update

helm install cert-manager jetstack/cert-manager \
--create-namespace \
--namespace cert-manager \
--version v1.11.0 \
--set installCRDs=true \
--wait

kubectl get pods --namespace cert-manager

sudo bash -c "echo 127.0.0.1 minikube.me | tee -a /etc/hosts"

eval $(minikube docker-env -u)
./gradlew build
eval $(minikube docker-env)
docker-compose build

for f in kubernetes/helm/components/*; do helm dep up $f; done

kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system

helm list -n loan-origination-system
helm uninstall loan-origination-system-dev-env -n loan-origination-system

helm install loan-origination-system-dev-env \
kubernetes/helm/environments/dev-env \
-n loan-origination-system \
--create-namespace \
--wait

kubectl config set-context $(kubectl config current-context) --namespace=loan-origination-system

kubectl get deploy rabbitmq -o yaml | grep -A10 readinessProbe