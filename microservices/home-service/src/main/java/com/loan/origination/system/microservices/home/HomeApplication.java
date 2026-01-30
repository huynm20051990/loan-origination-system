package com.loan.origination.system.microservices.home;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.loan.origination.system")
public class HomeApplication {

  public static void main(String[] args) {
    SpringApplication.run(HomeApplication.class, args);
  }
}
