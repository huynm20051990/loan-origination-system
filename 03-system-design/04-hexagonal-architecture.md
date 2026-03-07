# Hexagonal Architecture
The service package structure follows hexagonal architecture:

```text
[service-name]
│
├── application/           # Use cases that orchestrate the domain
│   ├── port/
│   │   ├── input/         # Primary ports (input)
│   │   └── output/        # Secondary ports (output)
│   └── service/           # Use case implementations
│
├── domain/                # Entities, rules, and business logic
│   ├── model/             # Domain objects
│   ├── exception/         # Domain exceptions
│   └── vo/                # Value Objects
│
├── infrastructure/        # Concrete implementations (adapters)
│   ├── input/             # Primary adapters (inputs)
│   │   ├── rest/          # REST controllers
│   │   └── config/        # Input adapter configurations
│   │
│   └── output/            # Secondary adapters (outputs)
│       ├── persistence/   # Repositories, JPA entities
│       └── config/        # Output adapter configurations
│
└── HexagonalApplication.java
```