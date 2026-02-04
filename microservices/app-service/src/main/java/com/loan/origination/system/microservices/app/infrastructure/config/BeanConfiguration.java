package com.loan.origination.system.microservices.app.infrastructure.config;

import com.loan.origination.system.microservices.app.domain.service.ApplicationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  public ApplicationDomainService loanDomainService() {
    return new ApplicationDomainService();
  }
}
