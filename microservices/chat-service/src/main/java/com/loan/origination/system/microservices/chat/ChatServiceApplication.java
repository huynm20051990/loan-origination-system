package com.loan.origination.system.microservices.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Chat Service.
 *
 * <p>This service handles AI-powered chat interactions for the loan origination platform,
 * streaming responses via Server-Sent Events backed by Google Gemini and Cassandra memory.
 */
@SpringBootApplication
public class ChatServiceApplication {

  /**
   * Bootstraps the Chat Service Spring Boot application.
   *
   * @param args command-line arguments passed to the application
   */
  public static void main(String[] args) {
    SpringApplication.run(ChatServiceApplication.class, args);
  }
}
