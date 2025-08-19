package com.loan.origination.system.microservices.rating;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.event.Event;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.rating.repository.RatingRepository;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RatingServiceApplicationTests extends MongoDbTestBase {

  @Autowired private WebTestClient client;

  @Autowired private RatingRepository repository;

  @Autowired
  @Qualifier("messageProcessor")
  private Consumer<Event<Integer, Rating>> messageProcessor;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
  }

  @Test
  void getRatingsByProductId() {
    int productId = 1;

    sendCreateRatingEvent(productId, 1);
    sendCreateRatingEvent(productId, 2);
    sendCreateRatingEvent(productId, 3);

    Assertions.assertEquals(3, (long) repository.findByProductId(productId).count().block());

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
    int ratingId = 1;

    sendCreateRatingEvent(productId, ratingId);

    Assertions.assertEquals(1, (long) repository.count().block());

    InvalidInputException thrown =
        Assertions.assertThrows(
            InvalidInputException.class,
            () -> sendCreateRatingEvent(productId, ratingId),
            "Expected a InvalidInputException here!");
    Assertions.assertEquals("Duplicate key, Product Id: 1, Rating Id:1", thrown.getMessage());

    Assertions.assertEquals(1, (long) repository.count().block());
  }

  @Test
  void deleteRatings() {

    int productId = 1;
    int ratingId = 1;

    sendCreateRatingEvent(productId, ratingId);
    Assertions.assertEquals(1, repository.findByProductId(productId).count().block());

    sendDeleteRatingEvent(productId);
    Assertions.assertEquals(0, repository.findByProductId(productId).count().block());

    sendDeleteRatingEvent(productId);
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

  private void sendCreateRatingEvent(int productId, int recommendationId) {
    Rating recommendation =
        new Rating(
            productId,
            recommendationId,
            "Author " + recommendationId,
            recommendationId,
            "Content " + recommendationId,
            "SA");
    Event<Integer, Rating> event = new Event(Event.Type.CREATE, productId, recommendation);
    messageProcessor.accept(event);
  }

  private void sendDeleteRatingEvent(int productId) {
    Event<Integer, Rating> event = new Event(Event.Type.DELETE, productId, null);
    messageProcessor.accept(event);
  }
}
