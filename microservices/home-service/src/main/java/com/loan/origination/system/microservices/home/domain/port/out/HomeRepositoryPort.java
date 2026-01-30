package com.loan.origination.system.microservices.home.domain.port.out;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepositoryPort {

  Home save(Home home);

  Optional<Home> findById(UUID id);

  List<Home> findAll();

  void deleteById(UUID id);
}
