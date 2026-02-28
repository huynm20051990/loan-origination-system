package com.loan.origination.system.microservices.credit.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.credit.infrastructure.output.persistence.entity.OutboxEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {
  // Standard CRUD is sufficient here
}
