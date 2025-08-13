package com.loan.origination.system.api.core.rating;

public class Rating {
  private int productId;
  private int ratingId;
  private String author;
  private int rate;
  private String content;
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

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public void setRatingId(int ratingId) {
    this.ratingId = ratingId;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setServiceAddress(String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }
}
