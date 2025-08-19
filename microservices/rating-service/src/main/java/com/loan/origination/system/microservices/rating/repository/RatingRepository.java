package com.loan.origination.system.microservices.rating.repository;

import com.loan.origination.system.microservices.rating.entity.RatingEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RatingRepository extends ReactiveCrudRepository<RatingEntity, String> {
  Flux<RatingEntity> findByProductId(int productId);
}
