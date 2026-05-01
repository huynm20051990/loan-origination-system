# Concurrency Issue
We need to solve three problems:
## First scenario: Two users update the detail information for the same home at the same time.
We will need a locking mechanism. There are two types:
  * Pessimistic locking locks the data immediately so nobody else can change it. It prevents the applications from updating the data that is being changed. It is useful when data contention is high.
  * Optimistic locking checks for changes at the very end using version number. It prevents the applications from editing the stall data. It is useful when the data contention is low.

In our case, we will apply optimistic locking because it is rare that two users will update the detail information for the same home at the same time.
## Second scenario: The same user clicks on the "Submit" button twice.
*  Hide or disable submit button.
*  Idempotency API: Add an idempotency key in application API request. 
## Third scenario: Multiple users try to submit the application for the same home.
* Create partial unique index on applications table: UNIQUE(home_id) WHERE status is not 'CLOSED'
