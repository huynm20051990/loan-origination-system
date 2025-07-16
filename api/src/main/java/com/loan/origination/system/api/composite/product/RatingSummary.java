package com.loan.origination.system.api.composite.product;

public class RatingSummary {
  private final int ratingId;
  private final String author;
  private final int rate;

  public RatingSummary(int ratingId, String author, int rate) {
    this.ratingId = ratingId;
    this.author = author;
    this.rate = rate;
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
}
