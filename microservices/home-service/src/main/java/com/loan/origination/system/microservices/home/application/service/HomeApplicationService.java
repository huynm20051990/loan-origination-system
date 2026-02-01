package com.loan.origination.system.microservices.home.application.service;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.in.AddHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.DeleteHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.GetHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.in.SearchHomeUseCase;
import com.loan.origination.system.microservices.home.domain.port.out.HomeRepositoryPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.EmbeddingModel;

public class HomeApplicationService
    implements AddHomeUseCase, GetHomeUseCase, DeleteHomeUseCase, SearchHomeUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(HomeApplicationService.class);

  private final HomeRepositoryPort repositoryPort;
  private final EmbeddingModel embeddingModel;

  public HomeApplicationService(HomeRepositoryPort repositoryPort, EmbeddingModel embeddingModel) {
    this.repositoryPort = repositoryPort;
    this.embeddingModel = embeddingModel;
  }

  @Override
  public Home execute(Home home) {
    LOG.info(home.toString());
    return repositoryPort.save(home);
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
    LOG.info("Searching for homes with query: {}", query);

    // Convert the user's natural language question into a vector
    float[] queryVector = embeddingModel.embed(query);

    // Pass the vector to the repository to find the top 5 closest matches
    return repositoryPort.findSimilar(queryVector, 5);
  }
}
