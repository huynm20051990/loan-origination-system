package com.loan.origination.system.api.core.rating;

public class Rating {
  private final int productId;
  private final int ratingId;
  private final String author;
  private final int rate;
  private final String content;
  private String serviceAddress;

  public Rating() {
    this.productId = 0;
    this.ratingId = 0;
    this.author = null;
    this.rate = 0;
    this.content = null;
    this.serviceAddress = null;
  }

  public Rating(
      int productId, int ratingId, String author, int rate, String content, String serviceAddress) {
    this.productId = productId;
    this.ratingId = ratingId;
    this.author = author;
    this.rate = rate;
    this.content = content;
    this.serviceAddress = serviceAddress;
  }

  public int getProductId() {
    return productId;
  }

  public int getRatingId() {
    return ratingId;
  }

  public String getAuthor() {
    return author;
  }

  public int getRate() {
    return rate;
  }

  public String getContent() {
    return content;
  }

  public String getServiceAddress() {
    return serviceAddress;
  }

  public void setServiceAddress(String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }
}
