package com.loan.origination.system.microservices.home.adapter.in.web;

import com.loan.origination.system.api.core.home.dto.HomeRequestDTO;
import com.loan.origination.system.api.core.home.dto.HomeResponseDTO;
import com.loan.origination.system.api.core.home.v1.HomeAPI;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.home.adapter.in.web.mapper.HomeWebMapper;
import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.in.AddHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.DeleteHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.GetHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.SearchHomeUseCase;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeWebAdapterController implements HomeAPI {

  private static final Logger LOG = LoggerFactory.getLogger(HomeWebAdapterController.class);

  private final AddHomeUseCase addHomeUseCase;
  private final GetHomeUseCase getHomeUseCase;
  private final DeleteHomeUseCase deleteHomeUseCase;
  private final SearchHomeUseCase searchHomeUseCase;
  private final HomeWebMapper mapper;

  public HomeWebAdapterController(
      AddHomeUseCase addHomeUseCase,
      GetHomeUseCase getHomeUseCase,
      DeleteHomeUseCase deleteHomeUseCase,
      SearchHomeUseCase searchHomeUseCase,
      HomeWebMapper mapper) {
    this.addHomeUseCase = addHomeUseCase;
    this.getHomeUseCase = getHomeUseCase;
    this.deleteHomeUseCase = deleteHomeUseCase;
    this.searchHomeUseCase = searchHomeUseCase;
    this.mapper = mapper;
  }

  @Override
  public HomeResponseDTO addHome(HomeRequestDTO request) {
    LOG.info(request.toString());
    var homeDomain = addHomeUseCase.execute(mapper.toDomain(request));
    return mapper.toResponse(homeDomain);
  }

  @Override
  public HomeResponseDTO getHome(UUID id) {
    Home home =
        getHomeUseCase
            .getById(id)
            .orElseThrow(() -> new NotFoundException("Home not found with ID: " + id));

    return mapper.toResponse(home);
  }

  @Override
  public List<HomeResponseDTO> getAllHomes() {
    // Convert the List of Domain objects to a List of DTOs using Java Streams
    return getHomeUseCase.getAll().stream().map(mapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public List<HomeResponseDTO> searchHomes(String query) {
    LOG.info("Received AI search request: {}", query);
    // 1. Call the AI search use case
    List<Home> results = searchHomeUseCase.search(query);
    // 2. Map the domain results back to the public Response DTOs
    return results.stream().map(mapper::toResponse).collect(Collectors.toList());
  }

  @Override
  public void deleteHome(UUID id) {
    deleteHomeUseCase.execute(id);
  }
}
