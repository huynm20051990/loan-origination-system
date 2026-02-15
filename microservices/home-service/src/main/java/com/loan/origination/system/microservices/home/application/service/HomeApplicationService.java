package com.loan.origination.system.microservices.home.application.service;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.in.AddHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.DeleteHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.GetHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.SearchHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.port.out.HomeSearchPort;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomeApplicationService
    implements AddHomeUseCase, GetHomeUseCase, DeleteHomeUseCase, SearchHomeUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(HomeApplicationService.class);

  private final HomeRepositoryPort repositoryPort;
  private final HomeSearchPort homeSearchPort;

  public HomeApplicationService(HomeRepositoryPort repositoryPort, HomeSearchPort homeSearchPort) {
    this.repositoryPort = repositoryPort;
    this.homeSearchPort = homeSearchPort;
  }

  @Override
  public Home addHome(Home home) {
    LOG.info("Saving home and generating embeddings: {}", home);

    // 1. Save to the standard relational database
    Home savedHome = repositoryPort.save(home);

    // 2. Prepare the text for embedding.
    // The more descriptive this string, the better the AI search results!
    String searchContent =
        String.format(
            "A home located at %s, %s, %s. Features: %d bedrooms, %.1f bathrooms, %d square feet. Description: %s",
            savedHome.getAddress().street(),
            savedHome.getAddress().city(),
            savedHome.getAddress().state(),
            savedHome.getBeds(),
            savedHome.getBaths(),
            savedHome.getSqft(),
            savedHome.getDescription());

    homeSearchPort.indexHome(savedHome);

    return savedHome;
  }

  @Override
  public Optional<Home> getById(UUID id) {
    return repositoryPort.findById(id);
  }

  @Override
  public List<Home> getAll() {
    return repositoryPort.findAll();
  }

  @Override
  public void execute(UUID id) {
    // Standard check-then-act pattern
    if (repositoryPort.findById(id).isEmpty()) {
      throw new RuntimeException("Cannot delete: Home not found with ID: " + id);
    }
    repositoryPort.deleteById(id);
  }

  @Override
  public List<Home> search(String query) {
    LOG.info("Searching homes using semantic search: {}", query);

    // 1️⃣ Ask AI adapter for relevant IDs
    List<UUID> matchingIds = homeSearchPort.search(query);

    // 2️⃣ Load full domain objects
    return matchingIds.stream()
        .map(repositoryPort::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }
}
