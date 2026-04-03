package com.loan.origination.system.microservices.chat.infrastructure.input.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.api.core.chat.dto.ChatMessageRequestDTO;
import com.loan.origination.system.api.core.chat.dto.ChatMessageResponseDTO;
import com.loan.origination.system.api.core.chat.dto.ChatSessionResponseDTO;
import com.loan.origination.system.api.core.chat.dto.ChatSessionSummaryDTO;
import com.loan.origination.system.api.core.chat.dto.ChatStreamChunkDTO;
import com.loan.origination.system.api.core.chat.v1.ChatAPI;
import com.loan.origination.system.microservices.chat.application.port.input.ChatUseCase;
import com.loan.origination.system.microservices.chat.infrastructure.input.rest.mapper.ChatWebMapper;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class ChatWebAdapterController implements ChatAPI {

  private static final Logger LOG = LoggerFactory.getLogger(ChatWebAdapterController.class);

  private final ChatUseCase chatUseCase;
  private final ChatWebMapper mapper;
  private final ObjectMapper objectMapper;
  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  public ChatWebAdapterController(ChatUseCase chatUseCase, ChatWebMapper mapper, ObjectMapper objectMapper) {
    this.chatUseCase = chatUseCase;
    this.mapper = mapper;
    this.objectMapper = objectMapper;
  }

  @Override
  public ChatSessionResponseDTO createSession(Principal principal) {
    var session = chatUseCase.createSession(principal.getName());
    return mapper.toResponse(session);
  }

  @Override
  public List<ChatSessionSummaryDTO> listSessions(Principal principal) {
    return chatUseCase.listSessions(principal.getName());
  }

  @Override
  public SseEmitter sendMessage(UUID sessionId, ChatMessageRequestDTO request, Principal principal) {
    SseEmitter emitter = new SseEmitter(60_000L);

    executor.submit(() -> {
      try {
        UUID[] assistantMessageId = {null};

        chatUseCase.sendMessage(
            sessionId,
            principal.getName(),
            request.content(),
            token -> {
              try {
                String json = objectMapper.writeValueAsString(ChatStreamChunkDTO.token(token));
                emitter.send(SseEmitter.event().data(json, MediaType.APPLICATION_JSON));
              } catch (Exception e) {
                LOG.error("Error sending SSE token for session {}: {}", sessionId, e.getMessage());
                emitter.completeWithError(e);
              }
            },
            () -> {
              try {
                UUID msgId = UUID.randomUUID();
                String json = objectMapper.writeValueAsString(ChatStreamChunkDTO.done(msgId));
                emitter.send(SseEmitter.event().data(json, MediaType.APPLICATION_JSON));
                emitter.complete();
              } catch (Exception e) {
                LOG.error("Error completing SSE for session {}: {}", sessionId, e.getMessage());
                emitter.completeWithError(e);
              }
            });
      } catch (SecurityException e) {
        LOG.warn("Ownership mismatch for session {}: {}", sessionId, e.getMessage());
        emitter.completeWithError(e);
      } catch (Exception e) {
        LOG.error("Unexpected error for session {}: {}", sessionId, e.getMessage());
        emitter.completeWithError(e);
      }
    });

    return emitter;
  }

  @Override
  public List<ChatMessageResponseDTO> getMessages(UUID sessionId, Principal principal) {
    return chatUseCase.getMessages(sessionId, principal.getName()).stream()
        .map(mapper::toResponse)
        .toList();
  }

  @Override
  public void deleteSession(UUID sessionId, Principal principal) {
    chatUseCase.deleteSession(sessionId, principal.getName());
  }
}
