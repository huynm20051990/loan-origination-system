# Back-of-the-envelop Estimation


---

## The Full Picture (One-Page Summary)

```
SERVICE MEMORY BUDGET (Suggested Minimums by Technology)
─────────────────────────────────────────────────────────────────
  app-ui                  Nginx            512 MB
  auth-server             Spring Boot      512 MB
  gateway                 Spring Boot      512 MB
  home-service            Spring Boot      512 MB
  app-service             Spring Boot      512 MB
  assessment-service      Spring Boot      512 MB
  assessment-mcp-server   Spring Boot      512 MB
  notification-service    Spring Boot      512 MB
  home-pg                 PostgreSQL 16    512 MB
  app-pg                  PostgreSQL 16    512 MB
  assessment-pg           PostgreSQL 16    512 MB
  notification-pg         PostgreSQL 16    512 MB
  assessment-cassandra    Cassandra      2,048 MB
  debezium                Kafka Connect  1,024 MB
  zipkin                  Tracing        1,024 MB
  prometheus              Metrics          512 MB
  grafana                 Dashboards       512 MB
  kafka                   Kafka KRaft    1,024 MB
  connector-init          Init container   128 MB
─────────────────────────────────────────────────────────────────
  Total                                ~12–13 GB
─────────────────────────────────────────────────────────────────
                            │ add to server spec
                            ▼
RECOMMENDED SERVER SPEC
─────────────────────────────────────────────────────────────────
  Resource    Minimum       Recommended
  RAM         16 GB         32 GB
  CPU         4 vCPU        8 vCPU
  Disk        60 GB SSD     100 GB SSD
  Network     100 Mbps      1 Gbps
─────────────────────────────────────────────────────────────────
                            │ choose recommended
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  SERVER: 8 vCPU / 32 GB RAM / 100 GB SSD                        │
└───────────────────────────┬─────────────────────────────────────┘
                            │ subtract overhead
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  USABLE FOR APP: ~3.5 vCPU / ~18 GB RAM                         │
│  (after OS, DBs, Kafka, Debezium, monitoring consume the rest)  │
└───────────────────────────┬─────────────────────────────────────┘
                            │ determines
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  BOTTLENECK: app-service (write path)                           │
│  10 DB connections ÷ 150ms write latency = ~67 RPS ceiling      │
└───────────────────────────┬─────────────────────────────────────┘
                            │ convert
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  SYSTEM RPS: ~67 RPS (write-heavy mixed workload)               │
└───────────────────────────┬─────────────────────────────────────┘
                            │ how many requests does 1 user generate?
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  REQUESTS PER SESSION                                           │
│                                                                 │
│  Action                          Requests                       │
│  ───────────────────────────────────────                        │
│  Browse home listings               5                           │
│  Submit a loan application          3                           │
│  Check assessment status            3                           │
│  ───────────────────────────────────────                        │
│  Total per session                 11 requests                  │
│  Session duration                  10 minutes                   │
│                                                                 │
│  → 1 active user ≈ 11 req / 10 min ≈ 1.1 req/min ≈ 0.017 RPS    │
└───────────────────────────┬─────────────────────────────────────┘
                            │ 67 RPS ÷ 0.017 RPS/user
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  CONCURRENT USERS: ~4,000                                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │ peak = 10% of DAU online at once
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  DAILY ACTIVE USERS (DAU): ~40,000                              │
└─────────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  DISK GROWTH                                                    │
│  Initial usage:  ~30 GB  (Docker images + empty DBs + Kafka)    │
│  Free at start:  ~70 GB                                         │
│  Daily growth:   ~500 MB/day  (at 40,000 DAU, 400 apps/day)     │
│  Disk full in:   ~140 days  (70 GB ÷ 0.5 GB/day ≈ 4–5 months)   │
└─────────────────────────────────────────────────────────────────┘

