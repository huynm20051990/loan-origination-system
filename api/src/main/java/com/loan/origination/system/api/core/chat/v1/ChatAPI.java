package com.loan.origination.system.api.core.chat.v1;

import com.loan.origination.system.api.core.chat.dto.ChatMessageRequestDTO;
import com.loan.origination.system.api.core.chat.dto.ChatMessageResponseDTO;
import com.loan.origination.system.api.core.chat.dto.ChatSessionResponseDTO;
import com.loan.origination.system.api.core.chat.dto.ChatSessionSummaryDTO;
import com.loan.origination.system.api.core.chat.dto.ChatStreamChunkDTO;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequestMapping("/api/v1/chat")
public interface ChatAPI {

  @PostMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  ChatSessionResponseDTO createSession(Principal principal);

  @GetMapping(value = "/sessions", produces = MediaType.APPLICATION_JSON_VALUE)
  List<ChatSessionSummaryDTO> listSessions(Principal principal);

  @PostMapping(
      value = "/sessions/{sessionId}/messages",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  SseEmitter sendMessage(
      @PathVariable UUID sessionId, @RequestBody ChatMessageRequestDTO request, Principal principal);

  @GetMapping(value = "/sessions/{sessionId}/messages", produces = MediaType.APPLICATION_JSON_VALUE)
  List<ChatMessageResponseDTO> getMessages(@PathVariable UUID sessionId, Principal principal);

  @DeleteMapping("/sessions/{sessionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void deleteSession(@PathVariable UUID sessionId, Principal principal);
}
