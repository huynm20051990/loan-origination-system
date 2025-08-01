package com.loan.origination.system.microservices.rating.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ratings")
@CompoundIndex(name = "prod-rat-id", unique = true, def = "{'productId': 1, 'ratingId': 1}")
public class RatingEntity {

  @Id private String id;
  @Version private Integer version;
  private int productId;
  private int ratingId;
  private String author;
  private int rate;
  private String content;

  public RatingEntity() {}

  public RatingEntity(int productId, int ratingId, String author, int rate, String content) {
    this.productId = productId;
    this.ratingId = ratingId;
    this.author = author;
    this.rate = rate;
    this.content = content;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public int getProductId() {
    return productId;
  }

  public void setProductId(int productId) {
    this.productId = productId;
  }

  public int getRatingId() {
    return ratingId;
  }

  public void setRatingId(int ratingId) {
    this.ratingId = ratingId;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public int getRate() {
    return rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
