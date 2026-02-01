package com.loan.origination.system.microservices.home.domain.port.in;

import com.loan.origination.system.microservices.home.domain.model.Home;
import java.util.List;

/** Input Port for searching homes using natural language processing. */
public interface SearchHomeUseCase {

  /**
   * Searches for homes that are semantically similar to the provided text query.
   *
   * @param query The natural language search string (e.g., "Quiet house with a large backyard").
   * @return A list of homes sorted by their similarity to the query.
   */
  List<Home> search(String query);
}
