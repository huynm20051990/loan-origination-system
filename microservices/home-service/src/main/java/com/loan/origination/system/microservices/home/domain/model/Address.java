package com.loan.origination.system.microservices.home.domain.model;

public record Address(String street, String city, String state, String zipCode, String country) {
  // Basic validation can be handled in the constructor
  public Address {
    if (street == null || street.isBlank()) {
      throw new IllegalArgumentException("Street cannot be empty");
    }
    if (city == null || city.isBlank()) {
      throw new IllegalArgumentException("City cannot be empty");
    }
    if (state == null || state.length() != 2) {
      throw new IllegalArgumentException("State must be a 2-letter code");
    }
  }

  public String toFullAddressString() {
    return String.format("%s, %s, %s %s, %s", street, city, state, zipCode, country);
  }
}
