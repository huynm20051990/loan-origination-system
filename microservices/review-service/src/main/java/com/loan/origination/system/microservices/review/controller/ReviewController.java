package com.loan.origination.system.microservices.review.controller;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.core.review.ReviewAPI;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController implements ReviewAPI {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

  private ServiceUtil serviceUtil;

  @Autowired
  public ReviewController(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public List<Review> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    if (productId == 213) {
      LOG.debug("No review found for productId: {}", productId);
      return new ArrayList<>();
    }
    List<Review> reviews = new ArrayList<>();
    reviews.add(
        new Review(
            productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()));
    reviews.add(
        new Review(
            productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()));
    reviews.add(
        new Review(
            productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress()));
    return reviews;
  }
}
