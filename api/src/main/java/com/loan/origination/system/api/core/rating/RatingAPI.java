package com.loan.origination.system.api.core.rating;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RatingAPI {

  /**
   * Sample usage: "curl $HOST:$PORT/rating?productId=1".
   *
   * @param headers
   * @param productId Id of the product
   * @return the rating of the product
   */
  @GetMapping(value = "/rating", produces = "application/json")
  Flux<Rating> getRatings(
      @RequestHeader HttpHeaders headers,
      @RequestParam(value = "productId", required = true) int productId);

  /**
   * Sample usage, see below.
   *
   * <p>curl -X POST $HOST:$PORT/rating \ -H "Content-Type: application/json" --data \
   * '{"productId":123,"ratingId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
   *
   * @param body A JSON representation of the new rating
   * @return A JSON representation of the newly created rating
   */
  @PostMapping(value = "/rating", consumes = "application/json", produces = "application/json")
  Mono<Rating> createRating(@RequestBody Rating body);

  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/rating?productId=1".
   *
   * @param productId Id of the product
   * @return
   */
  @DeleteMapping(value = "/rating")
  Mono<Void> deleteRatings(@RequestParam(value = "productId", required = true) int productId);
}
