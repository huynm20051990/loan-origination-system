package com.loan.origination.system.microservices.home.application.port.output;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeRepositoryPort {

  Home save(Home home);

  Optional<Home> findById(UUID id);

  List<Home> findAll();

  void deleteById(UUID id);

  void indexHome(Home home);

  List<UUID> search(String query);
}
