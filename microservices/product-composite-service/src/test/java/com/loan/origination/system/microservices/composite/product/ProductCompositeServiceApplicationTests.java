package com.loan.origination.system.microservices.composite.product;

import static org.mockito.Mockito.when;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.composite.product.integration.ProductCompositeIntegration;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {TestSecurityConfig.class},
    properties = {
      "spring.security.oauth2.resourceserver.jwt.issuer-uri=",
      "spring.main.allow-bean-definition-overriding=true",
      "spring.cloud.config.enabled=false"
    })
class ProductCompositeServiceApplicationTests {

  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  @Autowired private WebTestClient client;
  @MockitoBean private ProductCompositeIntegration integration;

  @BeforeEach
  void setup() {
    when(integration.getProduct(
            ArgumentMatchers.eq(PRODUCT_ID_OK),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()))
        .thenReturn(
            Mono.just(new Product(PRODUCT_ID_OK, "name", "name-" + PRODUCT_ID_OK, "mock-address")));
    when(integration.getRatings(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                Collections.singletonList(
                    new Rating(PRODUCT_ID_OK, 1, "author", 1, "content", "mock-address"))));
    when(integration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                Collections.singletonList(
                    new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock-address"))));

    when(integration.getProduct(
            ArgumentMatchers.eq(PRODUCT_ID_NOT_FOUND),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()))
        .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

    when(integration.getProduct(
            ArgumentMatchers.eq(PRODUCT_ID_INVALID),
            ArgumentMatchers.anyInt(),
            ArgumentMatchers.anyInt()))
        .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  void contextLoads() {}

  @Test
  void getProductById() {

    getAndVerifyProduct(PRODUCT_ID_OK, HttpStatus.OK)
        .jsonPath("$.productId")
        .isEqualTo(PRODUCT_ID_OK)
        .jsonPath("$.ratings.length()")
        .isEqualTo(1)
        .jsonPath("$.reviews.length()")
        .isEqualTo(1);
  }

  @Test
  void getProductNotFound() {

    getAndVerifyProduct(PRODUCT_ID_NOT_FOUND, HttpStatus.NOT_FOUND)
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .jsonPath("$.message")
        .isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
  }

  @Test
  void getProductInvalidInput() {

    getAndVerifyProduct(PRODUCT_ID_INVALID, HttpStatus.UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
        .jsonPath("$.message")
        .isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/product-composite/" + productId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }
}
