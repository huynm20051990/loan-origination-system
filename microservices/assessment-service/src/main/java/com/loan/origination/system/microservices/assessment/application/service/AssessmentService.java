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
import org.springaicommunity.agent.tools.SkillsTool;
import org.springaicommunity.agent.tools.task.TaskToolCallbackProvider;
import org.springaicommunity.agent.tools.task.subagent.claude.ClaudeSubagentReferences;
import org.springaicommunity.tool.search.ToolSearcher;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class AssessmentService implements ProcessAssessmentUseCase {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationSubmittedConsumer.class);

  private final AssessmentRepositoryPort assessmentRepository;
  private final OutboxRepositoryPort outboxRepository;
  private final ChatClient chatClient;
  private final ToolCallbackProvider mcpToolProvider;
  private Resource assessmentResource;

  public AssessmentService(
      AssessmentRepositoryPort assessmentRepository,
      OutboxRepositoryPort outboxRepository,
      ChatClient.Builder builder,
      ChatMemory chatMemory,
      ToolCallbackProvider mcpToolProvider,
      ToolSearcher toolSearcher,
      @Value("file:/agentic-ai/prompts/assessment.st") Resource assessmentResource,
      @Value("file:/agentic-ai/skills") Resource skillsResource,
      @Value("file:/agentic-ai/agents") Resource subagentResource) {
    this.assessmentRepository = assessmentRepository;
    this.outboxRepository = outboxRepository;
    this.mcpToolProvider = mcpToolProvider;
    this.assessmentResource = assessmentResource;
    this.chatClient =
        builder
            .defaultToolCallbacks(SkillsTool.builder().addSkillsResource(skillsResource).build())
            .defaultToolCallbacks(
                TaskToolCallbackProvider.builder()
                    .chatClientBuilder("default", builder)
                    .subagentReferences(ClaudeSubagentReferences.fromResource(subagentResource))
                    .build())
            .defaultAdvisors(
                MessageChatMemoryAdvisor.builder(chatMemory).build(),
                // ToolSearchToolCallAdvisor.builder().toolSearcher(toolSearcher).build(),
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
                        ChatMemory.CONVERSATION_ID, event.userId() + "_" + event.conversationId()))
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
