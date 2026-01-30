package com.loan.origination.system.microservices.home.adapter.out.persistence.mapper;

import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.AddressEntity;
import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.HomeEntity;
import com.loan.origination.system.microservices.home.domain.model.Address;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.model.HomeStatus;
import java.util.UUID;

public class HomePersistenceMapper {

  public HomeEntity toHomeEntity(Home home) {
    HomeEntity entity = new HomeEntity();
    entity.setId(home.getId());
    entity.setPrice(home.getPrice());
    entity.setBeds(home.getBeds());
    entity.setBaths(home.getBaths());
    entity.setSqft(home.getSqft());
    entity.setImageUrl(home.getImageUrl());
    entity.setStatus(home.getStatus().name());
    return entity;
  }

  public AddressEntity toAddressEntity(Address domainAddress, UUID addressId) {
    return new AddressEntity(
        addressId,
        domainAddress.street(),
        domainAddress.city(),
        domainAddress.state(), // Mapped to stateCode in Entity
        domainAddress.zipCode(),
        domainAddress.country());
  }

  public Home toDomain(HomeEntity homeEntity, AddressEntity addressEntity) {
    Address domainAddress =
        new Address(
            addressEntity.getStreet(),
            addressEntity.getCity(),
            addressEntity.getStateCode(),
            addressEntity.getZipCode(),
            addressEntity.getCountry());

    return new Home(
        homeEntity.getId(),
        domainAddress,
        homeEntity.getPrice(),
        homeEntity.getBeds(),
        homeEntity.getBaths(),
        homeEntity.getSqft(),
        homeEntity.getImageUrl(),
        HomeStatus.valueOf(homeEntity.getStatus()));
  }
}
