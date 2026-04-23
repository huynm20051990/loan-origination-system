# Handling Challenges With Messaging

## Transactional Messaging
A service often need to publish domain events whenever they create or update business entities.
Both database update and the sending of the message must happen within a transaction.
Otherwise, a service might update the database and then crash before sending the message.
If the service doesn't perform these two operations atomically, a failure could leave the system in an inconsistent state.

A straight forward way to reliably publish messages is to apply the Transactional Outbox pattern.
![Transactional Outbox](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/13-transactional-outbox-pattern.png)

## Handling Duplicate Messages
When the system is working normally, a message broker that guarantees at-least
once delivery will deliver each message only once. But a failure of a client, network, or
message broker can result in a message being delivered multiple times. Say a client
crashes after processing a message and updating its database—but before acknowledging the message. 
The message broker will deliver the unacknowledged message again,
either to that client when it restarts or to another replica of the client.  
To handle duplicate messages, we can track messages and discard duplicates.
![Duplicate Messages](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/14-handling-duplicate-messages.png)

## Consumer Groups
The problem here is, if we scale up the number of instances of a message consumer, for example, if
we start two instances of the microservice, both instances of the microservice will
consume the same messages. This could result in one message being processed two times, potentially leading to duplicates or other
undesired inconsistencies in the database. Therefore, we only want one instance per consumer to
process each message. This can be solved by introducing a consumer group.

![Consumer Groups](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/15-consumer-group.png)

## Guaranteed Order
If the business logic requires that messages are consumed and processed in the same order as they
were sent, we cannot use multiple instances per consumer to increase processing performance; for
example, we cannot use consumer groups. This might, in some cases, lead to an unacceptable latency
in the processing of incoming messages.
We can use partitions to ensure that messages are delivered in the same order as they were sent but
without losing performance and scalability
![Guaranteed Order](https://github.com/huynm20051990/loan-origination-system/blob/main/02-architecture/16-guaranteed-order.png)

## Retries And Dead-Letter Queue
When an event-driven consumer fails to process a message, 
we need a strategy to distinguish between temporary glitches and permanent data errors.
1. Retry Limit: You must define a maximum number of attempts (e.g., 3 retries) before giving up on a message.
2. Dead-Letter Queue (DLQ): Once the retry limit is reached, the failing message is moved to this "parking lot" queue for manual inspection or correction.
3. Exponential Backoff: To protect the system, the time between retries should increase (e.g., 1s, 5s, 30s) instead of hitting the database repeatedly at full speed.

