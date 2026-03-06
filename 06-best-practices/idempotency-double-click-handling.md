# Idempotency

## Q: What if the customer clicks the **Pay** button quickly twice?

**A:** I handle this using a **three‑layer defense**.

1. **UI Layer**: I immediately disable the button or show a loading spinner.
   This gives the user instant feedback and prevents additional clicks.

2. **Client‑Side Logic Layer**: I use debouncing or a flag variable (such as `isSubmitting`).
   This ensures the function runs only once, even if the user bypasses the UI.

3. **Server‑Side Logic Layer (Distributed Lock)**: I use a distributed lock in Redis to ensure that even if two separate servers receive the request, only one processes it at a time.

4. **Server Layer (Idempotency Key)**: For critical actions like payments, I require an **Idempotency‑Key** in the HTTP header.
   The server stores this key in Redis to detect and ignore duplicate requests.

---

## 1. UI Layer

I immediately disable the button or show a loading spinner.
This gives the user instant feedback and prevents additional clicks.

---

## 2. Logic Layer

### Client‑Side Logic

I use debouncing or a flag variable (such as `isSubmitting`).
This ensures the function runs only once, even if the user bypasses the UI.

```javascript
// Why this is effective (interview notes):
// 1. Early return: checks state first to block extra clicks immediately.
// 2. finally block: guarantees the lock is released even if the request fails.
// 3. Minimal overhead: lightweight logic without complex libraries.

let isSubmitting = false; // The flag (lock)

async function handleSubmit() {
    if (isSubmitting) return; // Exit if already processing

    isSubmitting = true;
    console.log("Processing request...");

    try {
        // Simulate an API call
        await new Promise(resolve => setTimeout(resolve, 2000));
        alert("Success!");
    } catch (error) {
        console.error("Failed");
    } finally {
        isSubmitting = false; // Release lock
    }
}
```

---

### Server‑Side Logic (Distributed Lock)

I use a distributed lock in Redis to ensure that even if two separate servers receive the request, only one processes it at a time.

#### OrderController

```java
// Why this is a great interview answer:
// 1. Redisson / Redis: Shows use of an industry-standard distributed locking tool.
// 2. tryLock with timeout: wait time = 0 makes duplicates fail fast.
// 3. Proper error handling: HTTP 409 Conflict is the correct REST response for duplicates.
// 4. Thread safety: isHeldByCurrentThread() prevents unlocking a lock owned by another thread.

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private RedissonClient redissonClient;

    @PostMapping("/{orderId}")
    public ResponseEntity<String> createOrder(@PathVariable String orderId) {
        RLock lock = redissonClient.getLock("lock:order:" + orderId);
        boolean isAcquired = false;

        try {
            isAcquired = lock.tryLock(0, 10, TimeUnit.SECONDS);

            if (isAcquired) {
                processDatabaseTransaction(orderId);
                return ResponseEntity.ok("Order processed successfully");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Request already in progress.");
            }
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } finally {
            if (isAcquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

#### OrderService

```java
// Key technical points (interview notes):
// 1. @Transactional: ensures atomicity — all DB operations succeed or rollback together.
// 2. Double-check pattern: existsById() is a final safety net even with Redis locks.
// 3. Spring Data JPA: abstracts SQL and handles persistence cleanly.

@Service
public class OrderService {

  @Autowired
  private OrderRepository orderRepository;

  @Transactional
  public void processDatabaseTransaction(String orderId) {
    if (orderRepository.existsById(orderId)) {
      throw new DuplicateOrderException("Order " + orderId + " already processed.");
    }

    Order newOrder = new Order();
    newOrder.setId(orderId);
    newOrder.setStatus("PAID");

    orderRepository.save(newOrder);
  }
}
```

---

## 3. Server Layer (Idempotency Key)

For critical actions like payments, I require an **Idempotency‑Key** in the HTTP header.
The server stores this key in Redis to detect and ignore duplicate requests.

### PaymentController

```java
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
  }

  @PostMapping
  public ResponseEntity<String> pay(
          @RequestHeader("Idempotency-Key") String idempotencyKey,
          @RequestBody PaymentRequest request) {

    paymentService.processPayment(idempotencyKey, request);
    return ResponseEntity.ok("Payment processed");
  }
}
```

### PaymentService

```java
@Service
public class PaymentService {

  private static final Duration IDEMPOTENCY_TTL = Duration.ofMinutes(10);

  private final StringRedisTemplate redisTemplate;

  public PaymentService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void processPayment(String idempotencyKey, PaymentRequest request) {

    String redisKey = "idempotency:payment:" + idempotencyKey;

    Boolean isFirstRequest = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, "PROCESSING", IDEMPOTENCY_TTL);

    if (Boolean.FALSE.equals(isFirstRequest)) {
      throw new DuplicateRequestException("Duplicate payment request");
    }

    // ---- Critical section ----
    chargeCustomer(request);
    savePayment(request);
  }

  private void chargeCustomer(PaymentRequest request) {
    // Call PSP / Payment Gateway
  }

  private void savePayment(PaymentRequest request) {
    // Save payment to DB
  }
}
```

### DuplicateRequestException

```java
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateRequestException extends RuntimeException {
  public DuplicateRequestException(String message) {
    super(message);
  }
}
```

### RedisConfig

```java
@Configuration
public class RedisConfig {

  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
    return new StringRedisTemplate(factory);
  }
}
```
