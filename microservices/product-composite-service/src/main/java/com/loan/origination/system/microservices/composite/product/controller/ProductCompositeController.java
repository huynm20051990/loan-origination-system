package com.loan.origination.system.microservices.composite.product.controller;

import com.loan.origination.system.api.composite.product.*;
import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.microservices.composite.product.integration.ProductCompositeIntegration;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ProductCompositeController implements ProductCompositeAPI {

  public static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);

  private final ServiceUtil serviceUtil;
  private ProductCompositeIntegration integration;

  @Autowired
  public ProductCompositeController(
      ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public Mono<ProductAggregate> getProduct(int productId) {
    LOG.info("Will get composite product info for product.id={}", productId);
    return Mono.zip(
            values ->
                createProductAggregate(
                    (Product) values[0],
                    (List<Rating>) values[1],
                    (List<Review>) values[2],
                    serviceUtil.getServiceAddress()),
            integration.getProduct(productId),
            integration.getRatings(productId).collectList(),
            integration.getReviews(productId).collectList())
        .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
        .log(LOG.getName(), Level.FINE);
  }

  @Override
  public Mono<Void> createProduct(ProductAggregate body) {
    try {

      List<Mono> monoList = new ArrayList<>();

      LOG.info("Will create a new composite entity for product.id: {}", body.getProductId());

      Product product =
          new Product(body.getProductId(), body.getName(), body.getDescription(), null);
      monoList.add(integration.createProduct(product));

      if (body.getRatings() != null) {
        body.getRatings()
            .forEach(
                r -> {
                  Rating rating =
                      new Rating(
                          body.getProductId(),
                          r.getRatingId(),
                          r.getAuthor(),
                          r.getRate(),
                          r.getContent(),
                          null);
                  monoList.add(integration.createRating(rating));
                });
      }

      if (body.getReviews() != null) {
        body.getReviews()
            .forEach(
                r -> {
                  Review review =
                      new Review(
                          body.getProductId(),
                          r.getReviewId(),
                          r.getAuthor(),
                          r.getSubject(),
                          r.getContent(),
                          null);
                  monoList.add(integration.createReview(review));
                });
      }

      LOG.debug(
          "createCompositeProduct: composite entities created for productId: {}",
          body.getProductId());

      return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
          .doOnError(ex -> LOG.warn("createCompositeProduct failed: {}", ex.toString()))
          .then();

    } catch (RuntimeException re) {
      LOG.warn("createCompositeProduct failed: {}", re.toString());
      throw re;
    }
  }

  @Override
  public Mono<Void> deleteProduct(int productId) {
    try {
      LOG.info("Will delete a product aggregate for product.id: {}", productId);
      return Mono.zip(
              r -> "",
              integration.deleteProduct(productId),
              integration.deleteRatings(productId),
              integration.deleteReviews(productId))
          .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
          .log(LOG.getName(), Level.FINE)
          .then();
    } catch (RuntimeException re) {
      LOG.warn("deleteCompositeProduct failed: {}", re.toString());
      throw re;
    }
  }

  private ProductAggregate createProductAggregate(
      Product product, List<Rating> ratings, List<Review> reviews, String serviceAddress) {

    // 1. Setup product info
    int productId = product.getProductId();
    String name = product.getName();
    String description = product.getDescription();

    // 2. Copy summary rating info, if available
    List<RatingSummary> ratingSummaries =
        (ratings == null)
            ? null
            : ratings.stream()
                .map(
                    r ->
                        new RatingSummary(
                            r.getRatingId(), r.getAuthor(), r.getRate(), r.getContent()))
                .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummary> reviewSummaries =
        (reviews == null)
            ? null
            : reviews.stream()
                .map(
                    r ->
                        new ReviewSummary(
                            r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = product.getServiceAddress();
    String reviewAddress =
        (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
    String ratingAddress =
        (ratings != null && ratings.size() > 0) ? ratings.get(0).getServiceAddress() : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, ratingAddress);

    return new ProductAggregate(
        productId, name, description, ratingSummaries, reviewSummaries, serviceAddresses);
  }
}
