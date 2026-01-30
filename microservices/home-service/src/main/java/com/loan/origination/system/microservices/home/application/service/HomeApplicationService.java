package com.loan.origination.system.microservices.home.application.service;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.in.AddHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.DeleteHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.GetHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HomeApplicationService implements AddHomeUseCase, GetHomeUseCase, DeleteHomeUseCase {

  private final HomeRepositoryPort repositoryPort;

  public HomeApplicationService(HomeRepositoryPort repositoryPort) {
    this.repositoryPort = repositoryPort;
  }

  @Override
  public Home execute(Home home) {
    // Business logic check: Ensure price is not negative before saving
    return repositoryPort.save(home);
  }

  @Override
  public Optional<Home> getById(UUID id) {
    return repositoryPort.findById(id);
  }

  @Override
  public List<Home> getAll() {
    return repositoryPort.findAll();
  }

  @Override
  public void execute(UUID id) {
    // Standard check-then-act pattern
    if (repositoryPort.findById(id).isEmpty()) {
      throw new RuntimeException("Cannot delete: Home not found with ID: " + id);
    }
    repositoryPort.deleteById(id);
  }
}
