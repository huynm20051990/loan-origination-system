package com.loan.origination.system.microservices.review.repository;

import com.loan.origination.system.microservices.review.entity.ReviewEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Integer> {

  @Transactional(readOnly = true)
  List<ReviewEntity> findByProductId(int productId);
}
