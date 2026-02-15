package com.loan.origination.system.microservices.home.infrastructure.config;

import com.loan.origination.system.microservices.home.adapter.in.web.mapper.HomeWebMapper;
import com.loan.origination.system.microservices.home.adapter.out.persistence.mapper.HomePersistenceMapper;
import com.loan.origination.system.microservices.home.application.service.HomeApplicationService;
import com.loan.origination.system.microservices.home.application.service.HomeSyncService;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.port.out.HomeSearchPort;
import org.springframework.ai.chat.client.ChatClient;
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
      HomeRepositoryPort repositoryPort, HomeSearchPort homeSearchPort) {
    return new HomeApplicationService(repositoryPort, homeSearchPort);
  }

  @Bean
  public HomeSyncService homeSyncService(
      HomeRepositoryPort repositoryPort, HomeSearchPort homeSearchPort) {
    return new HomeSyncService(repositoryPort, homeSearchPort);
  }

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }
}
