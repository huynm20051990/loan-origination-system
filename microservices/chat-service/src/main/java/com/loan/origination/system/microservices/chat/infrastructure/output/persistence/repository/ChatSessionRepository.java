package com.loan.origination.system.microservices.chat.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.chat.infrastructure.output.persistence.entity.ChatSessionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {

  List<ChatSessionEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
