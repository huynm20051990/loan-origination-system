package com.loan.origination.system.microservices.home.adapter.out.persistence;

import com.loan.origination.system.microservices.home.domain.model.Home;
import com.loan.origination.system.microservices.home.domain.port.out.HomeSearchPort;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

@Component
public class PgVectorHomeSearchAdapter implements HomeSearchPort {

  private final VectorStore vectorStore;

  public PgVectorHomeSearchAdapter(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void indexHome(Home home) {
    Document document =
        new Document(
            home.getDescription(),
            Map.of(
                "homeId", home.getId().toString(),
                "city", home.getAddress().city()));

    vectorStore.add(List.of(document));
  }

  @Override
  public List<UUID> search(String query) {
    return vectorStore.similaritySearch(query).stream()
        .map(doc -> UUID.fromString(doc.getMetadata().get("homeId").toString()))
        .toList();
  }
}
