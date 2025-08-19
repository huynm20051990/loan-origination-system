package com.loan.origination.system.microservices.review.service;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.review.entity.ReviewEntity;
import com.loan.origination.system.microservices.review.mapper.ReviewMapper;
import com.loan.origination.system.microservices.review.repository.ReviewRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.List;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
public class ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final ServiceUtil serviceUtil;
  private final Scheduler jdbcScheduler;

  @Autowired
  public ReviewService(
      @Qualifier("jdbcScheduler") Scheduler jdbcScheduler,
      ReviewRepository reviewRepository,
      ReviewMapper reviewMapper,
      ServiceUtil serviceUtil) {
    this.jdbcScheduler = jdbcScheduler;
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
    this.serviceUtil = serviceUtil;
  }

  public Mono<Review> createReview(Review review) {
    if (review.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + review.getProductId());
    }
    return Mono.fromCallable(() -> internalCreateReview(review)).subscribeOn(jdbcScheduler);
  }

  public Flux<Review> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get reviews for product with id={}", productId);

    return Mono.fromCallable(() -> internalGetReviews(productId))
        .flatMapMany(Flux::fromIterable)
        .log(LOG.getName(), Level.FINE)
        .subscribeOn(jdbcScheduler);
  }

  public Mono<Void> deleteReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    return Mono.fromRunnable(() -> internalDeleteReviews(productId))
        .subscribeOn(jdbcScheduler)
        .then();
  }

  private Review internalCreateReview(Review body) {
    try {
      ReviewEntity entity = reviewMapper.apiToEntity(body);
      ReviewEntity newEntity = reviewRepository.save(entity);

      LOG.debug(
          "createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return reviewMapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException(
          "Duplicate key, Product Id: "
              + body.getProductId()
              + ", Review Id:"
              + body.getReviewId());
    }
  }

  private List<Review> internalGetReviews(int productId) {

    List<ReviewEntity> entityList = reviewRepository.findByProductId(productId);
    List<Review> list = reviewMapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("Response size: {}", list.size());

    return list;
  }

  private void internalDeleteReviews(int productId) {
    LOG.debug(
        "deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
  }
}
