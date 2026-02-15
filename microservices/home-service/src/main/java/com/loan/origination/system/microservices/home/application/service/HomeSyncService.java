package com.loan.origination.system.microservices.home.application.service;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.in.SyncHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import com.loan.origination.system.microservices.home.domain.port.out.HomeSearchPort;
import java.util.List;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class HomeSyncService implements SyncHomeUseCase {
  private final HomeRepositoryPort repositoryPort;
  private final HomeSearchPort homeSearchPort;

  public HomeSyncService(HomeRepositoryPort repositoryPort, HomeSearchPort homeSearchPort) {
    this.repositoryPort = repositoryPort;
    this.homeSearchPort = homeSearchPort;
  }

  // This annotation ensures the code runs ONLY after the DB is ready
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
    List<Home> allHomes = repositoryPort.findAll();

    if (allHomes.isEmpty()) {
      return;
    }

    allHomes.forEach(
        home -> {
          String searchContent =
              String.format(
                  "Home in %s, %s. Price: %s. %d beds and %.1f baths. %s",
                  home.getAddress().city(),
                  home.getAddress().state(),
                  home.getPrice(),
                  home.getBeds(),
                  home.getBaths(),
                  home.getDescription());

          // Delegate indexing to adapter
          homeSearchPort.indexHome(home);
        });
  }
}
