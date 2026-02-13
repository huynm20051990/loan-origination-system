package com.loan.origination.system.api.core.home.dto;

import java.math.BigDecimal;

public record HomeRequestDTO(
    BigDecimal price,
    Integer beds,
    Double baths,
    Integer sqft,
    String imageUrl,
    AddressDTO address,
    String description) {}
