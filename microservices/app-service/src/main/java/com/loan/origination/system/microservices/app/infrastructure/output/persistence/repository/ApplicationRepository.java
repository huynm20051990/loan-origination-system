package com.loan.origination.system.microservices.app.infrastructure.output.persistence.repository;

import com.loan.origination.system.microservices.app.infrastructure.output.persistence.entity.ApplicationEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

  Optional<ApplicationEntity> findByApplicationNumber(String applicationNumber);

  List<ApplicationEntity> findByEmail(String email);
}
