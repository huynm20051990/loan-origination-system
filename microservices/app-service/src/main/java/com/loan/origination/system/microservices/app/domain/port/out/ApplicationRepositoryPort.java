package com.loan.origination.system.microservices.app.domain.port.out;

import com.loan.origination.system.microservices.app.domain.model.Application;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepositoryPort {
  /** Persists the loan application to the data store. */
  void save(Application loanApplication);

  /** Retrieves a loan application by its technical UUID. */
  Optional<Application> findById(UUID id);

  /** Retrieves a loan application by its human-readable application number. */
  Optional<Application> findByApplicationNumber(String applicationNumber);
}
