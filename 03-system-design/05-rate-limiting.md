# Rate Limiting
In our case, we will limit 
* 10 requests/second/user at API Gateway level.
* 3 active loan applications in total for business logic.

## Decision Analysis
* Rate limit by what? - IP, user ID or API Key.
* Response header: Always return rate limit info in headers
  * X-RateLimit-Limit
  * X-RateLimit-Remaining
  * X-RateLimit-Retry-After
* Traffic limit or business rule limit:
  * Traffic limit - 10 requests/second/user
    * Use Bucket4j for single-instance or Redis-backed distributed rate limiting
    * Use Spring Cloud Gateway RateLimiter if you already have an API gateway.
    * Use custom Redis filter when you need full control over the logic.
  * Business rule limit - 3 active loan applications in total
    * Use database constraint.

Note: In practice, most production systems land on Bucket4j with Redis or Spring Cloud Gateway.