package com.loan.origination.system.microservices.product.controller;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.product.ProductAPI;
import com.loan.origination.system.microservices.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProductController implements ProductAPI {

  private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

  private final ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @Override
  public Mono<Product> getProduct(int productId, int delay, int faultPercent) {
    return productService.getProduct(productId, delay, faultPercent);
  }

  @Override
  public Mono<Product> createProduct(Product body) {
    return productService.createProduct(body);
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    return productService.deleteProduct(productId);
  }
}
