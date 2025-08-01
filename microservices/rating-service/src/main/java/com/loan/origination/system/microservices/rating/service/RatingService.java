package com.loan.origination.system.microservices.rating.service;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import com.loan.origination.system.microservices.rating.mapper.RatingMapper;
import com.loan.origination.system.microservices.rating.repository.RatingRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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

  public Rating createRating(Rating rating) {
    try {
      RatingEntity entity = ratingMapper.apiToEntity(rating);
      RatingEntity newEntity = ratingRepository.save(entity);
      Rating response = ratingMapper.entityToApi(newEntity);
      LOG.debug(
          "createRating: created a rating entity: {}/{}",
          rating.getProductId(),
          rating.getRatingId());
      return response;
    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException(
          "Duplicate key, Product Id: "
              + rating.getProductId()
              + ", Rating Id: "
              + rating.getRatingId());
    }
  }

  public List<Rating> getRatings(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    List<RatingEntity> entityList = ratingRepository.findByProductId(productId);
    List<Rating> list = ratingMapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));
    LOG.debug("getRatings: response size: {}", list.size());
    return list;
  }

  public void deleteRatings(int productId) {
    LOG.debug("deleteRatings: tries to delete ratings for productId: {}", productId);
    ratingRepository.deleteAll(ratingRepository.findByProductId(productId));
  }
}
