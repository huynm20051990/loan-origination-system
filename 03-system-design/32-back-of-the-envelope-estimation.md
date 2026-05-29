# Back-of-the-envelop Estimation

> **The core question:** Given this server, how many users can the system serve?
>
> **The answer chain:** Hardware → Available Resources → Component Throughput → System Bottleneck → RPS → Concurrent Users → DAU

---

## The Full Picture (One-Page Summary)

```
┌─────────────────────────────────────────────────────────────────┐
│  SERVER: 8 vCPU / 32 GB RAM / 100 GB SSD                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │ subtract overhead
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  USABLE FOR APP: ~3.5 vCPU / ~18 GB RAM                        │
│  (after OS, DBs, Kafka, Debezium, monitoring consume the rest)  │
└───────────────────────────┬─────────────────────────────────────┘
                            │ determines
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  BOTTLENECK: app-service (write path)                           │
│  10 DB connections ÷ 150ms write latency = ~67 RPS ceiling      │
│  (150ms ~ 60ms PG + 50ms Kafka + 40ms connection pools wait)    │
└───────────────────────────┬─────────────────────────────────────┘
                            │ convert
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  SYSTEM RPS: ~100 RPS (write-heavy mixed workload)             │
└───────────────────────────┬─────────────────────────────────────┘
                            │ 1 user ≈ 0.017 RPS
                            │ (11 requests per 10-min session
                            │  = 1.1 req/min ÷ 60 ≈ 0.017 RPS)
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  CONCURRENT USERS: ~4,000                                       │
└───────────────────────────┬─────────────────────────────────────┘
                            │ peak = 10% of DAU online at once
                            ▼
┌─────────────────────────────────────────────────────────────────┐
│  DAILY ACTIVE USERS (DAU): ~40,000                             │
└─────────────────────────────────────────────────────────────────┘
```

---

## Step 1 — What Hardware Do We Have?

Derived from `docker-compose.yml` `mem_limit` fields:

### Service Memory Budget

| Service                 | Type | mem_limit     |
|-------------------------|---|---------------|
| `app-ui`                | Angular frontend (Nginx) | 512 MB        |
| `auth-server`           | Spring Boot | 512 MB        |
| `gateway`               | Spring Cloud Gateway | 512 MB        |
| `home-service`          | Spring Boot | 512 MB        |
| `app-service`           | Spring Boot | 512 MB        |
| `assessment-service`    | Spring Boot | 512 MB        |
| `assessment-mcp-server` | Spring Boot | 512 MB        |
| `notification-service`  | Spring Boot | 512 MB        |
| `home-pg`               | PostgreSQL 16 | 512 MB        |
| `app-pg`                | PostgreSQL 16 (WAL/CDC) | 512 MB        |
| `assessment-pg`         | PostgreSQL 16 (WAL/CDC) | 512 MB        |
| `notification-pg`       | PostgreSQL 16 (WAL/CDC) | 512 MB        |
| `assessment-cassandra`  | Cassandra | 2,048 MB      |
| `debezium`              | Kafka Connect | 1,024 MB      |
| `zipkin`                | Distributed tracing | 1,024 MB      |
| `prometheus`            | Metrics | 512 MB        |
| `grafana`               | Dashboards | 512 MB        |
| `kafka`                 | Kafka KRaft | ~1,024 MB     |
| `connector-init`        | Kafka KRaft | ~128 MB       |
| **Total**               | | **~12–13 GB** |

### Recommended Server Spec

| Resource | Minimum | Recommended |
|---|---|---|
| RAM | 16 GB | 32 GB |
| CPU | 4 vCPU | 8 vCPU |
| Disk | 60 GB SSD | 100 GB SSD |
| Network | 100 Mbps | 1 Gbps |

### Disk Breakdown

| Item | Estimate | Why |
|---|---|---|
| Docker images (~17 images) | 8–12 GB | Each image (JDK 17 + fat jar, PostgreSQL, Kafka, etc.) averages 500MB–1GB |
| PostgreSQL data (×4) | 5–10 GB | Raw data + indexes + WAL logs; 3 DBs have `wal_level=logical` for CDC which generates extra WAL |
| Cassandra data | 5–10 GB | SSTables + background compaction; actual disk usage is 2–3× logical data size |
| Kafka log segments | 2–5 GB | Messages retained on disk until consumed; default 7-day retention adds up |
| **Total estimated** | **20–40 GB** | |

**Why recommend 100 GB if total is only 20–40 GB?**
- Data grows over time — loan applications accumulate
- Logs, crash dumps, and temp files add up
- Docker needs scratch space during builds
- Rule of thumb: **provision 2–3× estimated usage** — Linux performance degrades past 80% disk capacity

---

## Step 2 — What Resources Are Actually Available for the App?

After subtracting infrastructure overhead from the server:

### CPU Allocation

```
Total server CPU:          8.0 vCPU
  OS + Docker daemon:     -1.0 vCPU
  4× PostgreSQL:          -2.0 vCPU
  Kafka + Debezium:       -1.0 vCPU
  Monitoring stack:       -0.5 vCPU
  ────────────────────────────────
  Available for services: ~3.5 vCPU
```

### RAM Allocation

```
Total server RAM:            32.0 GB
  OS + Docker overhead:      -2.0 GB
  4× PostgreSQL (512MB ea):  -2.0 GB
  Cassandra:                 -1.0 GB
  Kafka + Debezium:          -2.0 GB
  Monitoring:                -1.5 GB
  ──────────────────────────────────
  Available for services:   ~18.0 GB
```

**Key insight:** CPU is the scarcer resource. 17 containers compete for 8 vCPU — JVM garbage collection on any service temporarily steals CPU from others.

---

## Step 3 — What Can Each Component Handle?

### Request Flow

Every user request travels this path:

```
User ──► Gateway ──► Auth-Server ──► Microservice ──► Database
         (fast,        (slow,          (medium,        (medium,
         reactive)     RSA crypto)     JVM)            I/O bound)
```

The **slowest stage determines the system throughput**.

### Component Throughput Ceilings

> **How ceilings are derived:** Little's Law — `RPS = pool_size ÷ p50_latency`.
> Spring Boot services use HikariCP with **10 connections** by default (unless configured otherwise).

| Component | Throughput Ceiling | Derivation |
|---|---|---|
| Gateway (WebFlux/reactive) | ~3,000–5,000 RPS | Non-blocking; no connection pool limit |
| **Auth-Server (RSA JWT)** | **~500–1,000 RPS** | **80 threads ÷ 20ms × 0.40 (CPU) × 0.80 (GC) ≈ 1,280 → practical ~500–1,000** |
| `home-service` | ~100–200 RPS | 10 connections ÷ 75ms avg read (p50 = 50–100ms) |
| `app-service` | ~50–100 RPS | 10 connections ÷ 150ms avg write (p50 = 100–200ms, includes Kafka + WAL) |
| `assessment-service` | ~5–20 RPS | 10 connections ÷ Gemini API latency (500ms–2s) |
| `notification-service` | ~100–200 RPS | 10 connections ÷ 75ms Kafka consume + DB write |
| PostgreSQL (per instance) | ~500–1,500 QPS | 512MB, shared disk I/O |
| Cassandra | ~2,000–5,000 ops/s | 1GB heap, good for reads |
| Kafka (single node) | ~10,000–50,000 msg/s | Not a bottleneck here |

### Latency Per Endpoint

> These are **estimates**, not measurements. Validate with load testing (Step 8).

| Endpoint Type | p50 | p99 |
|---|---|---|
| Simple read (home listings) | 50–100ms | 300–500ms |
| Loan application submit | ~150ms (60ms PG + 50ms Kafka + 40ms JVM) | 500ms–1s |
| Assessment trigger (async) | 200–500ms | 1–3s |
| Assessment result (AI) | 2–10s | 10–30s |

**How loan application submit p50 = 150ms is estimated:**

```
A. PostgreSQL write (wal_level=logical):   60ms   (WAL flush must be written to disk before ack)
B. Kafka publish + broker ack:             50ms   (producer waits for broker to confirm receipt)
C. JVM overhead + connection pool wait:    40ms   (serialization, HikariCP queue, GC pauses)
   ──────────────────────────────────────────────
   Total p50 estimate:               A+B+C = 150ms
```

---

## Step 4 — What Is the System Bottleneck?

### Bottleneck Stack Rank

```
Rank 1 → app-service (DB conn pool) — 10 HikariCP connections ÷ 150ms write latency = ~67 RPS
Rank 2 → Auth-Server (RSA JWT)      — ~500–1,000 RPS on shared CPU; not the write-path limit
Rank 3 → Shared CPU                 — 17 containers on 8 vCPU; JVM GC spikes steal cycles from all
Rank 4 → Gemini API                 — External call with 500ms–2s latency throttles assessment flow
Rank 5 → JVM heap (512MB)           — Frequent GC under load degrades p99 latency significantly
```

**The app-service connection pool is the write-path bottleneck.** With only 10 HikariCP connections and 150ms write latency (PostgreSQL + Kafka + WAL), it caps at ~67 RPS — far lower than Auth-Server's ~500 RPS.

Auth-Server is the bottleneck only for **read-only workloads**. Since this is a loan origination system (write-dominant), the connection pool wins.

---

## Step 5 — System RPS Calculation

### Method A: Little's Law (Bottom-Up)

```
Formula: RPS = Concurrency / Latency

Auth-Server concurrency:  ~80 threads (safe for 512MB JVM)
Auth-Server latency:       ~20ms (RSA validation)

Theoretical ceiling = 80 / 0.020 = 4,000 RPS

Apply degradation factors:
  Shared CPU contention:       × 0.40   (17 containers on 8 vCPU)
  JVM GC pauses:               × 0.80
  Connection pool limits:      × 0.60
  ─────────────────────────────────────
  4,000 × 0.40 × 0.80 × 0.60 ≈ 768 RPS

Mixed workload penalty (writes + CDC + Kafka):
  × 0.50
  ─────────────────────────────────────
  768 × 0.50 ≈ 384 RPS
```

### Method B: Top-Down from CPU

```
Available CPU for services: 3.5 vCPU
CPU cost per request:        ~3ms average

Ceiling = 3.5 × 1000ms / 3ms = ~1,166 RPS
At 70% safe utilization    =     ~816 RPS
Mixed workload penalty     =   × 0.50
                           ≈     408 RPS
```

Both methods estimate ~384–408 RPS. However, these assume requests are CPU-bound. In a write-heavy loan origination system, the **database connection pool** is the actual constraint.

### Reconciled System RPS

Ceiling per workload type, derived from Little's Law on the binding component:

| Workload Type | RPS | Derivation |
|---|---|---|
| Light reads only | ~100–200 RPS | home-service: 10 conn ÷ 75ms avg read |
| Loan application submissions | ~50–100 RPS | app-service: 10 conn ÷ 150ms avg write |
| Assessment flow (AI-driven) | ~5–20 RPS | assessment-service: 10 conn ÷ 500ms–2s Gemini latency |
| **Realistic mixed workload** | **~100 RPS** | Write path is binding; at 70% safe utilization |

---

## Step 6 — Convert RPS to Users

### User Behavior Model

A typical loan applicant in one session:

```
Action                          Requests
───────────────────────────────────────
Browse home listings               5
Submit a loan application          3
Check assessment status            3
───────────────────────────────────────
Total per session                 11 requests
Session duration                  10 minutes

→ 1 active user ≈ 11 req / 10 min ≈ 1.1 req/min ≈ 0.017 RPS
```

### RPS → Concurrent Users

```
System RPS (safe):    100 RPS
RPS per user:         0.017 RPS

Max concurrent users = 100 / 0.017 ≈ 5,880

Apply 70% safety margin:
  5,880 × 0.70 ≈ 4,100 concurrent users
```

### Concurrent Users → Daily Active Users (DAU)

```
Rule of thumb: peak concurrent users ≈ 10% of DAU
(not all users are online at the same moment)

DAU = 4,100 / 0.10 = 41,000

Safe round estimate: ~40,000 DAU
```

---

## Step 7 — Final Answer

| Metric | Value | How We Got There |
|---|---|---|
| Server spec | 8 vCPU / 32 GB / 100 GB SSD | Sum of docker-compose mem_limits + overhead |
| Usable CPU for app | ~3.5 vCPU | After DBs, Kafka, monitoring subtracted |
| System bottleneck | app-service (DB connection pool) | 10 HikariCP connections ÷ 150ms write latency = ~67 RPS |
| System RPS (safe) | ~100 RPS | app-service ceiling at 70% utilization |
| RPS per user | 0.017 RPS | 11 requests per 10-min session |
| Concurrent users | ~4,100 | 100 ÷ 0.017 × 0.70 |
| **Daily Active Users** | **~40,000 DAU** | 4,100 ÷ 10% peak ratio |

> **This single-server setup can comfortably serve ~40,000 DAU.**
> Suitable for early-stage production. Not suitable for high-traffic scale (200k+ DAU).

---

## Step 8 — Validate with Load Testing

Theory gives an order of magnitude. Load testing gives the real number.

**Tools:** k6, Gatling, JMeter

**Process:**
1. Baseline — one request, measure p50/p99
2. Ramp up — increase virtual users gradually
3. Find the knee — where latency spikes or error rate rises
4. That knee = your real RPS ceiling

```js
// k6 example — ramp up and find the breaking point
export const options = {
  stages: [
    { duration: '1m', target: 50  },  // ramp up
    { duration: '3m', target: 50  },  // hold
    { duration: '1m', target: 100 },  // push harder
    { duration: '3m', target: 100 },  // watch for degradation
    { duration: '1m', target: 0   },  // ramp down
  ],
  thresholds: {
    http_req_duration: ['p(99)<500'],  // fail if p99 > 500ms
    http_req_failed:   ['rate<0.01'],  // fail if error rate > 1%
  },
};
```

```
Latency |              /
        |             /  ← knee point = real RPS limit
        |____________/
        +─────────────────→ Concurrent Users
```

---

## Step 9 — How to Scale Beyond 40,000 DAU

| Bottleneck | Fix | Expected Gain |
|---|---|---|
| app-service connection pool | Increase HikariCP pool size (e.g. 10 → 50) | 5× write RPS |
| app-service connection pool | Move app-pg to a dedicated server | Removes shared-disk I/O contention |
| Auth-Server (RSA) | Add Redis cache for token validation | 3–5× RPS |
| Auth-Server (single instance) | Run 2–3 replicas behind gateway | 2–3× RPS |
| Shared CPU | Move DBs to a separate server | Frees ~3 vCPU for services |
| Single server | Kubernetes with HPA (Helm charts already in `kubernetes/`) | Horizontal scale |
| Gemini API latency | Cache assessment results + async queue | 5–10× assessment throughput |

---

## Step 10 — Disk Growth Projection (30 Years)

### Assumptions

```
Initial disk usage:   ~30 GB  (Docker images + empty DBs + Kafka setup)
Free space at start:  ~70 GB
Daily data growth:    ~1 GB/day  (at 40,000 DAU, 1,000 applications/day)

Daily breakdown:
  app-pg (application + WAL):       ~150 MB
  assessment-pg (assessment + WAL):  ~60 MB
  notification-pg:                    ~6 MB
  home-pg:                            ~1 MB
  Cassandra (+ SSTable overhead):    ~30 MB
  Application logs (rotated):       ~500 MB
  ──────────────────────────────────────────
  Total:                            ~750 MB – 1 GB/day
```

### Cumulative Growth Table

| Milestone | New Data Added | Total Cumulative | Storage Tier | Regulatory Note |
|---|---|---|---|---|
| Day 1 | ~1 GB | ~31 GB | Hot (SSD) | — |
| Month 1 | ~30 GB | ~60 GB | Hot (SSD) | — |
| **Month 2–3** | **~60–90 GB** | **~90–120 GB** | **Disk full** | — |
| Year 1 | ~365 GB | ~395 GB | Warm needed | — |
| Year 2 | ~365 GB | ~760 GB | Warm | — |
| **Year 3** | **~365 GB** | **~1.1 TB** | **Warm → Cold** | — |
| Year 5 | ~365 GB | ~1.9 TB | Cold | — |
| **Year 7** | **~365 GB** | **~2.6 TB** | **Cold** | **Min. regulatory retention (ECOA, FCRA)** |
| Year 10 | ~365 GB | ~3.7 TB | Cold | IRS / tax audit window |
| Year 15 | ~365 GB | ~5.5 TB | Cold archive | — |
| Year 20 | ~365 GB | ~7.3 TB | Cold archive | — |
| Year 25 | ~365 GB | ~9.1 TB | Cold archive | — |
| **Year 30** | **~365 GB** | **~11 TB** | **Cold archive** | — |

### Visual Timeline

```
Disk (GB)
11,000 |                                                          ●  Year 30
       |
 9,000 |                                              ●  Year 25
       |
 7,000 |                                  ●  Year 20
       |
 5,000 |                      ●  Year 15
       |
 3,700 |              ●  Year 10
 2,600 |          ●  Year 7 (regulatory min)
 1,900 |      ●  Year 5
   760 |   ●  Year 2
   100 |●  Month 3 ← current SSD full
     0 +─────────────────────────────────────────────────────────→ Time
```

### Recommended Tiered Storage Strategy

| Tier | Period | Storage Type | Capacity Needed | Est. Cost/month |
|---|---|---|---|---|
| Hot | 0–3 months | SSD (current) | 100 GB | Already provisioned |
| Warm | 3 months – 2 years | HDD or managed DB (e.g. RDS) | 1 TB | ~$50–100 |
| Cold | 2–10 years | Object storage (S3/GCS) | 4 TB | ~$80 (at $0.02/GB) |
| Archive | 10–30 years | Glacier / Coldline | 11 TB | ~$40 (at $0.004/GB) |

### Key Takeaways

- **The 100 GB SSD fills in 2–3 months** — it is only adequate as a hot-tier buffer, not long-term storage
- **Regulatory minimum (7 years) requires ~2.6 TB** — must plan for cold storage from day one
- **30-year full retention requires ~11 TB** — feasible and cheap with object storage (~$40/month on Glacier)
- **WAL overhead is significant** — 3 PostgreSQL instances with `wal_level=logical` for CDC double the write volume on disk
- **Never delete raw records** — use soft deletes; archive to cold storage instead; regulators can audit at any time
- **GDPR conflict** — if operating in the EU, anonymize rather than delete to satisfy both retention laws and right-to-erasure

---

## Config Issues to Fix Before Production

| Issue | Risk | Fix |
|---|---|---|
| Zipkin `STORAGE_TYPE=mem` | All traces lost on restart | Switch to Elasticsearch backend |
| All PG instances share one credential | Security risk in production | Per-service DB credentials |
