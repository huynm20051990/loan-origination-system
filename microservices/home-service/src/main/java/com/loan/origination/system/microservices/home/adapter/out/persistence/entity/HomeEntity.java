package com.loan.origination.system.microservices.home.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.annotation.Id;

@Entity
@Table(
    name = "homes",
    indexes = {
      @Index(name = "idx_home_status", columnList = "status"),
      @Index(name = "idx_home_price", columnList = "price"),
      @Index(name = "idx_home_address_id", columnList = "address_id")
    })
public class HomeEntity {

  @Id private UUID id;

  private BigDecimal price;
  private Integer beds;
  private Double baths;
  private Integer sqft;

  @Column(name = "image_url")
  private String imageUrl;

  private String status;

  @Column(name = "address_id")
  private UUID addressId;

  public HomeEntity() {}

  public HomeEntity(
      UUID id,
      BigDecimal price,
      Integer beds,
      Double baths,
      Integer sqft,
      String imageUrl,
      String status,
      UUID addressId) {
    this.id = id;
    this.price = price;
    this.beds = beds;
    this.baths = baths;
    this.sqft = sqft;
    this.imageUrl = imageUrl;
    this.status = status;
    this.addressId = addressId;
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

  public UUID getAddressId() {
    return addressId;
  }

  public void setAddressId(UUID addressId) {
    this.addressId = addressId;
  }
}
