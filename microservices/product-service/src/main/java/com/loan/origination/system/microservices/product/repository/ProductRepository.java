package com.loan.origination.system.microservices.product.repository;

import com.loan.origination.system.microservices.product.entity.ProductEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository
    extends PagingAndSortingRepository<ProductEntity, String>,
        CrudRepository<ProductEntity, String> {
  Optional<ProductEntity> findByProductId(int productId);
}
