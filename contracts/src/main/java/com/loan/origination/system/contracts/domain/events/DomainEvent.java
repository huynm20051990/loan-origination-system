package com.loan.origination.system.contracts.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DomainEvent {
  UUID eventId();

  String aggregateType();

  String aggregateId();

  EventType eventType();

  LocalDateTime createdAt();
}
