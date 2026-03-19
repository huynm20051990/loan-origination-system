package com.loan.origination.system.microservices.assessment.application.service;

import com.loan.origination.system.contracts.domain.common.DecisionResult;
import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.AssessmentCompletedEvent;
import com.loan.origination.system.microservices.assessment.application.port.input.ProcessAssessmentUseCase;
import com.loan.origination.system.microservices.assessment.application.port.output.AssessmentRepositoryPort;
import com.loan.origination.system.microservices.assessment.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.assessment.application.port.output.PolicyStoragePort;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.infrastructure.input.messaging.ApplicationSubmittedConsumer;
import com.loan.origination.system.util.encryption.EncryptionUtils;
import jakarta.annotation.PostConstruct;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.tool.search.ToolSearchToolCallAdvisor;
import org.springaicommunity.tool.search.ToolSearcher;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService implements ProcessAssessmentUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationSubmittedConsumer.class);

  private final AssessmentRepositoryPort assessmentRepository;
  private final OutboxRepositoryPort outboxRepository;
  private final PolicyStoragePort policyStoragePort;
  private final ChatClient chatClient;
  private final ToolCallbackProvider mcpToolProvider;

  @Value("file:/prompts/assessment.st")
  private Resource assessmentResource;

  @PostConstruct
  public void verifyPromptExists() {
    if (!assessmentResource.exists()) {
      LOG.warn("CRITICAL: Prompt file not found at file:/prompts/assessment.st. ");
    }
  }

  @EventListener(ApplicationReadyEvent.class)
  public void onApplicationReady() {
    LOG.info("Application started. Loading lending policy into Vector Store...");
    policyStoragePort.storePolicyDocuments();
  }

  public AssessmentService(
      AssessmentRepositoryPort assessmentRepository,
      OutboxRepositoryPort outboxRepository,
      PolicyStoragePort policyStoragePort,
      ChatClient.Builder builder,
      ChatMemory chatMemory,
      VectorStore vectorStore,
      ToolCallbackProvider mcpToolProvider,
      ToolSearcher toolSearcher) {
    this.assessmentRepository = assessmentRepository;
    this.outboxRepository = outboxRepository;
    this.policyStoragePort = policyStoragePort;
    this.mcpToolProvider = mcpToolProvider;
    this.chatClient =
        builder
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                QuestionAnswerAdvisor.builder(vectorStore).build(),
                ToolSearchToolCallAdvisor.builder().toolSearcher(toolSearcher).build(),
                new SimpleLoggerAdvisor())
            .build();
  }

  @Override
  public void process(ApplicationSubmittedEvent event) {

    LOG.info("Starting AI-driven assessment for Application: {}", event.applicationNumber());

    var callbacks = mcpToolProvider.getToolCallbacks();
    LOG.info("Processing with {} available MCP tools.", callbacks.length);
    for (var callback : callbacks) {
      LOG.info("Available MCP Tool: {}", callback.getToolDefinition().name());
    }

    var decisionResult =
        chatClient
            .prompt()
            .toolCallbacks(callbacks)
            .advisors(
                a ->
                    a.param(
                            // Apply per-user conversation memory
                            ChatMemory.CONVERSATION_ID,
                            event.userId() + "_" + event.conversationId())
                        .param(
                            QuestionAnswerAdvisor.FILTER_EXPRESSION,
                            // Use standard equality: metadata_key == 'value'
                            "authorized_role == '" + event.userRole() + "'"))
            .user(
                u ->
                    u.text(assessmentResource)
                        .param("appNum", event.applicationNumber())
                        .param("amount", event.loanAmount())
                        .param("secureSsn", EncryptionUtils.encryptQuietly(event.ssn()))
                        .param("address", "4532 Maple Drive, Austin, TX 78701"))
            .call()
            .entity(DecisionResult.class);

    LOG.info("AI Assessment concluded: {} - {}", decisionResult.result(), decisionResult.comment());
    Assessment assessment = new Assessment(UUID.fromString(event.aggregateId()));
    assessment.recordDecision(decisionResult.result(), decisionResult.comment());
    assessmentRepository.save(assessment);

    AssessmentCompletedEvent assessmentCompletedEvent =
        AssessmentCompletedEvent.of(
            event.applicationId(),
            null,
            null,
            null,
            event.applicationNumber(),
            assessment.getDecision(),
            assessment.getRemarks());
    outboxRepository.save(assessmentCompletedEvent);
  }
}
