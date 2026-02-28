package com.loan.origination.system.microservices.credit.infrastructure.output.client;

import com.loan.origination.system.microservices.credit.application.port.output.CreditBureauPort;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BureauClientAdapter implements CreditBureauPort {

  private static final Logger log = LoggerFactory.getLogger(BureauClientAdapter.class);
  private final Random random = new Random();

  @Override
  public int getCreditScore(String ssn) {
    log.info(
        "Calling external Credit Bureau for SSN ending in: {}", ssn.substring(ssn.length() - 4));

    // Simulate network latency
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Logic: Return a consistent score for a specific SSN for testing,
    // or a random score between 300 and 850.
    return 300 + random.nextInt(551);
  }
}
