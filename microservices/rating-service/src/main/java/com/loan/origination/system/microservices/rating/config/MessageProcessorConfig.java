package com.loan.origination.system.microservices.rating.config;

import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.event.Event;
import com.loan.origination.system.api.exceptions.EventProcessingException;
import com.loan.origination.system.microservices.rating.service.RatingService;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageProcessorConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

  private final RatingService ratingService;

  @Autowired
  public MessageProcessorConfig(RatingService ratingService) {
    this.ratingService = ratingService;
  }

  @Bean
  public Consumer<Event<Integer, Rating>> messageProcessor() {
    return event -> {
      LOG.info("Process message created at {}...", event.getEventCreatedAt());

      switch (event.getEventType()) {
        case CREATE:
          Rating rating = event.getData();
          LOG.info("Create rating with ID: {}/{}", rating.getProductId(), rating.getRatingId());
          ratingService.createRating(rating).block();
          break;

        case DELETE:
          int productId = event.getKey();
          LOG.info("Delete ratings with ProductID: {}", productId);
          ratingService.deleteRatings(productId).block();
          break;

        default:
          String errorMessage =
              "Incorrect event type: "
                  + event.getEventType()
                  + ", expected a CREATE or DELETE event";
          LOG.warn(errorMessage);
          throw new EventProcessingException(errorMessage);
      }

      LOG.info("Message processing done!");
    };
  }
}
