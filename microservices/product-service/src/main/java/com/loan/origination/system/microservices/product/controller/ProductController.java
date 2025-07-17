package com.loan.origination.system.microservices.product.controller;

import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.product.ProductAPI;
import com.loan.origination.system.api.exceptions.InvalidInputException;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController implements ProductAPI {

  private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

  private final ServiceUtil serviceUtil;

  @Autowired
  public ProductController(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Product getProduct(int productId) {
    LOG.debug("/product return the found product for productId={}", productId);
    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    if (productId == 13) {
      throw new NotFoundException("No product found for productId: " + productId);
    }
    return new Product(
        productId, "name-" + productId, "The first product", serviceUtil.getServiceAddress());
  }
}
