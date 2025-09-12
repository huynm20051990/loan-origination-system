package com.loan.origination.system.microservices.composite.product;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  @Bean
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange(
            exchange ->
                exchange
                    .pathMatchers("/openapi/**", "/webjars/**", "/actuator/**")
                    .permitAll()
                    .pathMatchers(HttpMethod.POST, "/product-composite/**")
                    .hasAuthority("SCOPE_product:write")
                    .pathMatchers(HttpMethod.DELETE, "/product-composite/**")
                    .hasAuthority("SCOPE_product:write")
                    .pathMatchers(HttpMethod.GET, "/product-composite/**")
                    .hasAuthority("SCOPE_product:read")
                    .anyExchange()
                    .authenticated())
        // ✅ use the new Customizer-based API
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    return http.build();
  }
}
