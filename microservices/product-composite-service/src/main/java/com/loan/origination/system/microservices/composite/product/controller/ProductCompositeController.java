package com.loan.origination.system.microservices.composite.product.controller;

import com.loan.origination.system.api.composite.product.*;
import com.loan.origination.system.api.core.product.Product;
import com.loan.origination.system.api.core.rating.Rating;
import com.loan.origination.system.api.core.review.Review;
import com.loan.origination.system.api.exceptions.NotFoundException;
import com.loan.origination.system.microservices.composite.product.integration.ProductCompositeIntegration;
import com.loan.origination.system.util.http.ServiceUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductCompositeController implements ProductCompositeAPI {

  private final ServiceUtil serviceUtil;
  private ProductCompositeIntegration integration;

  @Autowired
  public ProductCompositeController(
      ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public ProductAggregate getProduct(int productId) {
    Product product = integration.getProduct(productId);
    if (product == null) {
      throw new NotFoundException("No product found for productId: " + productId);
    }
    List<Rating> ratings = integration.getRatings(productId);
    List<Review> reviews = integration.getReviews(productId);
    return createProductAggregate(product, ratings, reviews, serviceUtil.getServiceAddress());
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
                .map(r -> new RatingSummary(r.getRatingId(), r.getAuthor(), r.getRate()))
                .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummary> reviewSummaries =
        (reviews == null)
            ? null
            : reviews.stream()
                .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
                .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = product.getServiceAddress();
    String reviewAddress =
        (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
    String recommendationAddress =
        (ratings != null && ratings.size() > 0) ? ratings.get(0).getServiceAddress() : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregate(
        productId, name, description, ratingSummaries, reviewSummaries, serviceAddresses);
  }
}
