package com.loan.origination.system.api.core.product;

public class Product {
  private final int productId;
  private final String name;
  private final String description;
  private final String serviceAddress;

  public Product() {
    this.productId = 0;
    this.name = null;
    this.description = null;
    this.serviceAddress = null;
  }

  public Product(int productId, String name, String description, String serviceAddress) {
    this.productId = productId;
    this.name = name;
    this.description = description;
    this.serviceAddress = serviceAddress;
  }

  public int getProductId() {
    return productId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getServiceAddress() {
    return serviceAddress;
  }
}
