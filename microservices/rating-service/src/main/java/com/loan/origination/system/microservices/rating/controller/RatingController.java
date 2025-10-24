package com.loan.origination.system.microservices.rating.controller;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.rating.RatingAPI;
import com.loan.origination.system.microservices.rating.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RatingController implements RatingAPI {

  private static final Logger LOG = LoggerFactory.getLogger(RatingController.class);

  private final RatingService ratingService;

  @Autowired
  public RatingController(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @Override
  public Flux<Rating> getRatings(HttpHeaders headers, int productId) {
    return ratingService.getRatings(headers, productId);
  }

  @Override
  public Mono<Rating> createRating(Rating body) {
    return ratingService.createRating(body);
  }

  @Override
  public Mono<Void> deleteRatings(int productId) {
    return ratingService.deleteRatings(productId);
  }
}
