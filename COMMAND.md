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