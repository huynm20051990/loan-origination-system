package com.loan.origination.system.microservices.home.domain.port.out;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;
import java.util.UUID;

public interface HomeSearchPort {
  void indexHome(Home home);

  List<UUID> search(String query);
}
