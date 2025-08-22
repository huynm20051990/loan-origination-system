package com.loan.origination.system.microservices.review;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.event.Event;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.review.repository.ReviewRepository;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReviewServiceApplicationTests extends MySqlTestBase {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceApplication.class);

  @Autowired private WebTestClient client;

  @Autowired private ReviewRepository repository;

  @Autowired
  @Qualifier("messageProcessor")
  private Consumer<Event<Integer, Review>> messageProcessor;

  @BeforeEach
  void setupDb() {
    repository.deleteAll();
  }

  @Test
  void getReviewsByProductId() {

    int productId = 1;

    Assertions.assertEquals(0, repository.findByProductId(productId).size());

    sendCreateReviewEvent(productId, 1);
    sendCreateReviewEvent(productId, 2);
    sendCreateReviewEvent(productId, 3);

    Assertions.assertEquals(3, repository.findByProductId(productId).size());

    getAndVerifyReviewsByProductId(productId, HttpStatus.OK)
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[2].productId")
        .isEqualTo(productId)
        .jsonPath("$[2].reviewId")
        .isEqualTo(3);
  }

  @Test
  void duplicateError() {

    int productId = 1;
    int reviewId = 1;

    Assertions.assertEquals(0, repository.count());

    sendCreateReviewEvent(productId, reviewId);

    Assertions.assertEquals(1, repository.count());

    InvalidInputException thrown =
        Assertions.assertThrows(
            InvalidInputException.class,
            () -> sendCreateReviewEvent(productId, reviewId),
            "Expected a InvalidInputException here!");
    Assertions.assertEquals("Duplicate key, Product Id: 1, Review Id:1", thrown.getMessage());

    Assertions.assertEquals(1, repository.count());
  }

  @Test
  void deleteReviews() {

    int productId = 1;
    int reviewId = 1;

    sendCreateReviewEvent(productId, reviewId);
    Assertions.assertEquals(1, repository.findByProductId(productId).size());

    sendDeleteReviewEvent(productId);
    Assertions.assertEquals(0, repository.findByProductId(productId).size());

    sendDeleteReviewEvent(productId);
  }

  @Test
  void getReviewsMissingParameter() {

    getAndVerifyReviewsByProductId("", HttpStatus.BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/review")
        .jsonPath("$.message")
        .isEqualTo("Required query parameter 'productId' is not present.");
  }

  @Test
  void getReviewsInvalidParameter() {

    getAndVerifyReviewsByProductId("?productId=no-integer", HttpStatus.BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/review")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  void getReviewsNotFound() {

    getAndVerifyReviewsByProductId("?productId=213", HttpStatus.OK)
        .jsonPath("$.length()")
        .isEqualTo(0);
  }

  @Test
  void getReviewsInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyReviewsByProductId(
            "?productId=" + productIdInvalid, HttpStatus.UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/review")
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyReviewsByProductId("?productId=" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyReviewsByProductId(
      String productIdQuery, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/review" + productIdQuery)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyReviewsByProductId(
      int productId, HttpStatus expectedStatus) {
    return client
        .delete()
        .uri("/review?productId=" + productId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectBody();
  }

  private void sendCreateReviewEvent(int productId, int reviewId) {
    Review review =
        new Review(
            productId,
            reviewId,
            "Author " + reviewId,
            "Subject " + reviewId,
            "Content " + reviewId,
            "SA");
    Event<Integer, Review> event = new Event(Event.Type.CREATE, productId, review);
    messageProcessor.accept(event);
  }

  private void sendDeleteReviewEvent(int productId) {
    Event<Integer, Review> event = new Event(Event.Type.DELETE, productId, null);
    messageProcessor.accept(event);
  }
}
