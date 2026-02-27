package com.loan.origination.system.microservices.home.infrastructure.config;

import com.loan.origination.system.microservices.home.application.port.output.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.application.service.HomeApplicationService;
import com.loan.origination.system.microservices.home.infrastructure.input.rest.mapper.HomeWebMapper;
import com.loan.origination.system.microservices.home.infrastructure.output.persistence.mapper.HomePersistenceMapper;
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
  public HomeApplicationService homeApplicationService(HomeRepositoryPort repositoryPort) {
    return new HomeApplicationService(repositoryPort);
  }

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    return builder.build();
  }
}
