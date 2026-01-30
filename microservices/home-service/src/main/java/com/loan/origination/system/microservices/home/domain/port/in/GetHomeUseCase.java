package com.loan.origination.system.microservices.home.domain.port.in;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetHomeUseCase {
  Optional<Home> getById(UUID id);

  List<Home> getAll();
}
