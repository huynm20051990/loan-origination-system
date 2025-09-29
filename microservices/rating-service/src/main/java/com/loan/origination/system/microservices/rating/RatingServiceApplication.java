package com.loan.origination.system.microservices.rating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.publisher.Hooks;

@SpringBootApplication
@ComponentScan("com.loan.origination.system")
public class RatingServiceApplication {

  public static final Logger LOG = LoggerFactory.getLogger(RatingServiceApplication.class);

  public static void main(String[] args) {
    Hooks.enableAutomaticContextPropagation();
    ConfigurableApplicationContext ctx =
        SpringApplication.run(RatingServiceApplication.class, args);
    String mongoDBHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
    String mongoDBPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
    LOG.info("Connected to MongoDB: " + mongoDBHost + ":" + mongoDBPort);
  }
}
