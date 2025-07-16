package com.loan.origination.system.api.composite.product;

import java.util.List;

public class ProductAggregate {
  private final int productId;
  private final String name;
  private final String description;
  private final List<RatingSummary> ratings;
  private final List<ReviewSummary> reviews;
  private final ServiceAddresses serviceAddresses;

  public ProductAggregate(
      int productId,
      String name,
      String description,
      List<RatingSummary> ratings,
      List<ReviewSummary> reviews,
      ServiceAddresses serviceAddresses) {
    this.productId = productId;
    this.name = name;
    this.description = description;
    this.ratings = ratings;
    this.reviews = reviews;
    this.serviceAddresses = serviceAddresses;
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

  public List<RatingSummary> getRatings() {
    return ratings;
  }

  public List<ReviewSummary> getReviews() {
    return reviews;
  }

  public ServiceAddresses getServiceAddresses() {
    return serviceAddresses;
  }
}
