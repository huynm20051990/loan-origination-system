package com.loan.origination.system.microservices.home.infrastructure.input.rest.mapper;

import com.loan.origination.system.api.core.home.dto.AddressDTO;
import com.loan.origination.system.api.core.home.dto.HomeRequestDTO;
import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.vo.Address;
import com.loan.origination.system.microservices.home.domain.vo.HomeStatus;
import java.util.UUID;

/**
 * Maps between the external API DTOs and the internal Domain Model. Handles the transition between
 * Record-based accessors and Class-based getters.
 */
public class HomeWebMapper {

  /** Converts an incoming Request DTO (Record) to a Domain Home entity (Class). */
  public Home toDomain(HomeRequestDTO dto) {
    // Mapping FROM Record: Use accessor methods like street(), city()
    Address domainAddress =
        new Address(
            dto.address().street(),
            dto.address().city(),
            dto.address().state(),
            dto.address().zipCode(),
            dto.address().country());

    return new Home(
        UUID.randomUUID(), // Generates identity for a new listing
        domainAddress,
        dto.price(),
        dto.beds(),
        dto.baths(),
        dto.sqft(),
        dto.imageUrl(),
        HomeStatus.AVAILABLE,
        dto.description(),
        null);
  }

  public HomeResponseDTO toResponse(Home home) {
    // 1. Map Domain Address (Record) to AddressDTO (Record)
    Address domainAddr = home.getAddress();

    AddressDTO addressDto =
        new AddressDTO(
            domainAddr.street(),
            domainAddr.city(),
            domainAddr.state(),
            domainAddr.zipCode(),
            domainAddr.country());

    // 2. Pass the nested DTO into the main Response DTO
    return new HomeResponseDTO(
        home.getId(),
        home.getPrice(),
        home.getBeds(),
        home.getBaths(),
        home.getSqft(),
        home.getImageUrl(),
        addressDto,
        home.getStatus().name(),
        home.getDescription());
  }
}
