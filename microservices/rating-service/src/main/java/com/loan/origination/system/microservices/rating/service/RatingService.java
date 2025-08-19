package com.loan.origination.system.microservices.rating.service;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import com.loan.origination.system.microservices.rating.mapper.RatingMapper;
import com.loan.origination.system.microservices.rating.repository.RatingRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RatingService {

  private static final Logger LOG = LoggerFactory.getLogger(RatingService.class);
  private final ServiceUtil serviceUtil;
  private final RatingRepository ratingRepository;
  private final RatingMapper ratingMapper;

  @Autowired
  public RatingService(
      ServiceUtil serviceUtil, RatingRepository ratingRepository, RatingMapper ratingMapper) {
    this.serviceUtil = serviceUtil;
    this.ratingRepository = ratingRepository;
    this.ratingMapper = ratingMapper;
  }

  public Mono<Rating> createRating(Rating rating) {
    if (rating.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + rating.getProductId());
    }

    RatingEntity entity = ratingMapper.apiToEntity(rating);
    Mono<Rating> newEntity =
        ratingRepository
            .save(entity)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->
                    new InvalidInputException(
                        "Duplicate key, Product Id: "
                            + rating.getProductId()
                            + ", Rating Id:"
                            + rating.getRatingId()))
            .map(e -> ratingMapper.entityToApi(e));
    return newEntity;
  }

  public Flux<Rating> getRatings(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    LOG.info("Will get ratings for product with id={}", productId);

    return ratingRepository
        .findByProductId(productId)
        .log(LOG.getName(), Level.FINE)
        .map(e -> ratingMapper.entityToApi(e))
        .map(e -> setServiceAddress(e));
  }

  public Mono<Void> deleteRatings(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.debug("deleteRatings: tries to delete ratings for productId: {}", productId);
    return ratingRepository.deleteAll(ratingRepository.findByProductId(productId));
  }

  private Rating setServiceAddress(Rating e) {
    e.setServiceAddress(serviceUtil.getServiceAddress());
    return e;
  }
}
