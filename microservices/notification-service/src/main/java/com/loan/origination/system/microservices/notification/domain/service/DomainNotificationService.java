package com.loan.origination.system.microservices.notification.domain.service;

import com.loan.origination.system.microservices.notification.domain.model.Notification;
import com.loan.origination.system.microservices.notification.domain.model.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class DomainNotificationService {

  public Notification prepareCreditCheckNotification(String appNumber, String email) {

    String subject = "Update on Loan Application: " + appNumber;
    String content =
        String.format(
            "Hello, your credit check is complete. Your application has been categorized as: %s.",
            subject);

    return Notification.create(appNumber, email, NotificationType.EMAIL, subject, content);
  }
}
