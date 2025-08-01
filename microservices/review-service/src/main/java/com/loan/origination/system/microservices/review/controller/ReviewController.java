package com.loan.origination.system.microservices.review.controller;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.core.review.ReviewAPI;
import com.loan.origination.system.microservices.review.service.ReviewService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController implements ReviewAPI {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

  private final ReviewService reviewService;

  @Autowired
  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @Override
  public List<Review> getReviews(int productId) {
    return reviewService.getReviews(productId);
  }

  @Override
  public Review createReview(Review review) {
    return reviewService.createReview(review);
  }

  @Override
  public void deleteReviews(int productId) {
    reviewService.deleteReviews(productId);
  }
}
