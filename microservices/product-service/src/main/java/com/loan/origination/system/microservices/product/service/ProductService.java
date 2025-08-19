package com.loan.origination.system.microservices.product.service;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.product.entity.ProductEntity;
import com.loan.origination.system.microservices.product.mapper.ProductMapper;
import com.loan.origination.system.microservices.product.repository.ProductRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
  private final ServiceUtil serviceUtil;
  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Autowired
  public ProductService(
      ServiceUtil serviceUtil, ProductRepository productRepository, ProductMapper productMapper) {
    this.serviceUtil = serviceUtil;
    this.productRepository = productRepository;
    this.productMapper = productMapper;
  }

  public Mono<Product> createProduct(Product product) {

    if (product.getProductId() < 1) {
      throw new InvalidInputException("Invalid productId: " + product.getProductId());
    }

    ProductEntity entity = productMapper.apiToEntity(product);
    Mono<Product> newEntity =
        productRepository
            .save(entity)
            .log(LOG.getName(), Level.FINE)
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->
                    new InvalidInputException(
                        "Duplicate key, Product Id: " + product.getProductId()))
            .map(e -> productMapper.entityToApi(e));
    return newEntity;
  }

  public Mono<Product> getProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.info("Will get product info for id={}", productId);

    Mono<Product> entity =
        productRepository
            .findByProductId(productId)
            .switchIfEmpty(
                Mono.error(new NotFoundException("No product found for productId: " + productId)))
            .log(LOG.getName(), Level.FINE)
            .map(e -> productMapper.entityToApi(e))
            .map(e -> setServiceAddress(e));

    return entity;
  }

  public Mono<Void> deleteProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }

    LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
    return productRepository
        .findByProductId(productId)
        .log(LOG.getName(), Level.FINE)
        .map(e -> productRepository.delete(e))
        .flatMap(e -> e);
  }

  private Product setServiceAddress(Product e) {
    e.setServiceAddress(serviceUtil.getServiceAddress());
    return e;
  }
}
