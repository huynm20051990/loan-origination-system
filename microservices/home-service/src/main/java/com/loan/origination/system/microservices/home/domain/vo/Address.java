package com.loan.origination.system.microservices.home.domain.vo;

import com.loan.origination.system.api.exceptions.InvalidInputException;

public record Address(String street, String city, String state, String zipCode, String country) {
  // Basic validation can be handled in the constructor
  public Address {
    if (street == null || street.isBlank()) {
      throw new InvalidInputException("Street cannot be empty");
    }
    if (city == null || city.isBlank()) {
      throw new InvalidInputException("City cannot be empty");
    }
    if (state == null || state.length() != 2) {
      throw new InvalidInputException("State must be a 2-letter code");
    }
  }

  public String toFullAddressString() {
    return String.format("%s, %s, %s %s, %s", street, city, state, zipCode, country);
  }
}
