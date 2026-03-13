package com.loan.origination.system.microservices.assessment.application.service;

import com.loan.origination.system.contracts.domain.common.DecisionResult;
import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.AssessmentCompletedEvent;
import com.loan.origination.system.microservices.assessment.application.port.input.ProcessAssessmentUseCase;
import com.loan.origination.system.microservices.assessment.application.port.output.AssessmentRepositoryPort;
import com.loan.origination.system.microservices.assessment.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.infrastructure.input.messaging.ApplicationSubmittedConsumer;
import com.loan.origination.system.util.encryption.EncryptionUtils;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService implements ProcessAssessmentUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationSubmittedConsumer.class);

  private final AssessmentRepositoryPort assessmentRepository;
  private final OutboxRepositoryPort outboxRepository;
  private final ChatClient chatClient;
  private final ToolCallbackProvider mcpToolProvider;

  public AssessmentService(
      AssessmentRepositoryPort assessmentRepository,
      OutboxRepositoryPort outboxRepository,
      ChatClient.Builder builder,
      ToolCallbackProvider mcpToolProvider) {
    this.assessmentRepository = assessmentRepository;
    this.outboxRepository = outboxRepository;
    this.mcpToolProvider = mcpToolProvider;
    this.chatClient = builder.build();
  }

  @Override
  public void process(ApplicationSubmittedEvent event) {

    LOG.info("Starting AI-driven assessment for Application: {}", event.applicationNumber());

    var callbacks = mcpToolProvider.getToolCallbacks();
    LOG.info("Processing with {} available MCP tools.", callbacks.length);
    for (var callback : callbacks) {
      LOG.info("Available MCP Tool: {}", callback.getToolDefinition().name());
    }

    String secureSsn = EncryptionUtils.encryptQuietly(event.ssn());
    var decisionResult =
        chatClient
            .prompt()
            .toolCallbacks(callbacks)
            .system(
                """
                    You are an automated loan underwriter.
                    Follow this workflow:
                    1. Verify identity and financials using the secureSsn token.
                    2. Evaluate property value at the address.
                    3. Run 'runRuleEngine' with all gathered data for the final result.

                    Only return a DecisionResult object.
                    """)
            .user(
                u ->
                    u.text(
                            """
                    Process application {appNum} for ${amount}.
                    Secure Token: {secureSsn}
                    Address: {address}
                    """)
                        .param("appNum", event.applicationNumber())
                        .param("amount", event.loanAmount())
                        .param("secureSsn", secureSsn)
                        .param(
                            "address",
                            "4532 Maple Drive, Austin, TX 78701")) // Replace with event.address()
            // when ready
            .call()
            .entity(DecisionResult.class);

    LOG.info("AI Assessment concluded: {} - {}", decisionResult.result(), decisionResult.comment());
    Assessment assessment = new Assessment(UUID.fromString(event.aggregateId()));
    assessment.recordDecision(decisionResult.result(), decisionResult.comment());
    assessmentRepository.save(assessment);

    AssessmentCompletedEvent assessmentCompletedEvent =
        AssessmentCompletedEvent.of(
            event.aggregateId(),
            event.applicationNumber(),
            assessment.getDecision(),
            assessment.getRemarks());
    outboxRepository.save(assessmentCompletedEvent);
  }
}
