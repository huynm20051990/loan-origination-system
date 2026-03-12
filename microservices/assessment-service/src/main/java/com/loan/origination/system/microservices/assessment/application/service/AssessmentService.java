package com.loan.origination.system.microservices.assessment.application.service;

import com.loan.origination.system.contracts.domain.events.ApplicationSubmittedEvent;
import com.loan.origination.system.contracts.domain.events.AssessmentCompletedEvent;
import com.loan.origination.system.microservices.assessment.application.port.input.ProcessAssessmentUseCase;
import com.loan.origination.system.microservices.assessment.application.port.output.AssessmentRepositoryPort;
import com.loan.origination.system.microservices.assessment.application.port.output.OutboxRepositoryPort;
import com.loan.origination.system.microservices.assessment.domain.model.Assessment;
import com.loan.origination.system.microservices.assessment.infrastructure.input.messaging.ApplicationSubmittedConsumer;
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

  public AssessmentService(
      AssessmentRepositoryPort assessmentRepository,
      OutboxRepositoryPort outboxRepository,
      ChatClient.Builder builder,
      ToolCallbackProvider mcpToolProvider) {
    this.assessmentRepository = assessmentRepository;
    this.outboxRepository = outboxRepository;
    this.chatClient = builder.defaultToolCallbacks(mcpToolProvider.getToolCallbacks()).build();
  }

  @Override
  public void process(ApplicationSubmittedEvent event) {

    LOG.info("Starting AI-driven assessment for application: {}", event.applicationNumber());

    // 1. Let Gemini orchestrate the tools to make a decision
    var aiResponse =
        chatClient
            .prompt()
            .system(
                "You are an automated loan underwriter. Use the provided tools to verify identity, "
                    + "check financials, and evaluate the property. Only approve if all steps pass.")
            .user(
                u ->
                    u.text("Process loan application {appNum} for property at {address}")
                        .param("appNum", event.applicationNumber())
                        .param("address", "123  "))
            .call();
    LOG.info("AI-driven assessment result:" + aiResponse);
    Assessment assessment = new Assessment(UUID.fromString(event.aggregateId()));
    assessment.recordDecision("APPROVED", "Hardcoded mock approval");
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
