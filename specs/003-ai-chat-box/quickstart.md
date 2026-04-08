# Quickstart: AI Chat Box

**Branch**: `003-ai-chat-box` | **Date**: 2026-04-08

---

## Prerequisites

- Docker + Docker Compose
- Java 21 (`JAVA_HOME` set)
- Node 20+ / npm (for Angular dev server)
- `.env` file at repo root with `GEMINI_API_KEY`, `POSTGRES_USR`, `POSTGRES_PWD`

---

## 1. Register the new module in `settings.gradle.kts`

```kotlin
include("microservices:chat-service")   // add this line
```

---

## 2. Scaffold `chat-service` — copy the assessment-service build file as a baseline

```bash
cp -r microservices/assessment-service microservices/chat-service
# Then trim to only what chat-service needs (see build.gradle.kts below)
```

### `microservices/chat-service/build.gradle.kts` (target state)

```kotlin
plugins {
    java
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.loan.origination.system.microservices"
version = "0.0.1-SNAPSHOT"

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

val springAiVersion = "1.1.2"
var springCloudVersion = "2025.0.0"

dependencies {
    implementation(project(":api"))
    implementation(project(":util"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    // SSE support alongside servlet stack
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Spring AI
    implementation("org.springframework.ai:spring-ai-starter-model-google-genai")
    implementation("org.springframework.ai:spring-ai-starter-model-chat-memory-repository-cassandra")
    // Observability
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:cassandra")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.wiremock:wiremock-standalone:3.5.4")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<Test> { useJUnitPlatform() }
```

---

## 3. Create `config-repo/chat.yml`

```yaml
server.port: 7007
server.error.include-message: always

spring.application.name: chat
spring.threads.virtual.enabled: true

spring:
  ai:
    model:
      chat: google-genai
    google:
      genai:
        chat:
          options:
            model: gemini-2.5-flash
    chat:
      memory:
        cassandra:
          keyspace: chat_keyspace
          messages-column: chat_messages
          table: chat_memory
  cassandra:
    contact-points: easy-apply-chat-cassandra
    port: 9042
    local-datacenter: dc1
    schema-action: create_if_not_exists
    request:
      timeout: 10s

app:
  home-service:
    url: http://home-service
```

---

## 4. Create Cassandra keyspace init script

```bash
mkdir -p database/init-chat-cassandra
cat > database/init-chat-cassandra/init.cql <<'EOF'
CREATE KEYSPACE IF NOT EXISTS chat_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};
EOF
```

---

## 5. Add `chat-service` and `chat-cassandra` to `docker-compose.yml`

```yaml
  chat-cassandra:
    image: 'cassandra:latest'
    mem_limit: 1024m
    container_name: easy-apply-chat-cassandra
    environment:
      - CASSANDRA_DC=dc1
      - CASSANDRA_ENDPOINT_SNITCH=GossipingPropertyFileSnitch
      - MAX_HEAP_SIZE=512M
      - HEAP_NEWSIZE=128M
    volumes:
      - $PWD/database/init-chat-cassandra:/docker-entrypoint-initdb.d
    healthcheck:
      test: ["CMD-SHELL", "cqlsh -e 'describe keyspaces'"]
      interval: 30s
      timeout: 10s
      retries: 10

  chat-service:
    build: microservices/chat-service
    image: loan-origination-system/chat-service
    mem_limit: 512m
    container_name: easy-apply-chat-service
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_CONFIG_LOCATION=file:/config-repo/application.yml,file:/config-repo/chat.yml
      - SPRING_AI_GOOGLE_GENAI_API_KEY=${GEMINI_API_KEY}
    volumes:
      - $PWD/config-repo:/config-repo
    depends_on:
      chat-cassandra:
        condition: service_healthy
      auth-server:
        condition: service_healthy
```

---

## 6. Add gateway route in `config-repo/gateway.yml`

Insert **before** the `app-ui` catch-all route:

```yaml
- id: chat-service
  uri: http://chat-service
  predicates:
    - Path=/api/v1/chat/**
```

---

## 7. Add `ChatAPI` contract to the `:api` module

Copy `specs/003-ai-chat-box/contracts/ChatAPI.java` →
`api/src/main/java/com/loan/origination/system/api/core/chat/v1/ChatAPI.java`

Copy `specs/003-ai-chat-box/contracts/ChatRequestDTO.java` →
`api/src/main/java/com/loan/origination/system/api/core/chat/dto/ChatRequestDTO.java`

---

## 8. Build and verify locally

```bash
# Build all modules
./gradlew build

# Start infrastructure only (fast iteration)
docker compose up -d chat-cassandra

# Run chat-service locally
./gradlew :microservices:chat-service:bootRun --args='--spring.config.location=config-repo/application.yml,config-repo/chat.yml'

# Smoke-test SSE endpoint
curl -N -X POST http://localhost:7007/api/v1/chat/stream \
  -H "Content-Type: application/json" \
  -d '{"sessionId":"test-session-1","query":"3 beds under 500k in Austin"}'
```

Expected output: a stream of `event: listings`, multiple `event: token`, then `event: done`.

---

## 9. Angular development

```bash
cd app-ui
npm install
ng serve --proxy-config proxy.conf.json   # proxy /api → https://localhost:8443
```

The `ChatBoxComponent` mounts inside `HomeListingsComponent`. Import it as a standalone
component and add it to the `imports` array.

---

## Test containers reference (integration tests)

```java
@Testcontainers
class ChatApplicationServiceIT {
    @Container
    static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:latest")
        .withInitScript("init-chat-cassandra.cql");
    // WireMock for home-service stub
}
```
