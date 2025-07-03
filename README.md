# 🏦 Loan Origination System

This project is a web-based Loan Origination System that allows users to explore loan products, apply for loans, track application status, and provide feedback through ratings and reviews.

## 🚀 Features

### 🔍 Loan Product Exploration
Users can browse available loan products — such as personal loans, mortgages, and auto loans — with detailed information including:
- Interest rates
- Terms and repayment options
- Eligibility criteria

### 📝 Loan Application Process
Users can:
- Submit loan applications after reviewing product details
- Provide personal and financial information
- Upload required supporting documents (e.g., ID, payslips)
- Undergo automated credit checks via an external credit service

Applications are reviewed by a loan officer. Once approved, users receive notifications and can track the status of their applications through the user dashboard.

### ⭐️ Loan Product Ratings & Reviews
Users who have had an approved loan application can:
- Submit a **rating** and **review** for the loan product they used
- Help other users assess product quality and suitability
- View average ratings and recent feedback directly on the product detail page

Reviews are moderated and only accepted from verified users with an approved loan for the respective product.

## 🧰 Technologies Used
- Java / Spring Boot (Backend)
- Angular (Frontend)
- PostgreSQL / MongoDB (Database)
- Kafka (Event-driven architecture)
- Docker, Kubernetes (Deployment)

## 📦 Architecture
The system is built using a microservices architecture and follows DDD and Hexagonal Architecture principles. Major components include:
- Product Service
- Application Service
- Review & Rating Service
- Notification Service
- Credit Check Integration
