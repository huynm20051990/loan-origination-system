package com.loan.origination.system.microservices.home.adapter.out.persistence;

import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.HomeEntity;
import com.loan.origination.system.microservices.home.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.repository.HomeRepository;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class HomePersistenceAdapter implements HomeRepositoryPort {

  private final HomeRepository homeRepository;
  private final HomePersistenceMapper mapper;

  public HomePersistenceAdapter(HomeRepository homeRepository, HomePersistenceMapper mapper) {
    this.homeRepository = homeRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public Home save(Home home) {
    HomeEntity entity = mapper.toEntity(home);
    HomeEntity saved = homeRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Home> findById(UUID id) {
    return homeRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Home> findAll() {
    return homeRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteById(UUID id) {
    homeRepository.deleteById(id);
  }
}
