# Database Connection Pool Exhaustion

## Question
**How do you handle database connection pool exhaustion?**

## Answer
I handle connection pool exhaustion by:
- **Failing fast with timeouts** to avoid thread starvation and cascading failures.
- **Preventing connection leaks** using `try-with-resources` to ensure connections are always released.
- **Releasing connections faster** through query optimization or by scaling reads with read replicas.

# Failing Fast with Timeouts in Spring Boot

Failing fast with timeouts prevents thread starvation, connection pool exhaustion, and cascading failures when the database is slow or unavailable. In Spring Boot, this is achieved by configuring timeouts at multiple layers of the application.

---

## 1. Connection Pool Timeouts (HikariCP)

Spring Boot uses **HikariCP** by default. These settings ensure requests fail quickly when no database connection is available.

### application.yml
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5

      # Fail-fast settings
      connection-timeout: 3000        # Max wait (ms) for a connection
      validation-timeout: 1000        # Connection validation timeout
      initialization-fail-timeout: 1  # Fail startup if DB is unreachable
```

### Query Timeouts: Even after a connection is acquired, long-running queries can block the pool.
```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          timeout: 3   # seconds
```

### Web Layer Timeouts: Fail requests early at the HTTP layer to avoid unnecessary load on the database.
```yaml
server:
  tomcat:
    connection-timeout: 5s
    threads:
      max: 200

```

