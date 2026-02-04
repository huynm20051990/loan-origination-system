package com.loan.origination.system.microservices.app.adapter.out.persistence.repository;

import com.loan.origination.system.microservices.app.adapter.out.persistence.entity.ApplicationEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

  // Custom query to find by the human-readable ID
  Optional<ApplicationEntity> findByApplicationNumber(String applicationNumber);
}
