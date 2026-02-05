package com.loan.origination.system.microservices.credit.domain.port.out;

public interface CreditBureauPort {
  int getCreditScore(String ssn);
}
