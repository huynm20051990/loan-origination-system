package com.loan.origination.system.microservices.app.domain.port.out;

import com.loan.origination.system.microservices.app.domain.model.Application;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepositoryPort {
  void save(Application loanApplication);

  Optional<Application> findById(UUID id);

  Optional<Application> findByApplicationNumber(String applicationNumber);

  List<Application> findByEmail(String email);
}
