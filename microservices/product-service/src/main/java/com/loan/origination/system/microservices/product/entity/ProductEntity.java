package com.loan.origination.system.microservices.product.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class ProductEntity {

  @Id
  private String id;
  @Version
  private Integer version;//used to implement optimistic locking
  @Indexed(unique = true)
  private int productId;
  private String name;
  private String description;

  public ProductEntity() {
  }

  public ProductEntity(int productId, String name, String description) {
    this.productId = productId;
    this.name = name;
    this.description = description;
  }

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
