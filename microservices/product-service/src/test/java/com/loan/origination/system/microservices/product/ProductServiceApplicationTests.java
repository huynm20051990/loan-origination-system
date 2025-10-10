package com.loan.origination.system.microservices.product;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.event.Event;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.product.repository.ProductRepository;
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
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests extends MongoDBTestBase {

  @Autowired private WebTestClient client;

  @Autowired private ProductRepository repository;

  @Autowired
  @Qualifier("messageProcessor")
  private Consumer<Event<Integer, Product>> messageProcessor;

  @BeforeEach
  void setupDb() {
    repository.deleteAll().block();
  }

  @Test
  void getProductById() {
    int productId = 1;

    Assertions.assertNull(repository.findByProductId(productId).block());
    Assertions.assertEquals(0, (long) repository.count().block());

    sendCreateProductEvent(productId);

    Assertions.assertNotNull(repository.findByProductId(productId).block());
    Assertions.assertEquals(1, (long) repository.count().block());

    getAndVerifyProduct(productId, HttpStatus.OK).jsonPath("$.productId").isEqualTo(productId);
  }

  @Test
  void duplicateError() {

    int productId = 1;

    Assertions.assertNull(repository.findByProductId(productId).block());

    sendCreateProductEvent(productId);

    Assertions.assertNotNull(repository.findByProductId(productId).block());

    InvalidInputException thrown =
        Assertions.assertThrows(
            InvalidInputException.class,
            () -> sendCreateProductEvent(productId),
            "Expected a InvalidInputException here!");
    Assertions.assertEquals("Duplicate key, Product Id: " + productId, thrown.getMessage());
  }

  @Test
  void deleteProduct() {
    int productId = 1;

    sendCreateProductEvent(productId);
    Assertions.assertNotNull(repository.findByProductId(productId).block());

    sendDeleteProductEvent(productId);
    Assertions.assertNull(repository.findByProductId(productId).block());

    sendDeleteProductEvent(productId);
  }

  @Test
  void getProductInvalidParameterString() {
    getAndVerifyProduct("/no-integer", HttpStatus.BAD_REQUEST)
        .jsonPath("$.path")
        .isEqualTo("/product/no-integer")
        .jsonPath("$.message")
        .isEqualTo("Type mismatch.");
  }

  @Test
  void getProductNotFound() {

    int productIdNotFound = 13;
    getAndVerifyProduct(productIdNotFound, HttpStatus.NOT_FOUND)
        .jsonPath("$.path")
        .isEqualTo("/product/" + productIdNotFound)
        .jsonPath("$.message")
        .isEqualTo("No product found for productId: " + productIdNotFound);
  }

  @Test
  void getProductInvalidParameterNegativeValue() {
    int productIdInvalid = -1;

    getAndVerifyProduct(productIdInvalid, HttpStatus.UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/product/" + productIdInvalid)
        .jsonPath("$.message")
        .isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return getAndVerifyProduct("/" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      String productIdPath, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/product" + productIdPath)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    Product product = new Product(productId, "Name " + productId, String.valueOf(productId), "SA");
    return client
        .post()
        .uri("/product")
        .body(Mono.just(product), Product.class)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(MediaType.APPLICATION_JSON)
        .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return client
        .delete()
        .uri("/product/" + productId)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectBody();
  }

  private void sendCreateProductEvent(int productId) {
    Product product = new Product(productId, "Name " + productId, String.valueOf(productId), "SA");
    Event<Integer, Product> event = new Event(Event.Type.CREATE, productId, product);
    messageProcessor.accept(event);
  }

  private void sendDeleteProductEvent(int productId) {
    Event<Integer, Product> event = new Event(Event.Type.DELETE, productId, null);
    messageProcessor.accept(event);
  }
}
