package com.loan.origination.system.microservices.home.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Home {
  private final UUID id;
  private final Address address;
  private BigDecimal price;
  private final Integer beds;
  private final Double baths;
  private final Integer sqft;
  private final String imageUrl;
  private HomeStatus status;
  private float[] embedding;

  public Home(
      UUID id,
      Address address,
      BigDecimal price,
      Integer beds,
      Double baths,
      Integer sqft,
      String imageUrl,
      HomeStatus status,
      float[] embedding) {
    this.id = id;
    this.address = address;
    this.price = price;
    this.beds = beds;
    this.baths = baths;
    this.sqft = sqft;
    this.imageUrl = imageUrl;
    this.status = status;
    this.embedding = embedding;
  }

  public void markAsSold() {
    this.status = HomeStatus.SOLD;
  }

  public void updatePrice(BigDecimal newPrice) {
    if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be greater than zero");
    }
    this.price = newPrice;
  }

  public UUID getId() {
    return id;
  }

  public Address getAddress() {
    return address;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Integer getBeds() {
    return beds;
  }

  public Double getBaths() {
    return baths;
  }

  public Integer getSqft() {
    return sqft;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public HomeStatus getStatus() {
    return status;
  }

  public void updateEmbedding(float[] newEmbedding) {
    this.embedding = newEmbedding;
  }

  public float[] getEmbedding() {
    return embedding;
  }
}
