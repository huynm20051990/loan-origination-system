package com.loan.origination.system.api.composite.product;

public class RatingSummary {
  private final int ratingId;
  private final String author;
  private final int rate;
  private final String content;

  public RatingSummary(int ratingId, String author, int rate, String content) {
    this.ratingId = ratingId;
    this.author = author;
    this.rate = rate;
    this.content = content;
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
}
