package com.loan.origination.system.api.core.home.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HomeResponseDTO(
    UUID id,
    BigDecimal price,
    Integer beds,
    Double baths,
    Integer sqft,
    String imageUrl,
    AddressDTO address,
    String status,
    String description) {}
