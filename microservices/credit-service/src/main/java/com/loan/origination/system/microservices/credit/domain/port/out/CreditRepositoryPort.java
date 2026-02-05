package com.loan.origination.system.microservices.credit.domain.port.out;

import com.loan.origination.system.microservices.credit.domain.model.CreditReport;

public interface CreditRepositoryPort {
  void save(CreditReport creditReport, String aggregateId);
}
