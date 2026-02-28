package com.loan.origination.system.microservices.credit.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.credit.infrastructure.output.persistence.entity.CreditReportEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditReportRepository extends JpaRepository<CreditReportEntity, UUID> {}
