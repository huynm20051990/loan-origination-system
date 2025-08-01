package com.loan.origination.system.microservices.rating.repository;

import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository
    extends CrudRepository<RatingEntity, String>, PagingAndSortingRepository<RatingEntity, String> {
  List<RatingEntity> findByProductId(int productId);
}
