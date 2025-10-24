package com.loan.origination.system.microservices.review.controller;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.core.review.ReviewAPI;
import com.loan.origination.system.microservices.review.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ReviewController implements ReviewAPI {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

  private final ReviewService reviewService;

  @Autowired
  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @Override
  public Flux<Review> getReviews(HttpHeaders headers, int productId) {
    return reviewService.getReviews(headers, productId);
  }

  @Override
  public Mono<Review> createReview(Review body) {
    LOG.info("Review Controller: " + body);
    return reviewService.createReview(body);
  }

  @Override
  public Mono<Void> deleteReviews(int productId) {
    return reviewService.deleteReviews(productId);
  }
}
