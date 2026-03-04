package com.loan.origination.system.microservices.app.infrastructure.output.persistence;

import com.loan.origination.system.microservices.app.application.port.output.ApplicationRepositoryPort;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.infrastructure.output.persistence.mapper.ApplicationPersistenceMapper;
import com.loan.origination.system.microservices.app.infrastructure.output.persistence.repository.ApplicationRepository;
import java.util.List;
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

  @Override
  public List<Application> findByEmail(String email) {
    return repository.findByEmail(email).stream().map(mapper::toDomain).toList();
  }

  @Override
  public boolean existsById(UUID id) {
    return repository.existsById(id);
  }

  @Override
  public void deleteById(UUID id) {
    repository.deleteById(id);
  }
}
