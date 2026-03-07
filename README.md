# [EasyApply](https://easy-apply.pro/)

A digital platform designed to streamline the home buying and loan application process.

# Contents
- [Requirement](01-requirement/01-requirement.md)
- Architecture
  - [Architecture Characteristics](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/01-architectural-characteristics.md)
  - Architecture Decisions
    - [ADR-001](https://github.com/huynm20051990/loan-origination-system/tree/main/02-architecture/04-architectural-decision-records)
  - [Logical Components](02-architecture/02-logical-components.png)
  - [Architecture Style](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/03-architectural-style.md)
- System Design
  - [High Level Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/01-high-level-design.md)
  - [API Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/02-api-design.md)
  - [Data Model](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/03-data-model.md)
  - [Hexagonal Architecture](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/04-hexagonal-architecture.md)
  - [Rate Limiting](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/05-rate-limiting.md)
  - [Timeout, Retry, Circuit Breaker](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/06-timeout-retry-circuitbreaker.md)
- Testing
- Deployment

# Tech Stack
* **Architecture:** Microservices, Event-Driven
* **Frontend:** Angular
* **Backend:** Java, Spring Boot
* **Orchestration:** Kubernetes
* **Service Mesh:** Istio
* **Monitor Tools:** Kiali, Jaeger, EFK stack, Prometheus, Grafana
* **Messaging:** Apache Kafka
* **Change Data Capture:** Debezium
* **Database:** PostgreSQL