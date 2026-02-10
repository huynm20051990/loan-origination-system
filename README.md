# 🏦 EasyApply

EasyApply is a microservices-based loan origination system designed for speed and reliability. 
Below is a walkthrough of the user journey through the application.

---

## 📸 Application Workflow

### 1. Home
The landing page where users can start finding their dream home.
![Home](./06-user-interface/home.png)

### 2. Personal
Collection of basic user details to begin the profile.
![Personal](./06-user-interface/personal.png)

### 3. Identity
Secure identity verification step to ensure applicant authenticity.
![Identity](./06-user-interface/identity.png)

### 4. Request
Where the user specifies the loan amount.
![Request](./06-user-interface/request.png)

### 5. Review
A final check of all submitted data before triggering the process.
![Review](./06-user-interface/review.png)

### 6. Finish
Application submitted successfully. This triggers the background events via Kafka.
![Finish](./06-user-interface/finish.png)

---

## 🛠️ Tech Stack
* **Architecture:** Microservices, Event-Driven
* **Frontend:** Angular
* **Backend:** Java, Spring Boot
* **Orchestration:** Kubernetes
* **Service Mesh:** Istio
* **Monitor Tools:** Kiali, Jaeger, EFK stack, Prometheus, Grafana
* **Messaging:** Apache Kafka
* **Database:** PostgreSQL