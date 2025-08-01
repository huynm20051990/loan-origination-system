package com.loan.origination.system.api.core.rating;

import java.util.List;
import org.springframework.web.bind.annotation.*;

public interface RatingAPI {

  /**
   * Sample usage: "curl $HOST:$PORT/rating?productId=1".
   *
   * @param productId Id of the product
   * @return the rating of the product
   */
  @GetMapping(value = "/rating", produces = "application/json")
  List<Rating> getRatings(@RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage, see below.
   *
   * <p>curl -X POST $HOST:$PORT/rating \ -H "Content-Type: application/json" --data \
   * '{"productId":123,"ratingId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
   *
   * @param rating A JSON representation of the new rating
   * @return A JSON representation of the newly created rating
   */
  @PostMapping(value = "/rating", consumes = "application/json", produces = "application/json")
  Rating createRating(@RequestBody Rating rating);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/rating?productId=1".
   *
   * @param productId Id of the product
   */
  @DeleteMapping(value = "/rating")
  void deleteRatings(@RequestParam(value = "productId", required = true) int productId);
}
