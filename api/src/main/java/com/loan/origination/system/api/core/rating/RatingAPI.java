package com.loan.origination.system.api.core.rating;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface RatingAPI {

  /**
   * Sample usage: "curl $HOST:$PORT/rating?productId=1".
   *
   * @param productId Id of the product
   * @return the rating of the product
   */
  @GetMapping(
          value = "/rating",
          produces = "application/json")
  List<Rating> getRatings(
          @RequestParam(value= "productId", required = true)
          int productId);
}
