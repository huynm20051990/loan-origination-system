package com.loan.origination.system.microservices.home.infrastructure.output.persistence.mapper;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.vo.Address;
import com.loan.origination.system.microservices.home.domain.vo.HomeStatus;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity.AddressEmbeddable;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.entity.HomeEntity;

public class HomePersistenceMapper {

  public HomeEntity toEntity(Home home) {
    HomeEntity entity = new HomeEntity();

    entity.setId(home.getId());
    entity.setPrice(home.getPrice());
    entity.setBeds(home.getBeds());
    entity.setBaths(home.getBaths());
    entity.setSqft(home.getSqft());
    entity.setImageUrl(home.getImageUrl());
    entity.setStatus(home.getStatus().name());
    entity.setEmbedding(home.getEmbedding());

    entity.setAddress(
        new AddressEmbeddable(
            home.getAddress().street(),
            home.getAddress().city(),
            home.getAddress().state(),
            home.getAddress().zipCode(),
            home.getAddress().country()));

    return entity;
  }

  public Home toDomain(HomeEntity entity) {
    AddressEmbeddable a = entity.getAddress();

    Address address =
        new Address(a.getStreet(), a.getCity(), a.getStateCode(), a.getZipCode(), a.getCountry());

    return new Home(
        entity.getId(),
        address,
        entity.getPrice(),
        entity.getBeds(),
        entity.getBaths(),
        entity.getSqft(),
        entity.getImageUrl(),
        HomeStatus.valueOf(entity.getStatus()),
        entity.getDescription(),
        entity.getEmbedding());
  }
}
