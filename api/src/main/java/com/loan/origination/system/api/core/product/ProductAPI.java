package com.loan.origination.system.api.core.product;

import org.springframework.web.bind.annotation.*;

public interface ProductAPI {

  /**
   * Sample usage: "curl $HOST:$PORT/product/1".
   *
   * @param productId Id of the product
   * @return the product, if found, else null
   */
  @GetMapping(value = "/product/{productId}", produces = "application/json")
  Product getProduct(@PathVariable int productId);

  /**
   * Sample usage, see below.
   *
   * <p>curl -X POST $HOST:$PORT/product \ -H "Content-Type: application/json" --data \
   * '{"productId":123,"name":"product 123","description":"product 123"}'
   *
   * @param product A JSON representation of the new product
   * @return A JSON representation of the newly created product
   */
  @PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
  Product createProduct(@RequestBody Product product);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/product/1".
   *
   * @param productId Id of the product
   */
  @DeleteMapping(value = "/product/{productId}")
  void deleteProduct(@PathVariable int productId);
}
