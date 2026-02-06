package com.loan.origination.system.microservices.app.adapter.out.persistence.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loan.origination.system.contracts.domain.events.DomainEvent;
import com.loan.origination.system.microservices.app.adapter.out.persistence.entity.ApplicationEntity;
import com.loan.origination.system.microservices.app.adapter.out.persistence.entity.OutboxEntity;
import com.loan.origination.system.microservices.app.domain.model.Application;
import com.loan.origination.system.microservices.app.domain.model.ApplicationStatus;
import com.loan.origination.system.microservices.app.domain.model.Borrower;
import org.springframework.stereotype.Component;

@Component
public class ApplicationPersistenceMapper {

  private final ObjectMapper objectMapper;

  public ApplicationPersistenceMapper(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public ApplicationEntity toEntity(Application domain) {
    ApplicationEntity entity = new ApplicationEntity();
    entity.setId(domain.getId());
    entity.setApplicationNumber(domain.getApplicationNumber());
    entity.setHomeId(domain.getHomeId());
    entity.setStatus(domain.getStatus().name());
    entity.setCreatedAt(domain.getCreatedAt());

    // Mapping from Borrower Value Object to flattened columns
    Borrower borrower = domain.getBorrower();
    entity.setFullName(borrower.fullName());
    entity.setEmail(borrower.email());
    entity.setPhone(borrower.phone());
    entity.setDateOfBirth(borrower.dob());
    entity.setSsn(borrower.ssn());
    entity.setLoanAmount(domain.getLoanAmount());
    entity.setLoanPurpose(domain.getLoanPurpose());
    return entity;
  }

  public Application toDomain(ApplicationEntity entity) {
    Borrower borrower =
        new Borrower(
            entity.getFullName(),
            entity.getEmail(),
            entity.getPhone(),
            entity.getDateOfBirth(),
            entity.getSsn());

    return new Application(
        entity.getId(),
        entity.getApplicationNumber(),
        entity.getHomeId(),
        borrower,
        entity.getLoanAmount(),
        entity.getLoanPurpose(),
        ApplicationStatus.valueOf(entity.getStatus()),
        entity.getCreatedAt());
  }

  /** Convert Outbox Domain Model -> JPA Entity */
  public OutboxEntity toOutboxEntity(DomainEvent event) {
    JsonNode payload = objectMapper.valueToTree(event);
    OutboxEntity entity = new OutboxEntity();
    entity.setId(event.eventId());
    entity.setAggregateType(event.aggregateType());
    entity.setAggregateId(event.aggregateId());
    entity.setType(event.eventType().name());
    entity.setPayload(payload);
    entity.setCreatedAt(event.createdAt());
    return entity;
  }
}
