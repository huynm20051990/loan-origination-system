# Handling Partial Failure
In distributed system, whenever a service makes a synchronous request to another service, there is a risk of partial failure like network issues, service is down for maintenance or downstream service failure.
The circuit breaker, time limiter and retry mechanisms are potentially useful in any synchronous communication between two services.
We will apply these mechanisms in calls from the application service to home service.

![Handling Partial Failure](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/10-handling-partial-failure.png)

