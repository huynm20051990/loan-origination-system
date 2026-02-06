package com.loan.origination.system.contracts.domain.events;

public enum EventType {
  APPLICATION_SUBMITTED("application_submitted"),
  CREDIT_ACCESSED("credit_assessed"),
  UNDERWRITING_COMPLETED("underwriting_completed"),
  NOTIFICATION_SENT("notification_sent");

  private final String topicSuffix;

  // Constructor
  EventType(String topicSuffix) {
    this.topicSuffix = topicSuffix;
  }

  // Getter to be used when inserting into the Outbox
  public String getTopicSuffix() {
    return this.topicSuffix;
  }
}
