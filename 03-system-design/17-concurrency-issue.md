# Concurrency Issue
We need to solve two problems:
## First scenario: The same user clicks on the "Submit" button twice.
*  Hide or disable submit button.
*  Idempotency API: Add an idempotency key in application API request. 
## Second scenario: Multiple users try to submit the application for the same home.
* Create partial unique index on applications table: UNIQUE(home_id) WHERE status is not 'CLOSED'
