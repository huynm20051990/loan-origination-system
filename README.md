# [EasyApply]()

https://easy-apply.pro/ - A digital platform designed to streamline the home buying and loan application process.

# Contents
- [Requirement](01-requirement/01-requirement.md)
- Architecture
  - [Architecture Characteristics](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/01-architectural-characteristics.md)
  - Architecture Decisions
    - [ADR-001](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/04-architectural-decision-records/ADR-001-microservices-architecture.md)
  - [Logical Components](02-architecture/02-logical-components.png)
  - [Architecture Style](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/03-architectural-style.md)
- System Design
  - [High Level Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/01-high-level-design.md)
  - [Service Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/04-hexagonal-architecture.md)
  - [API Gateway Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/16-api-gateway.md)
  - [API Design](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/02-api-design.md)
  - [Data Model](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/03-data-model.md)
  - [Handling Semantic Search](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/19-handling-semantic-search.md)
    - [Converting Data To Embeddings](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/24-converting-data-to-embeddings.md)
    - [Submitting Prompt For Generation](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/20-submitting-prompt-for-generation.md)
    - [Safeguarding against adversarial prompting](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/23-safeguarding-prompting.md)
  - [Concurrency Issue](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/17-concurrency-issue.md)
  - [Handling Partial Failure](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/18-handling-partial-failure.md)
  - [Handling Challenges With Messaging](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/21-handling-challenges-with-messaging.md)
  - [Handling Automated Workflow]()
  - [Security](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/22-security.md)
  - [Observing and Monitoring](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/25-observability.md)
  - [Scaling](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/26-scaling.md)
  - [How To Use The Application](https://github.com/huynm20051990/loan-origination-system/blob/main/03-system-design/how-to-use-the-application.md)

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