package com.loan.origination.system.microservices.product.service;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.product.entity.ProductEntity;
import com.loan.origination.system.microservices.product.mapper.ProductMapper;
import com.loan.origination.system.microservices.product.repository.ProductRepository;
import com.loan.origination.system.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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

  public Product createProduct(Product product) {
    try {
      ProductEntity entity = productMapper.apiToEntity(product);
      ProductEntity newEntity = productRepository.save(entity);
      LOG.debug("createProduct: entity created for productId: {}", product.getProductId());
      return productMapper.entityToApi(newEntity);
    } catch (DuplicateKeyException dke) {
      throw new InvalidInputException("Duplicate key, Product Id: " + product.getProductId());
    }
  }

  public Product getProduct(int productId) {
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    ProductEntity entity =
        productRepository
            .findByProductId(productId)
            .orElseThrow(
                () -> new NotFoundException("No product found for productId: " + productId));
    Product response = productMapper.entityToApi(entity);
    response.setServiceAddress(serviceUtil.getServiceAddress());
    LOG.debug("getProduct: found productId: {}", response.getProductId());
    return response;
  }

  public void deleteProduct(int productId) {
    LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
    productRepository.findByProductId(productId).ifPresent(e -> productRepository.delete(e));
  }
}
