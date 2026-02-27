package com.loan.origination.system.microservices.home.infrastructure.input.rest;

import com.loan.origination.system.api.core.home.dto.HomeRequestDTO;
import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import com.loan.origination.system.api.core.home.v1.HomeAPI;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.home.application.port.input.HomeUseCase;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.infrastructure.input.rest.mapper.HomeWebMapper;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeWebAdapterController implements HomeAPI {

  private static final Logger LOG = LoggerFactory.getLogger(HomeWebAdapterController.class);

  private final HomeUseCase homeUseCase;
  private final HomeWebMapper mapper;

  public HomeWebAdapterController(HomeUseCase homeUseCase, HomeWebMapper mapper) {
    this.homeUseCase = homeUseCase;
    this.mapper = mapper;
  }

  @Override
  public HomeResponseDTO addHome(HomeRequestDTO request) {
    LOG.info(request.toString());
    var homeDomain = homeUseCase.addHome(mapper.toDomain(request));
    return mapper.toResponse(homeDomain);
  }

  @Override
  public HomeResponseDTO getHome(UUID id) {
    Home home =
        homeUseCase
            .getById(id)
            .orElseThrow(() -> new NotFoundException("Home not found with ID: " + id));

    return mapper.toResponse(home);
  }

  @Override
  public List<HomeResponseDTO> getAllHomes() {
    return homeUseCase.getAll().stream().map(mapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public List<HomeResponseDTO> searchHomes(String query) {
    LOG.info("Received AI search request: {}", query);
    List<Home> results = homeUseCase.search(query);
    return results.stream().map(mapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public void deleteHome(UUID id) {
    homeUseCase.deleteHome(id);
  }
}
