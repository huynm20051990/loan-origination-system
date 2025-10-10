package com.loan.origination.system.springcloud.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private static final Logger LOG = LoggerFactory.getLogger(SecurityConfig.class);

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeExchange(
            exchange ->
                exchange
                    .pathMatchers(
                        "/headerrouting/**",
                        "/actuator/**",
                        "/oauth2/**",
                        "/login/**",
                        "/error/**",
                        "/openapi/**",
                        "/webjars/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt());

    return http.build();
  }
}
