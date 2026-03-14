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

  @Value("file:/rag/lending-policy.md")
  private Resource searchResource;

  public VectorStorePolicyStorageAdapter(VectorStore vectorStore) {
    this.vectorStore = vectorStore;
  }

  @Override
  public void storePolicyDocuments() {
    LOG.info("Starting policy ingestion from resource: {}", searchResource.getFilename());
    TextReader textReader = new TextReader(searchResource);
    TokenTextSplitter splitter = new TokenTextSplitter();

    List<Document> chunks = splitter.apply(textReader.get());
    vectorStore.accept(chunks);
    LOG.info("Successfully persisted {} chunks to the Vector Store.", chunks.size());
  }
}
