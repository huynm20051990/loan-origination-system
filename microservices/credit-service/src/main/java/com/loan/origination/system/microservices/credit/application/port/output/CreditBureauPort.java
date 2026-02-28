package com.loan.origination.system.microservices.credit.application.port.output;

public interface CreditBureauPort {
  int getCreditScore(String ssn);
}
