package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence;

import com.loan.origination.system.microservices.assessment.application.port.output.PolicyStoragePort;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class VectorStorePolicyStorageAdapter implements PolicyStoragePort {

  private static final Logger LOG = LoggerFactory.getLogger(VectorStorePolicyStorageAdapter.class);

  private final VectorStore vectorStore;

  @Value("file:/rag/borrower--lending-rules.md")
  private Resource searchResource;

  public VectorStorePolicyStorageAdapter(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void storePolicyDocuments() {
    String filename = searchResource.getFilename();
    if (filename == null) return;

    // Extract the single role: "borrower--rules.md" -> "borrower"
    // If no "--" is found, default to "admin"
    String authorizedRole = filename.contains("--") ? filename.split("--")[0] : "admin";

    TextReader textReader = new TextReader(searchResource);
    TokenTextSplitter splitter = new TokenTextSplitter();
    List<Document> chunks = splitter.apply(textReader.get());

    chunks.forEach(
        doc -> {
          // Store as a simple String now, not a List
          doc.getMetadata().put("authorized_role", authorizedRole);
          doc.getMetadata().put("source", filename);
        });

    vectorStore.accept(chunks);
    LOG.info("Persisted {} chunks for role: {}", chunks.size(), authorizedRole);
  }
}
