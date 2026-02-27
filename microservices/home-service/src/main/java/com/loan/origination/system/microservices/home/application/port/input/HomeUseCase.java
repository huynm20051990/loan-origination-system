package com.loan.origination.system.microservices.home.application.port.input;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HomeUseCase {
  /**
   * Add home
   *
   * @param home
   * @return
   */
  Home addHome(Home home);

  /**
   * Delete home
   *
   * @param id
   */
  void deleteHome(UUID id);

  /**
   * Get home by id
   *
   * @param id
   * @return
   */
  Optional<Home> getById(UUID id);

  /**
   * Get all homes
   *
   * @return
   */
  List<Home> getAll();

  /**
   * Searches for homes that are semantically similar to the provided text query.
   *
   * @param query The natural language search string (e.g., "Quiet house with a large backyard").
   * @return A list of homes sorted by their similarity to the query.
   */
  List<Home> search(String query);

  /** Sync all homes */
  void syncAllHomes();
}
