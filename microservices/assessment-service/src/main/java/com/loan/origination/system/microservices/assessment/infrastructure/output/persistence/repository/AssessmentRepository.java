package com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.assessment.infrastructure.output.persistence.entity.AssessmentEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, UUID> {}
