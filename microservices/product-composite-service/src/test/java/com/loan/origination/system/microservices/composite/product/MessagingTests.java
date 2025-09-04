package com.loan.origination.system.microservices.composite.product;

import com.loan.origination.system.api.composite.product.ProductAggregate;
import com.loan.origination.system.api.composite.product.RatingSummary;
import com.loan.origination.system.api.composite.product.ReviewSummary;
import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.event.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {"spring.main.allow-bean-definition-overriding=true", "eureka.client.enabled=false"})
@Import({TestChannelBinderConfiguration.class})
class MessagingTests {

  private static final Logger LOG = LoggerFactory.getLogger(MessagingTests.class);

  @Autowired private WebTestClient client;

  @Autowired private OutputDestination target;

  @BeforeEach
  void setUp() {
    purgeMessages("products");
    purgeMessages("ratings");
    purgeMessages("reviews");
  }

  @Test
  void createCompositeProduct1() {

    ProductAggregate composite = new ProductAggregate(1, "name", "1", null, null, null);
    postAndVerifyProduct(composite, HttpStatus.ACCEPTED);

    final List<String> productMessages = getMessages("products");
    final List<String> ratingMessages = getMessages("ratings");
    final List<String> reviewMessages = getMessages("reviews");

    // Assert one expected new product event queued up
    Assertions.assertEquals(1, productMessages.size());

    Event<Integer, Product> expectedEvent =
        new Event(
            Event.Type.CREATE,
            composite.getProductId(),
            new Product(
                composite.getProductId(), composite.getName(), composite.getDescription(), null));
    MatcherAssert.assertThat(
        productMessages.get(0), Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedEvent)));

    // Assert no rating and review events
    Assertions.assertEquals(0, ratingMessages.size());
    Assertions.assertEquals(0, reviewMessages.size());
  }

  @Test
  void createCompositeProduct2() {

    ProductAggregate composite =
        new ProductAggregate(
            1,
            "name",
            "1",
            Collections.singletonList(new RatingSummary(1, "a", 1, "c")),
            Collections.singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);
    postAndVerifyProduct(composite, HttpStatus.ACCEPTED);

    final List<String> productMessages = getMessages("products");
    final List<String> ratingMessages = getMessages("ratings");
    final List<String> reviewMessages = getMessages("reviews");

    // Assert one create product event queued up
    Assertions.assertEquals(1, productMessages.size());

    Event<Integer, Product> expectedProductEvent =
        new Event(
            Event.Type.CREATE,
            composite.getProductId(),
            new Product(
                composite.getProductId(), composite.getName(), composite.getDescription(), null));
    MatcherAssert.assertThat(
        productMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedProductEvent)));

    // Assert one create rating event queued up
    Assertions.assertEquals(1, ratingMessages.size());

    RatingSummary rec = composite.getRatings().get(0);
    Event<Integer, Product> expectedRatingEvent =
        new Event(
            Event.Type.CREATE,
            composite.getProductId(),
            new Rating(
                composite.getProductId(),
                rec.getRatingId(),
                rec.getAuthor(),
                rec.getRate(),
                rec.getContent(),
                null));
    MatcherAssert.assertThat(
        ratingMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedRatingEvent)));

    // Assert one create review event queued up
    Assertions.assertEquals(1, reviewMessages.size());

    ReviewSummary rev = composite.getReviews().get(0);
    Event<Integer, Product> expectedReviewEvent =
        new Event(
            Event.Type.CREATE,
            composite.getProductId(),
            new Review(
                composite.getProductId(),
                rev.getReviewId(),
                rev.getAuthor(),
                rev.getSubject(),
                rev.getContent(),
                null));
    MatcherAssert.assertThat(
        reviewMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedReviewEvent)));
  }

  @Test
  void deleteCompositeProduct() {
    deleteAndVerifyProduct(1, HttpStatus.ACCEPTED);

    final List<String> productMessages = getMessages("products");
    final List<String> ratingMessages = getMessages("ratings");
    final List<String> reviewMessages = getMessages("reviews");

    // Assert one delete product event queued up
    Assertions.assertEquals(1, productMessages.size());

    Event<Integer, Product> expectedProductEvent = new Event(Event.Type.DELETE, 1, null);
    MatcherAssert.assertThat(
        productMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedProductEvent)));

    // Assert one delete rating event queued up
    Assertions.assertEquals(1, ratingMessages.size());

    Event<Integer, Product> expectedRatingEvent = new Event(Event.Type.DELETE, 1, null);
    MatcherAssert.assertThat(
        ratingMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedRatingEvent)));

    // Assert one delete review event queued up
    Assertions.assertEquals(1, reviewMessages.size());

    Event<Integer, Product> expectedReviewEvent = new Event(Event.Type.DELETE, 1, null);
    MatcherAssert.assertThat(
        reviewMessages.get(0),
        Matchers.is(IsSameEvent.sameEventExceptCreatedAt(expectedReviewEvent)));
  }

  private void purgeMessages(String bindingName) {
    getMessages(bindingName);
  }

  private List<String> getMessages(String bindingName) {
    List<String> messages = new ArrayList<>();
    boolean anyMoreMessages = true;

    while (anyMoreMessages) {
      Message<byte[]> message = getMessage(bindingName);

      if (message == null) {
        anyMoreMessages = false;

      } else {
        messages.add(new String(message.getPayload()));
      }
    }
    return messages;
  }

  private Message<byte[]> getMessage(String bindingName) {
    try {
      return target.receive(0, bindingName);
    } catch (NullPointerException npe) {
      // If the messageQueues member variable in the target object contains no queues when the
      // receive method is called, it will cause a NPE to be thrown.
      // So we catch the NPE here and return null to indicate that no messages were found.
      LOG.error("getMessage() received a NPE with binding = {}", bindingName);
      return null;
    }
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
