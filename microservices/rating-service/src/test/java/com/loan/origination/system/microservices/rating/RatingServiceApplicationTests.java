package com.loan.origination.system.microservices.rating;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.microservices.rating.repository.RatingRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RatingServiceApplicationTests extends MongoDbTestBase {

  @Autowired private WebTestClient client;

  @Autowired private RatingRepository repository;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
  }

  @Test
  void getRatingsByProductId() {
    int productId = 1;

    postAndVerifyRating(productId, 1, HttpStatus.OK);
    postAndVerifyRating(productId, 2, HttpStatus.OK);
    postAndVerifyRating(productId, 3, HttpStatus.OK);

    Assertions.assertEquals(3, repository.findByProductId(productId).size());

    getAndVerifyRatingsByProductId(productId, HttpStatus.OK)
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[2].productId")
        .isEqualTo(productId)
        .jsonPath("$[2].recommendationId")
        .isEqualTo(3);
  }

  @Test
  void duplicateError() {

    int productId = 1;
    int recommendationId = 1;

    postAndVerifyRating(productId, recommendationId, HttpStatus.OK)
        .jsonPath("$.productId")
        .isEqualTo(productId)
        .jsonPath("$.recommendationId")
        .isEqualTo(recommendationId);

    Assertions.assertEquals(1, repository.count());

    postAndVerifyRating(productId, recommendationId, HttpStatus.UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/recommendation")
        .jsonPath("$.message")
        .isEqualTo("Duplicate key, Product Id: 1, Recommendation Id:1");

    Assertions.assertEquals(1, repository.count());
  }

  @Test
  void deleteRatings() {

    int productId = 1;
    int recommendationId = 1;

    postAndVerifyRating(productId, recommendationId, HttpStatus.OK);
    Assertions.assertEquals(1, repository.findByProductId(productId).size());

    deleteAndVerifyRatingsByProductId(productId, HttpStatus.OK);
    Assertions.assertEquals(0, repository.findByProductId(productId).size());

    deleteAndVerifyRatingsByProductId(productId, HttpStatus.OK);
  }

  @Test
  void getRatingsMissingParameter() {
    getAndVerifyRatingsByProductId("", HttpStatus.BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Required query parameter 'productId' is not present.");
  }

  @Test
  void getRatingsInvalidParameter() {
    getAndVerifyRatingsByProductId("?productId=no-integer", HttpStatus.BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  void getRatingsNotFound() {
    getAndVerifyRatingsByProductId("?productId=113", HttpStatus.OK)
        .jsonPath("$.length()")
        .isEqualTo(0);
  }

  @Test
  void getRatingsInvalidParameterNegativeValue() {
    int productIdInvalid = -1;
    getAndVerifyRatingsByProductId(
            "?productId=" + productIdInvalid, HttpStatus.UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyRatingsByProductId(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyRatingsByProductId("?productId=" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyRatingsByProductId(
      String productIdQuery, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/rating" + productIdQuery)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyRating(
      int productId, int recommendationId, HttpStatus expectedStatus) {
    Rating recommendation =
        new Rating(
            productId,
            recommendationId,
            "Author " + recommendationId,
            recommendationId,
            "Content " + recommendationId,
            "SA");
    return client
        .post()
        .uri("/rating")
        .body(Mono.just(recommendation), Rating.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyRatingsByProductId(
      int productId, HttpStatus expectedStatus) {
    return client
        .delete()
        .uri("/rating?productId=" + productId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectBody();
  }
}
