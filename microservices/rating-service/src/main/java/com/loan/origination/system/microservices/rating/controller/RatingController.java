package com.loan.origination.system.microservices.rating.controller;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.rating.RatingAPI;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController implements RatingAPI {

  private static final Logger LOG = LoggerFactory.getLogger(RatingController.class);

  private final ServiceUtil serviceUtil;

  @Autowired
  public RatingController(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public List<Rating> getRatings(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    if (productId > 113) {
      LOG.debug("No rating found for productId: " + productId);
      return new ArrayList<>();
    }

    List<Rating> ratings = new ArrayList<>();
    ratings.add(
        new Rating(productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()));
    ratings.add(
        new Rating(productId, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()));
    ratings.add(
        new Rating(productId, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress()));
    return ratings;
  }
}
