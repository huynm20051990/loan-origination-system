package com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
    name = "homes",
    indexes = {
      @Index(name = "idx_home_status", columnList = "status"),
      @Index(name = "idx_home_price", columnList = "price"),
      @Index(name = "idx_home_city", columnList = "city"),
      @Index(name = "idx_home_zip", columnList = "zip_code")
    })
public class HomeEntity {

  @Id
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(nullable = false)
  private BigDecimal price;

  private Integer beds;
  private Double baths;
  private Integer sqft;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(nullable = false)
  private String status;

  @Embedded private AddressEmbeddable address;

  private String description;

  @Column(columnDefinition = "vector(768)")
  private float[] embedding;

  public HomeEntity() {}

  public HomeEntity(
      UUID id,
      BigDecimal price,
      Integer beds,
      Double baths,
      Integer sqft,
      String imageUrl,
      String status,
      AddressEmbeddable address,
      String description,
      float[] embedding) {
    this.id = id;
    this.price = price;
    this.beds = beds;
    this.baths = baths;
    this.sqft = sqft;
    this.imageUrl = imageUrl;
    this.status = status;
    this.address = address;
    this.description = description;
    this.embedding = embedding;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Integer getBeds() {
    return beds;
  }

  public void setBeds(Integer beds) {
    this.beds = beds;
  }

  public Double getBaths() {
    return baths;
  }

  public void setBaths(Double baths) {
    this.baths = baths;
  }

  public Integer getSqft() {
    return sqft;
  }

  public void setSqft(Integer sqft) {
    this.sqft = sqft;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public AddressEmbeddable getAddress() {
    return address;
  }

  public void setAddress(AddressEmbeddable address) {
    this.address = address;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public float[] getEmbedding() {
    return embedding;
  }

  public void setEmbedding(float[] embedding) {
    this.embedding = embedding;
  }
}
