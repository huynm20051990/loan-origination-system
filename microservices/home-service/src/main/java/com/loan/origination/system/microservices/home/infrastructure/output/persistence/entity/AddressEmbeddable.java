package com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AddressEmbeddable {

  @Column(name = "street", nullable = false)
  private String street;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "state_code", nullable = false)
  private String stateCode;

  @Column(name = "zip_code", nullable = false)
  private String zipCode;

  @Column(name = "country", nullable = false)
  private String country;

  protected AddressEmbeddable() {
    // JPA only
  }

  public AddressEmbeddable(
      String street, String city, String stateCode, String zipCode, String country) {
    this.street = street;
    this.city = city;
    this.stateCode = stateCode;
    this.zipCode = zipCode;
    this.country = country;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getStateCode() {
    return stateCode;
  }

  public String getZipCode() {
    return zipCode;
  }

  public String getCountry() {
    return country;
  }
}
