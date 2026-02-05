Difference between optimistic and pessimistic locking.

Pessimistic locks the data immediately so nobody else can touch it, while Optimistic only checks for changes at the very end using a version number.

——interview questions——

**Interviewer:** "Can you explain the difference between Optimistic and Pessimistic locking? When would you use each?"

**Candidate:** "The main difference is when you lock the data.

* Pessimistic locking is like 'asking for permission.' You lock the data immediatelyso nobody else can touch it. I use this for high-stakes data like banking or moneybecause we cannot afford any mistakes.
* Optimistic locking is like 'asking for forgiveness.' You don't lock the data while you work. Instead, you use a version numberto check for changes at the very end. I use this for web apps because it is faster and doesn't block other users."

**Interviewer:** "What happens if two people try to save the same record at the same time in Optimistic locking?"

**Candidate:** "A conflict happens, and the system throws an exception (like DbUpdateConcurrencyException). To handle this, I catch the error and show a message to the user. I tell them, 'Someone else updated this record,' and ask them to refresh the page so they can see the new data before trying again."

**Interviewer:** "We are building a high-traffic e-commerce platform. For the 'User Profile' page, where users update their bio and profile picture, would you implement Optimistic or Pessimisticlocking? And why?"

(Hint: Think about the Chef and the Salt Shaker!)

**Candidate:** "I would choose Optimistic locking. Since profile updates are like a shared salt shaker—meaning users rarely edit the same profile at the exact same time—conflicts are rare.
Using Optimistic locking allows the system to remain scalablebecause we don't lock the database record while the user is typing. We just check the version number at the very end. If I used Pessimistic locking here, it would be like locking the whole ovenjust to look at a recipe; it would slow down the entire kitchen for no reason."

**Interviewer:** "That makes sense for profiles. But what about our 'Flash Sale' inventory? We only have 10 iPhones left, and 5,000 people are trying to buy them at the exact same second. Would you still use Optimistic locking there?

(Hint: Think about the cost of being wrong and the "Only Oven" analogy!)"

**Candidate:** "No, for a Flash Sale, I would switch to Pessimistic locking. In this case, contention is extremely high—it’s like 5,000 chefs fighting for the last 10 eggs in the kitchen.
If I used Optimistic locking, almost every transaction would fail at the last second, causing a bad experience. With Pessimistic locking, I 'lock the oven door' immediately. Only the person who has the lock can finish the purchase, ensuring our inventory count stays perfect and we don't oversell."

**Interviewer:** "Last question. When you are writing the code to handle these orders, you have a task that saves the order to the database and another task that generates a complex PDF receipt. Which one is I/O-bound and which is CPU-bound?"

(Hint: Think about 'Waiting for the Oven' vs. 'Whisking by Hand'!)

**Candidate:** "Saving the order is I/O-boundbecause it's like putting a cake in the oven—the CPU is just waiting for the database to finish writing to the disk. I would use async/await here so the thread can handle other tasks while it waits.
Generating the PDF is CPU-bound because it's like whisking egg whites by hand. The CPU has to do heavy math to calculate the layout and render the images. It requires 100% of the processor's focus until the document is finished."
