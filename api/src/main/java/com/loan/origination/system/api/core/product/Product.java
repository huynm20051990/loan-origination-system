package com.loan.origination.system.api.core.product;

public class Product {
  private int productId;
  private String name;
  private String description;
  private String serviceAddress;

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

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setServiceAddress(String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }
}
