./gradlew build && docker-compose build && docker-compose up -d

./gradlew build --full-stacktrace --debug && docker-compose build && docker-compose up -d

./gradlew test --tests ProductCompositeServiceApplicationTests --info

./gradlew test --tests MessagingTests --info

docker logs loan-origination-system-product-composite-1
docker-compose exec mongodb mongosh product-db --quiet --eval "db.products.find()"
docker-compose exec mongodb mongosh rating-db --quiet --eval "db.ratings.find()"
docker-compose exec mysql mysql -uuser -p review-db -e "select * from reviews"