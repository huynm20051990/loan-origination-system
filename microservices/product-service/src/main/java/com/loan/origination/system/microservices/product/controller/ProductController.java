package com.loan.origination.system.microservices.product.controller;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.product.ProductAPI;
import com.loan.origination.system.microservices.product.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductAPI {

  private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

  private final ProductService productService;

  @Autowired
  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @Override
  public Product getProduct(int productId) {
    return productService.getProduct(productId);
  }

  @Override
  public Product createProduct(Product product) {
    return productService.createProduct(product);
  }

  @Override
  public void deleteProduct(int productId) {
    productService.deleteProduct(productId);
  }
}
