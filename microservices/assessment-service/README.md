# Folder Structure

src/main/java/com/loan/origination/system/microservices/assessment
│
├── application/
│   ├── port/
│   │   ├── input/
│   │   │   └── ProcessAssessmentUseCase.java         # Interface for the use case
│   │   └── output/
│   │       ├── AssessmentRepositoryPort.java        # Interface for saving assessment result
│   │       ├── OutboxRepositoryPort.java            # Interface for saving to outbox
│   │       └── ExternalServiceClientPort.java       # Interface for external APIs (Identity, Credit, etc.)
│   └── service/
│       └── AssessmentService.java                   # Orchestrates the 8 steps
│
├── domain/
│   ├── model/
│   │   ├── Assessment.java                          # Core entity
│   │   └── AssessmentStatus.java                    # Enum/Value Object
│   ├── exception/
│   │   └── AssessmentDomainException.java
│   └── vo/                                          # Value Objects (e.g., CreditScore)
│
├── infrastructure/
│   ├── input/
│   │   └── messaging/
│   │       ├── ApplicationSubmittedConsumer.java    # Spring Cloud Stream Consumer
│   │       └── ApplicationSubmittedEvent.java       # DTO for incoming event
│   └── output/
│       ├── persistence/
│       │   ├── jpa/
│       │   │   ├── AssessmentEntity.java            # JPA entity
│       │   │   ├── OutboxEntity.java                # JPA entity for outbox
│       │   │   └── AssessmentJpaRepository.java     # Spring Data interface
│       │   ├── AssessmentPersistenceAdapter.java    # Implements AssessmentRepositoryPort
│       │   └── OutboxPersistenceAdapter.java        # Implements OutboxRepositoryPort
│       ├── client/
│       │   └── ExternalApiAdapter.java              # Implements ExternalServiceClientPort
│       └── config/
│           ├── MessagingConfig.java                 # Cloud Stream configuration
│           └── PersistenceConfig.java               # DB transaction settings
│
└── AssessmentApplication.java

