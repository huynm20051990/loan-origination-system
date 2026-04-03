package com.loan.origination.system.microservices.chat.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity.ChatMessageEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, UUID> {

  List<ChatMessageEntity> findBySessionIdOrderByTimestampAsc(UUID sessionId);

  int countBySessionId(UUID sessionId);
}
