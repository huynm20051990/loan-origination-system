# Home Microservice – File Inventory & Architecture Overview

This document describes the complete inventory of files for the **Home Microservice**, organized by architectural layer.

The service follows **Hexagonal Architecture (Ports and Adapters)** and is implemented using **Spring WebFlux** and **R2DBC** to support a fully reactive, non-blocking model.

---

## I. Domain Layer (The Heart)

**Purpose:**  
Pure business logic. This layer contains **no framework dependencies** and represents the core domain model.

### Files

- **Address.java**  
  A Java `record` representing the home’s location.  
  *Role:* Value Object.

- **HomeStatus.java**  
  An enum defining the lifecycle of a home (e.g. `AVAILABLE`, `SOLD`, etc.).

- **Home.java**  
  The Aggregate Root entity containing:
    - Core business logic
    - State transitions
    - Full getters to protect invariants

---

## II. Application Layer (Inbound Ports & Services)

**Purpose:**  
Orchestrates business use cases and coordinates between the domain and infrastructure layers.

### Files

- **AddHomeUseCase.java**  
  Inbound Port (interface) for creating new home listings.

- **GetHomeUseCase.java**  
  Inbound Port (interface) for retrieving home listings.

- **DeleteHomeUseCase.java**  
  Inbound Port (interface) for removing home listings.

- **HomeApplicationService.java**  
  Implements the inbound ports and:
    - Coordinates domain logic
    - Calls outbound ports
    - Contains no web or persistence details

---

## III. Infrastructure Layer – Outbound (Persistence)

**Purpose:**  
Handles database access using **R2DBC** and implements outbound ports.

### Files

- **HomeRepositoryPort.java**  
  Outbound Port (interface) used by the application layer to persist data.

- **HomeEntity.java**  
  R2DBC-annotated persistence entity for the `homes` table.

- **AddressEntity.java**  
  R2DBC-annotated persistence entity for the `addresses` table.

- **SpringDataHomeRepository.java**  
  Reactive CRUD repository for homes.

- **SpringDataAddressRepository.java**  
  Reactive CRUD repository for addresses.

- **HomePersistenceMapper.java**  
  Maps between:
    - Domain (`Home`, `Address`)
    - Persistence (`HomeEntity`, `AddressEntity`)

- **HomePersistenceAdapter.java**  
  Implements `HomeRepositoryPort` and orchestrates:
    - Non-blocking database access
    - Aggregate persistence using reactive composition

---

## IV. Infrastructure Layer – Inbound (Web / API)

**Purpose:**  
Exposes the REST API and maps HTTP requests to application use cases.

### Files

- **HomeApi.java**  
  Reactive API contract (interface) using `Mono` and `Flux`.

- **HomeRequestDTO.java**  
  Java `record` representing incoming JSON payloads.

- **AddressDTO.java**  
  Java `record` for nested address data in requests/responses.

- **HomeResponseDTO.java**  
  Java `record` representing outgoing JSON payloads.

- **HomeWebMapper.java**  
  Maps between:
    - DTO records
    - Domain entities

- **HomeOrderController.java**  
  REST controller implementing `HomeApi` and delegating to the application layer.

---

## V. Configuration

**Purpose:**  
Explicit wiring of dependencies to preserve clean architecture boundaries.

### Files

- **BeanConfiguration.java**  
  Manually defines Spring beans to:
    - Avoid framework annotations in Domain and Application layers
    - Keep dependencies explicit and testable

---

## Architectural Summary

- **Architecture:** Hexagonal (Ports & Adapters)
- **Programming Model:** Reactive (Spring WebFlux + R2DBC)
- **Domain:** Pure Java, framework-agnostic
- **Infrastructure:** Fully replaceable adapters
- **Design Goal:** Clean separation of concerns, testability, and scalability
