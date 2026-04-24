# Observability

| Pattern             | Framework or Tool                         | Purpose                               |
|---------------------|-------------------------------------------|---------------------------------------|
| Health Check        | Spring Actuator                           | Check health & readiness              |
| Centralized Logging | EFK / Loki                                | Search, aggregate, and alert on logs  |
| Distributed Tracing | OpenTelemetry and Tempo / Zipkin / Jaeger | Trace requests across microservices   |
| Metrics             | Prometheus and Grafana                    | Monitor CPU, memory, disk utilization |
| Exception Tracking  | Honeybadger.io/Sentry.io                  | Identify and alert on stack traces    |
| Audit Logging       | Logging Code/AOP/Event Sourcing           | Record "Who did what and when"        |