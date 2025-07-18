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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductCompositeServiceApplicationTests {

  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  @Autowired private WebTestClient client;
  @MockitoBean private ProductCompositeIntegration integration;

  @BeforeEach
  void setup() {
    when(integration.getProduct(PRODUCT_ID_OK))
        .thenReturn(new Product(PRODUCT_ID_OK, "name", "name-" + PRODUCT_ID_OK, "mock-address"));
    when(integration.getRatings(PRODUCT_ID_OK))
        .thenReturn(
            Collections.singletonList(
                new Rating(PRODUCT_ID_OK, 1, "author", 1, "content", "mock-address")));
    when(integration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            Collections.singletonList(
                new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock-address")));

    when(integration.getProduct(PRODUCT_ID_NOT_FOUND))
        .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

    when(integration.getProduct(PRODUCT_ID_INVALID))
        .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  void contextLoads() {}

  @Test
  void getProductById() {
    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_OK)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.productId")
        .isEqualTo(PRODUCT_ID_OK)
        .jsonPath("$.ratings.length()")
        .isEqualTo(1)
        .jsonPath("$.reviews.length()")
        .isEqualTo(1);
  }

  @Test
  void getProductNotFound() {
    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .jsonPath("$.message")
        .isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
  }

  @Test
  void getProductInvalid() {
    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_INVALID)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
        .jsonPath("$.message")
        .isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
  }
}
