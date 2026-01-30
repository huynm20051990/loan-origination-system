package com.loan.origination.system.api.core.home.dto;

public record AddressDTO(
    String street, String city, String state, String zipCode, String country) {}
