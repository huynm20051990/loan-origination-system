package com.loan.origination.system.microservices.notification.adapter.out.persistence.repository;

import com.loan.origination.system.microservices.notification.adapter.out.persistence.entity.NotificationEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {}
