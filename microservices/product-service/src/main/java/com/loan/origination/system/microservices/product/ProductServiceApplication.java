package com.loan.origination.system.microservices.product;

import brave.baggage.BaggagePropagation;
import brave.baggage.BaggagePropagationCustomizer;
import brave.propagation.B3Propagation;
import brave.propagation.Propagation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@ComponentScan("com.loan.origination.system")
public class ProductServiceApplication {

  private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

  @Bean
  BaggagePropagation.FactoryBuilder myPropagationFactoryBuilder(
      ObjectProvider<BaggagePropagationCustomizer> baggagePropagationCustomizers) {
    Propagation.Factory delegate =
        B3Propagation.newFactoryBuilder().injectFormat(B3Propagation.Format.MULTI).build();
    BaggagePropagation.FactoryBuilder builder = BaggagePropagation.newFactoryBuilder(delegate);
    baggagePropagationCustomizers
        .orderedStream()
        .forEach((customizer) -> customizer.customize(builder));
    return builder;
  }

  public static void main(String[] args) {
    Hooks.enableAutomaticContextPropagation();
    ConfigurableApplicationContext ctx =
        SpringApplication.run(ProductServiceApplication.class, args);
    String mongoDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
    String mongoDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
    LOG.info("Connected to MongoDb: " + mongoDbHost + ":" + mongoDbPort);
  }
}
