package com.loan.origination.system.contracts.domain.events;

public enum EventType {
  APPLICATION_SUBMITTED("application_submitted"),
  ASSESSMENT_COMPLETED("assessment_completed"),
  ASSESSMENT_NOTIFICATION("assessment_notification"),
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
