package com.loan.origination.system.microservices.composite.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.product.ProductAPI;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.rating.RatingAPI;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.core.review.ReviewAPI;
import com.loan.origination.system.api.event.Event;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.util.http.HttpErrorInfo;
import java.io.IOException;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class ProductCompositeIntegration implements ProductAPI, RatingAPI, ReviewAPI {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
  private final WebClient webClient;
  private final ObjectMapper mapper;
  private static final String PRODUCT_SERVICE_URL = "http://product";
  private static final String RATING_SERVICE_URL = "http://rating";
  private static final String REVIEW_SERVICE_URL = "http://review";

  private final StreamBridge streamBridge;
  private final Scheduler publishEventScheduler;

  @Autowired
  public ProductCompositeIntegration(
      @Qualifier("publishEventScheduler") Scheduler publishEventScheduler,
      WebClient.Builder webClient,
      ObjectMapper mapper,
      StreamBridge streamBridge) {
    this.publishEventScheduler = publishEventScheduler;
    this.webClient = webClient.build();
    this.mapper = mapper;
    this.streamBridge = streamBridge;
  }

  @Override
  public Mono<Product> getProduct(int productId) {
    String url = PRODUCT_SERVICE_URL + "/product/" + productId;
    LOG.debug("Will call the getProduct API on URL: {}", url);

    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(Product.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));
  }

  @Override
  public Mono<Product> createProduct(Product body) {
    return Mono.fromCallable(
            () -> {
              sendMessage(
                  "products-out-0", new Event(Event.Type.CREATE, body.getProductId(), body));
              return body;
            })
        .subscribeOn(publishEventScheduler);
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    return Mono.fromRunnable(
            () -> sendMessage("products-out-0", new Event(Event.Type.DELETE, productId, null)))
        .subscribeOn(publishEventScheduler)
        .then();
  }

  @Override
  public Flux<Rating> getRatings(int productId) {
    String url = RATING_SERVICE_URL + "/rating?productId=" + productId;

    LOG.debug("Will call the getRatings API on URL: {}", url);

    // Return an empty result if something goes wrong to make it possible for the composite service
    // to return partial responses
    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToFlux(Rating.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorResume(error -> Flux.empty());
  }

  @Override
  public Mono<Rating> createRating(Rating body) {
    return Mono.fromCallable(
            () -> {
              sendMessage("ratings-out-0", new Event(Event.Type.CREATE, body.getProductId(), body));
              return body;
            })
        .subscribeOn(publishEventScheduler);
  }

  @Override
  public Mono<Void> deleteRatings(int productId) {
    return Mono.fromRunnable(
            () -> sendMessage("ratings-out-0", new Event(Event.Type.DELETE, productId, null)))
        .subscribeOn(publishEventScheduler)
        .then();
  }

  @Override
  public Flux<Review> getReviews(int productId) {
    String url = REVIEW_SERVICE_URL + "/review?productId=" + productId;

    LOG.debug("Will call the getReviews API on URL: {}", url);

    // Return an empty result if something goes wrong to make it possible for the composite service
    // to return partial responses
    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToFlux(Review.class)
        .log(LOG.getName(), Level.FINE)
        .onErrorResume(error -> Flux.empty());
  }

  @Override
  public Mono<Review> createReview(Review body) {
    return Mono.fromCallable(
            () -> {
              sendMessage("reviews-out-0", new Event(Event.Type.CREATE, body.getProductId(), body));
              return body;
            })
        .subscribeOn(publishEventScheduler);
  }

  @Override
  public Mono<Void> deleteReviews(int productId) {
    return Mono.fromRunnable(
            () -> sendMessage("reviews-out-0", new Event(Event.Type.DELETE, productId, null)))
        .subscribeOn(publishEventScheduler)
        .then();
  }

  public Mono<Health> getProductHealth() {
    return getHealth(PRODUCT_SERVICE_URL);
  }

  public Mono<Health> getRatingHealth() {
    return getHealth(RATING_SERVICE_URL);
  }

  public Mono<Health> getReviewHealth() {
    return getHealth(REVIEW_SERVICE_URL);
  }

  private Mono<Health> getHealth(String url) {
    url += "/actuator/health";
    LOG.debug("Will call the Health API on URL: {}", url);
    return webClient
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .map(s -> new Health.Builder().up().build())
        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
        .log(LOG.getName(), Level.FINE);
  }

  private void sendMessage(String bindingName, Event event) {
    LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
    Message message =
        MessageBuilder.withPayload(event).setHeader("partitionKey", event.getKey()).build();
    streamBridge.send(bindingName, message);
  }

  private Throwable handleException(Throwable ex) {
    if (!(ex instanceof WebClientResponseException)) {
      LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
      return ex;
    }
    WebClientResponseException wcre = (WebClientResponseException) ex;
    switch (HttpStatus.resolve(wcre.getStatusCode().value())) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(wcre));

      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(wcre));

      default:
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
        return ex;
    }
  }

  private String getErrorMessage(WebClientResponseException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }
}
