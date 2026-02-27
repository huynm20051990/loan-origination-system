package com.loan.origination.system.microservices.home.application.service;

import com.loan.origination.system.microservices.home.application.port.input.HomeUseCase;
import com.loan.origination.system.microservices.home.application.port.output.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

public class HomeApplicationService implements HomeUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(HomeApplicationService.class);

  private final HomeRepositoryPort homeRepositoryPort;

  public HomeApplicationService(HomeRepositoryPort repositoryPort) {
    this.homeRepositoryPort = repositoryPort;
  }

  @Override
  public Home addHome(Home home) {
    LOG.info("Saving home and generating embeddings: {}", home);

    // 1. Save to the standard relational database
    Home savedHome = homeRepositoryPort.save(home);

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

    homeRepositoryPort.indexHome(savedHome);

    return savedHome;
  }

  @Override
  public Optional<Home> getById(UUID id) {
    return homeRepositoryPort.findById(id);
  }

  @Override
  public List<Home> getAll() {
    return homeRepositoryPort.findAll();
  }

  @Override
  public void deleteHome(UUID id) {
    // Standard check-then-act pattern
    if (homeRepositoryPort.findById(id).isEmpty()) {
      throw new RuntimeException("Cannot delete: Home not found with ID: " + id);
    }
    homeRepositoryPort.deleteById(id);
  }

  @Override
  public List<Home> search(String query) {
    LOG.info("Searching homes using semantic search: {}", query);

    // 1️⃣ Ask AI adapter for relevant IDs
    List<UUID> matchingIds = homeRepositoryPort.search(query);
    LOG.info("AI Search Result: " + matchingIds.toString());

    // 2️⃣ Load full domain objects
    return matchingIds.stream()
        .map(homeRepositoryPort::findById)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .toList();
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    System.out.println("🚀 Database is ready. Starting home vector sync...");
    try {
      syncAllHomes();
      System.out.println("✅ Sync complete!");
    } catch (Exception e) {
      System.err.println("❌ Sync failed: " + e.getMessage());
    }
  }

  @Override
  public void syncAllHomes() {
    // 1. EXTRACTION: Fetch all existing homes from your relational database
    List<Home> allHomes = homeRepositoryPort.findAll();

    if (allHomes.isEmpty()) {
      return;
    }

    allHomes.forEach(homeRepositoryPort::indexHome);
  }
}
