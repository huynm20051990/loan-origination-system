package com.loan.origination.system.microservices.app.adapter.out.persistence;

import com.loan.origination.system.microservices.app.adapter.out.persistence.mapper.ApplicationPersistenceMapper;
import com.loan.origination.system.microservices.app.adapter.out.persistence.repository.ApplicationRepository;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.port.out.ApplicationRepositoryPort;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPersistenceAdapter implements ApplicationRepositoryPort {

  private final ApplicationRepository repository;
  private final ApplicationPersistenceMapper mapper;

  public ApplicationPersistenceAdapter(
      ApplicationRepository repository, ApplicationPersistenceMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Override
  public void save(Application loanApplication) {
    repository.save(mapper.toEntity(loanApplication));
  }

  @Override
  public Optional<Application> findById(UUID id) {
    return repository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Application> findByApplicationNumber(String applicationNumber) {
    return repository.findByApplicationNumber(applicationNumber).map(mapper::toDomain);
  }
}
