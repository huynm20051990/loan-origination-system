package com.loan.origination.system.microservices.home.infrastructure.config;

import com.loan.origination.system.microservices.home.adapter.in.web.mapper.HomeWebMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.HomePersistenceAdapter;
import com.loan.origination.system.microservices.home.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.repository.AddressRepository;
import com.loan.origination.system.microservices.home.adapter.out.persistence.repository.HomeRepository;
import com.loan.origination.system.microservices.home.application.service.HomeApplicationService;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Infrastructure configuration class. This is where we wire the Hexagonal components together. */
@Configuration
public class BeanConfiguration {

  // --- Mappers ---

  @Bean
  public HomePersistenceMapper homePersistenceMapper() {
    return new HomePersistenceMapper();
  }

  @Bean
  public HomeWebMapper homeWebMapper() {
    return new HomeWebMapper();
  }

  // --- Outbound Adapters (Persistence) ---

  @Bean
  public HomeRepositoryPort homeRepositoryPort(
      HomeRepository homeRepo, AddressRepository addressRepo, HomePersistenceMapper mapper) {
    return new HomePersistenceAdapter(homeRepo, addressRepo, mapper);
  }

  // --- Application Services (The Use Case implementations) ---

  @Bean
  public HomeApplicationService homeApplicationService(HomeRepositoryPort repositoryPort) {
    return new HomeApplicationService(repositoryPort);
  }

  /* Note: The HomeOrderController is annotated with @RestController,
     so Spring finds it automatically. It will inject the
     HomeApplicationService beans created here into the controller's
     Use Case ports.
  */
}
