package com.loan.origination.system.microservices.home.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import org.springframework.data.annotation.Id;

@Entity
@Table(
    name = "addresses",
    indexes = {
      @Index(name = "idx_address_zip_code", columnList = "zip_code"),
      @Index(name = "idx_address_city_state", columnList = "city, state_code")
    })
public class AddressEntity {

  @Id private UUID id;

  private String street;
  private String city;

  @Column(name = "state_code")
  private String stateCode;

  @Column(name = "zip_code")
  private String zipCode;

  private String country;

  public AddressEntity(
      UUID id, String street, String city, String stateCode, String zipCode, String country) {
    this.id = id;
    this.street = street;
    this.city = city;
    this.stateCode = stateCode;
    this.zipCode = zipCode;
    this.country = country;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStateCode() {
    return stateCode;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public String getZipCode() {
    return zipCode;
  }

  public void setZipCode(String zipCode) {
    this.zipCode = zipCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}
