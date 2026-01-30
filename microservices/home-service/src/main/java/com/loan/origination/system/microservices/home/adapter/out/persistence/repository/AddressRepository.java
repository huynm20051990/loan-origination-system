package com.loan.origination.system.microservices.home.adapter.out.persistence.repository;

import com.loan.origination.system.microservices.home.adapter.out.persistence.entity.AddressEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<AddressEntity, UUID> {}
