package com.loan.origination.system.microservices.app.infrastructure.config;

import com.loan.origination.system.microservices.app.domain.service.DomainApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

  @Bean
  public DomainApplicationService loanDomainService() {
    return new DomainApplicationService();
  }
}
