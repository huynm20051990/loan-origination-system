# API Exception Handling Guide

## Overview

This document explains recommended practices for handling exceptions in
REST APIs using Spring Boot 3+, including the use of **ProblemDetail**,
global exception handlers, and consistent error response formatting.

------------------------------------------------------------------------

## 1. Use ProblemDetail (Spring Boot 3+ Standard)

Spring Boot introduces `ProblemDetail` based on RFC 7807 for
standardized error responses.

### Example Global Exception Handler

``` java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFound(NotFoundException ex, ServerWebExchange exchange) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return problem;
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInput(InvalidInputException ex, ServerWebExchange exchange) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage()
        );
        problem.setTitle("Invalid Input");
        problem.setInstance(URI.create(exchange.getRequest().getPath().value()));
        return problem;
    }
}
```

------------------------------------------------------------------------

## 2. Example JSON Responses

### Resource Not Found (404)

``` json
{
  "type": "about:blank",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "No product found for productId: 13",
  "instance": "/product/13"
}
```

### Invalid Input (422)

``` json
{
  "type": "about:blank",
  "title": "Invalid Input",
  "status": 422,
  "detail": "Invalid productId: -1",
  "instance": "/product/-1"
}
```

------------------------------------------------------------------------

## 3. Adding Custom Fields

You can add metadata to the error details:

``` java
problem.setProperty("timestamp", ZonedDateTime.now());
problem.setProperty("service", "product-service");
```

------------------------------------------------------------------------

## 4. Why Use ProblemDetail?

-   Complies with **RFC 7807**
-   Native in Spring Boot 3+
-   Clean JSON formatting
-   Extensible with custom fields
-   Better for API gateways, clients, frontend apps
-   Standard across microservices

------------------------------------------------------------------------

## 5. Testing Error Responses

Use `WebTestClient`:

``` java
client.get()
      .uri("/product/999")
      .exchange()
      .expectStatus().isNotFound()
      .expectBody()
      .jsonPath("$.title").isEqualTo("Resource Not Found");
```

------------------------------------------------------------------------

## 6. Summary

  Technique                    Recommended
  ---------------------------- ----------------------
  `ProblemDetail`              ✅ Yes
  Custom error DTO             ⚠️ Only if necessary
  Returning raw Strings        ❌ Avoid
  Throwing generic Exception   ❌ Avoid

------------------------------------------------------------------------

## Final Recommendation

Use **ProblemDetail** with a global `@RestControllerAdvice` to ensure
all exceptions return consistent, standard, machine-readable error
responses.
