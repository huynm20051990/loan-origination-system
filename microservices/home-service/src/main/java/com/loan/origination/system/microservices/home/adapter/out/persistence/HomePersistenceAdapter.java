package com.loan.origination.system.microservices.home.adapter.out.persistence;

import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.AddressEntity;
import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.HomeEntity;
import com.loan.origination.system.microservices.home.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.repository.AddressRepository;
import com.loan.origination.system.microservices.home.adapter.out.persistence.repository.HomeRepository;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

public class HomePersistenceAdapter implements HomeRepositoryPort {

  private final HomeRepository homeRepository;
  private final AddressRepository addressRepository;
  private final HomePersistenceMapper mapper;

  public HomePersistenceAdapter(
      HomeRepository homeRepository,
      AddressRepository addressRepository,
      HomePersistenceMapper mapper) {
    this.homeRepository = homeRepository;
    this.addressRepository = addressRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Home save(Home home) {
    UUID addressId = UUID.randomUUID();
    AddressEntity addressEntity = mapper.toAddressEntity(home.getAddress(), addressId);
    AddressEntity savedAddress = addressRepository.save(addressEntity);

    HomeEntity homeEntity = mapper.toHomeEntity(home);
    homeEntity.setAddressId(savedAddress.getId());
    HomeEntity savedHome = homeRepository.save(homeEntity);

    return mapper.toDomain(savedHome, savedAddress);
  }

  @Override
  public Optional<Home> findById(UUID id) {
    return homeRepository
        .findById(id)
        .flatMap(
            homeEntity ->
                addressRepository
                    .findById(homeEntity.getAddressId())
                    .map(addressEntity -> mapper.toDomain(homeEntity, addressEntity)));
  }

  @Override
  public List<Home> findAll() {
    return homeRepository.findAll().stream()
        .map(
            homeEntity -> {
              AddressEntity addressEntity =
                  addressRepository
                      .findById(homeEntity.getAddressId())
                      .orElseThrow(
                          () ->
                              new IllegalStateException(
                                  "Address missing for home: " + homeEntity.getId()));
              return mapper.toDomain(homeEntity, addressEntity);
            })
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    homeRepository
        .findById(id)
        .ifPresent(
            home -> {
              homeRepository.deleteById(home.getId());
              addressRepository.deleteById(home.getAddressId());
            });
  }
}
