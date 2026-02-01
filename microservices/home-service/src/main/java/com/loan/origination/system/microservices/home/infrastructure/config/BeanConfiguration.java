package com.loan.origination.system.microservices.home.infrastructure.config;

import com.loan.origination.system.microservices.home.adapter.in.web.mapper.HomeWebMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.application.service.HomeApplicationService;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  public HomePersistenceMapper homePersistenceMapper() {
    return new HomePersistenceMapper();
  }

  @Bean
  public HomeWebMapper homeWebMapper() {
    return new HomeWebMapper();
  }

  @Bean
  public HomeApplicationService homeApplicationService(
      HomeRepositoryPort repositoryPort, ObjectProvider<EmbeddingModel> embeddingModelProvider) {

    // This will NOT crash if the bean is missing
    EmbeddingModel model = embeddingModelProvider.getIfAvailable();

    return new HomeApplicationService(repositoryPort, model);
  }
}
