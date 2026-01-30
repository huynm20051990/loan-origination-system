package com.loan.origination.system.microservices.home.domain.port.in;

import com.loan.origination.system.microservices.home.domain.model.Home;

public interface AddHomeUseCase {
  Home execute(Home home);
}
