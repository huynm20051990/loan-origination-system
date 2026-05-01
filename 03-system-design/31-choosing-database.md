# Choosing Database
Generally, we will choose Relational Database for our system because:
* Relational Database is optimized for read-heavy workflows while NoSQL is optimized for write-heavy workflows. In our scenario, the users who visit the website will be significantly higher than those who actually submit the applications. Therefore, our application will be read-heavy workflows.
* Relational Database provides ACID guarantees - they are atomicity, consistency, isolation and durability. Without those properties, it is not easy to prevent problem like double submission.
* Relational Database can make easy to model the data.

For home service, we will use Relational Vector Database because we need to store embeddings data to support semantic search function.

For chat service, we will use key-value stores because:
* It allows easy horizontal scaling.
* It provides low latency to access data.
* It is adopted by reliable proven chat applications like Facebook Messenger or Discord.