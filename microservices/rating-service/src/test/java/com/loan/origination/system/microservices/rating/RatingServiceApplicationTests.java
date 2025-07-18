package com.loan.origination.system.microservices.rating;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RatingServiceApplicationTests {

  @Autowired private WebTestClient client;

  @Test
  void getRatingsByProductId() {
    int productId = 1;
    client
        .get()
        .uri("/rating?productId=" + productId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(3)
        .jsonPath("$[0].productId")
        .isEqualTo(productId);
  }

  @Test
  void getRatingsMissingParameter() {
    client
        .get()
        .uri("/rating")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(BAD_REQUEST)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Required query parameter 'productId' is not present.");
  }

  @Test
  void getRatingsInvalidParameter() {
    client
        .get()
        .uri("/rating?productId=no-integer")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(BAD_REQUEST)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  void getRatingsNotFound() {
    int productIdNotFound = 113;

    client
        .get()
        .uri("/rating?productId=" + productIdNotFound)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.length()")
        .isEqualTo(0);
  }

  @Test
  void getRatingsInvalidParameterNegativeValue() {
    int productIdInvalid = -1;

    client
        .get()
        .uri("/rating?productId=" + productIdInvalid)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(UNPROCESSABLE_ENTITY)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/rating")
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  @Test
  void contextLoads() {}
}
