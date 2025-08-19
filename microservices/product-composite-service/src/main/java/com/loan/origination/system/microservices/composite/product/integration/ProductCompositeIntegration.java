package com.loan.origination.system.microservices.composite.product.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.product.ProductAPI;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.rating.RatingAPI;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.core.review.ReviewAPI;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.util.http.HttpErrorInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductCompositeIntegration implements ProductAPI, RatingAPI, ReviewAPI {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
  private final RestTemplate restTemplate;
  private final ObjectMapper mapper;
  private final String productServiceUrl;
  private final String ratingServiceUrl;
  private final String reviewServiceUrl;

  @Autowired
  public ProductCompositeIntegration(
      RestTemplate restTemplate,
      ObjectMapper mapper,
      @Value("${app.product-service.host}") String productServiceHost,
      @Value("${app.product-service.port}") int productServicePort,
      @Value("${app.rating-service.host}") String ratingServiceHost,
      @Value("${app.rating-service.port}") int ratingServicePort,
      @Value("${app.review-service.host}") String reviewServiceHost,
      @Value("${app.review-service.port}") int reviewServicePort) {
    this.restTemplate = restTemplate;
    this.mapper = mapper;
    this.productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product";
    this.ratingServiceUrl = "http://" + ratingServiceHost + ":" + ratingServicePort + "/rating";
    this.reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
  }

  @Override
  public Mono<Product> getProduct(int productId) {
    try {
      String url = productServiceUrl + "/" + productId;
      LOG.debug("Will call getProduct API on URL: {}", url);
      Product product = restTemplate.getForObject(url, Product.class);
      LOG.debug("Found a product with id: {}", product != null ? product.getProductId() : 0);
      return product;
    } catch (HttpClientErrorException ex) {
      switch (HttpStatus.resolve(ex.getStatusCode().value())) {
        case NOT_FOUND -> throw new NotFoundException(getErrorMessage(ex));
        case UNPROCESSABLE_ENTITY -> throw new InvalidInputException(getErrorMessage(ex));
        default -> {
          LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
          LOG.warn("Error body: {}", ex.getResponseBodyAsString());
          throw ex;
        }
      }
    }
  }

  @Override
  public Mono<Product> createProduct(Product body) {
    try {
      String url = productServiceUrl;
      LOG.debug("Will post a new product to URL: {}", url);

      Product product = restTemplate.postForObject(url, body, Product.class);
      LOG.debug("Created a product with id: {}", product.getProductId());

      return product;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    try {
      String url = productServiceUrl + "/" + productId;
      LOG.debug("Will call the deleteProduct API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Flux<Rating> getRatings(int productId) {

    try {
      String url = ratingServiceUrl + productId;
      LOG.debug("Will call getRatings API on URL: {}", url);
      List<Rating> ratings =
          restTemplate
              .exchange(
                  url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Rating>>() {})
              .getBody();
      LOG.debug("Found {} ratings for a product with id: {}", ratings.size(), productId);
      return ratings;
    } catch (Exception ex) {
      LOG.warn(
          "Got an exception while requesting ratings, return zero ratings: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public Mono<Rating> createRating(Rating body) {
    try {
      String url = ratingServiceUrl;
      LOG.debug("Will post a new rating to URL: {}", url);

      Rating rating = restTemplate.postForObject(url, body, Rating.class);
      LOG.debug("Created a rating with id: {}", rating.getProductId());

      return rating;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Mono<Void> deleteRatings(int productId) {
    try {
      String url = ratingServiceUrl + "?productId=" + productId;
      LOG.debug("Will call the deleteRatings API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Flux<Review> getReviews(int productId) {
    try {
      String url = reviewServiceUrl + productId;
      LOG.debug("Will call getReviews API on URL: {}", url);
      List<Review> reviews =
          restTemplate
              .exchange(
                  url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {})
              .getBody();
      LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
      return reviews;
    } catch (Exception ex) {
      LOG.warn(
          "Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
      return new ArrayList<>();
    }
  }

  @Override
  public Mono<Review> createReview(Review body) {
    try {
      String url = reviewServiceUrl;
      LOG.debug("Will post a new review to URL: {}", url);

      Review review = restTemplate.postForObject(url, body, Review.class);
      LOG.debug("Created a review with id: {}", review.getProductId());

      return review;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  @Override
  public Mono<Void> deleteReviews(int productId) {
    try {
      String url = reviewServiceUrl + "?productId=" + productId;
      LOG.debug("Will call the deleteReviews API on URL: {}", url);

      restTemplate.delete(url);

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }
  }

  private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
    switch (HttpStatus.resolve(ex.getStatusCode().value())) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(ex));

      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(ex));

      default:
        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        LOG.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
  }

  private String getErrorMessage(HttpClientErrorException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ioex.getMessage();
    }
  }
}
