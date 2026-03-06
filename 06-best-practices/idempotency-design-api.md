**Q: How do you design idempotent REST APIs?**

An idempotent API ensures that making the same request multiple times has the same effectas making it once, preventing unintended side effects like duplicate payments or records during network retries.

To design an idempotent API, evaluate the operation’s impact:

* Naturally Idempotent: If the operation always results in the same state (like GET, PUT, or DELETE), simply leverage these standard HTTP methods.
* Non-Idempotent: If multiple calls create different results (like POST or PATCH). I will apply replay protection mechanism.

Replay protection mechanism:
Step 1: The client generates a unique Idempotency-Key (usually a UUID), puts it in the header and send to the server.
Step 2: Before doing anything, the server checks for that key.

* If not found, Execute the business logic (like a Stripe Payment), save the result and the key to the database.
* If found and the status linked with that key is conpleted, return the saved responseimmediately. Do not run any logic.
* If found and the status linked to that key is “In Progress”, it will return HTTP Code 409 Conflict or HTTP 425 Too Early. This tells the client: "I'm already working on this; stop spamming and wait for the result.
* If found and request body has changed, it will return HTTP Code 400 Bad Request or 422 Unprocessable Entity to prevent data inconsistency.

**Q: Where will you store Idempotency Key, cache like Redis or Database?**
A: In a production system, I often use both. I use Redis as a fast first check to block double-clicks immediately, and I use a Unique Constraint in the Database as the final source of truth to guarantee the transaction is never duplicated even if the cache fails".

**Q: How long should you store these idempotency keys in cache?**
A: I would use a 24-hour TTL (Time-To-Live) in a cache like Redis; this is long enough to handle any retries but keeps the storage from getting too full.

**Q: Can two servers process the same request at the same time and cause a double payment?**
A: Yes, this is called a Race Condition. To prevent it, I use an Atomic "SET if Not Exists" (SETNX) command in a shared cache like Redis. This ensures that even if two requests hit different servers at the exact same millisecond, the cache only allows one server to create the key and start the work, while the other server is blocked and returns a 409 Conflict.

**Q: What does SETNX mean?**
A: It stands for "SET if Not eXists." When you send this command to Redis, Redis checks if the key is already there.

* If the key is missing: Redis saves it and returns 1 (Success).
* If the key is already there: Redis does nothing and returns 0 (Fail).

**Q: Why is it called "Atomic"?**
A: "Atomic" means the operation happens as a single unit. In a distributed system, two different servers might try to create the same key at the exact same time. Because Redis is single-threaded, it processes these requests one-by-one. It will allow the first server to create the key and immediately tell the second server "No," preventing a race condition.

**Q: How does this help with Idempotency?**
A: It prevents the "double-click" problem across multiple servers.

1. Server A and B both receive a payment request with Key: 123.
2. Both send SETNX Key:123 "In-Progress" to Redis.
3. Redis gives the "Lock" to Server A and rejects Server B.
4. Server B immediately stops and returns an HTTP 409 Conflict, ensuring the payment logic only runs once.

**Q: What if the cache (Redis) fails or goes down?**
A: I use the Database as a final backup. I add a Unique Constraint (unique index) on the Idempotency-Key column in the database. If two servers somehow bypass the cache and try to save the same transaction, the database will physically reject the second one, making it impossible to create a duplicate record.

**Q: How do we handle a Cancel request while a Payment is still processing?**
A: You use a Resource-Level Lock (not an idempotency lock) or a State Machine to manage the order's status:

* 1. Check State Eligibility: When the POST /cancel arrives, the server checks the current status of the order in the database.
* 2. Handle "In-Progress" State: If the database shows the order is currently PAYMENT_PENDING (processing), you have two choices:

    * Option A (Reject): Return a 422 Unprocessable Content with a message: "Cannot cancel while payment is in progress. Please wait."
    * Option B (Queue & Async): Mark the order as CANCELLATION_REQUESTED. Once the payment process finishes, a background worker sees this flag and immediately triggers a void or refund.
* 3. Distributed Lock: For critical systems, the server uses a Distributed Lock on the Order ID (not the idempotency key). This ensures that while Server A is processing the payment, Server B cannot modify the same order until the lock is released.
