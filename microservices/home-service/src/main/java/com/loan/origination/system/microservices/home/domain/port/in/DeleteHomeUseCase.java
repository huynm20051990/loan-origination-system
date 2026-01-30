package com.loan.origination.system.microservices.home.domain.port.in;

import java.util.UUID;

public interface DeleteHomeUseCase {
  void execute(UUID id);
}
