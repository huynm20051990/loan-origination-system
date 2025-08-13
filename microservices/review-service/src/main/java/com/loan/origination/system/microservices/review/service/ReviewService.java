package com.loan.origination.system.microservices.review.service;

import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.review.entity.ReviewEntity;
import com.loan.origination.system.microservices.review.mapper.ReviewMapper;
import com.loan.origination.system.microservices.review.repository.ReviewRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;
  private final ServiceUtil serviceUtil;

  @Autowired
  public ReviewService(
      ReviewRepository reviewRepository, ReviewMapper reviewMapper, ServiceUtil serviceUtil) {
    this.reviewRepository = reviewRepository;
    this.reviewMapper = reviewMapper;
    this.serviceUtil = serviceUtil;
  }

  public Review createReview(Review review) {
    try {
      ReviewEntity reviewEntity = reviewMapper.apiToEntity(review);
      ReviewEntity newReviewEntity = reviewRepository.save(reviewEntity);
      LOG.debug(review.toString());
      LOG.debug(
          "createReview: created a review entity: {}/{}",
          review.getProductId(),
          review.getReviewId());
      return reviewMapper.entityToApi(newReviewEntity);
    } catch (DataIntegrityViolationException es) {
      throw new InvalidInputException(
          "Duplicate key, Product Id: "
              + review.getProductId()
              + ", Review Id: "
              + review.getReviewId());
    }
  }

  public List<Review> getReviews(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    List<ReviewEntity> reviewEntities = reviewRepository.findByProductId(productId);
    List<Review> reviews = reviewMapper.entityListToApiList(reviewEntities);
    reviews.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
    LOG.debug("getReviews: response size: {}", reviews.size());
    return reviews;
  }

  public void deleteReviews(int productId) {
    LOG.debug(
        "deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    reviewRepository.deleteAll(reviewRepository.findByProductId(productId));
  }
}
