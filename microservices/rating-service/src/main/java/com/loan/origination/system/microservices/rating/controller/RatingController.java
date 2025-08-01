package com.loan.origination.system.microservices.rating.controller;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.rating.RatingAPI;
import com.loan.origination.system.microservices.rating.service.RatingService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController implements RatingAPI {

  private static final Logger LOG = LoggerFactory.getLogger(RatingController.class);

  private final RatingService ratingService;

  @Autowired
  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @Override
  public List<Rating> getRatings(int productId) {
    return ratingService.getRatings(productId);
  }

  @Override
  public Rating createRating(Rating rating) {
    return ratingService.createRating(rating);
  }

  @Override
  public void deleteRatings(int productId) {
    ratingService.deleteRatings(productId);
  }
}
