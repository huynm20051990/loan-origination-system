package com.loan.origination.system.microservices.composite.product;

import static org.mockito.Mockito.when;

import com.loan.origination.system.api.composite.product.ProductAggregate;
import com.loan.origination.system.api.composite.product.RatingSummary;
import com.loan.origination.system.api.composite.product.ReviewSummary;
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
import reactor.core.publisher.Mono;

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
  void createCompositeProduct1() {

    ProductAggregate compositeProduct = new ProductAggregate(1, "name", "1", null, null, null);

    postAndVerifyProduct(compositeProduct, HttpStatus.OK);
  }

  @Test
  void createCompositeProduct2() {
    ProductAggregate compositeProduct =
        new ProductAggregate(
            1,
            "name",
            "1",
            Collections.singletonList(new RatingSummary(1, "a", 1, "c")),
            Collections.singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProduct(compositeProduct, HttpStatus.OK);
  }

  @Test
  void deleteCompositeProduct() {
    ProductAggregate compositeProduct =
        new ProductAggregate(
            1,
            "name",
            "1",
            Collections.singletonList(new RatingSummary(1, "a", 1, "c")),
            Collections.singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProduct(compositeProduct, HttpStatus.OK);

    deleteAndVerifyProduct(compositeProduct.getProductId(), HttpStatus.OK);
    deleteAndVerifyProduct(compositeProduct.getProductId(), HttpStatus.OK);
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

  private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
    client
        .post()
        .uri("/product-composite")
        .body(Mono.just(compositeProduct), ProductAggregate.class)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus);
  }

  private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    client
        .delete()
        .uri("/product-composite/" + productId)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus);
  }
}
