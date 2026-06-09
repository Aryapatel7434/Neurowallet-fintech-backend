# 🚀 NeuroWallet AI Fintech Platform

> AI-Powered Secure FinTech Backend Ecosystem built using Spring Boot, Spring Security, JWT, BCrypt, MySQL, and Enterprise Backend Architecture.

---

# 🌟 Project Vision

NeuroWallet AI is a next-generation intelligent fintech backend platform designed to simulate real-world digital payment and wallet systems.

The project focuses on:

- Secure authentication & authorization
- Wallet management
- Transaction processing
- Enterprise-grade backend security
- AI-ready scalable architecture
- Real-world Spring Security internals
- Production-style REST APIs

---

# 🧠 Tech Stack

## Backend
- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate

## Security
- JWT Authentication
- BCrypt Password Encryption
- Role-Based Authorization
- Method-Level Security

## Database
- MySQL

## Tools
- Maven
- Postman
- Git & GitHub
- NetBeans / IntelliJ

---

# 🚀 PHASE 1 COMPLETED (Day 1 → Day 10)

---

# ✅ Day 1 — Project Setup & JDBC Basics

### Implemented
- Java + Maven setup
- MySQL database connection
- JDBC fundamentals
- Basic project structure

### Concepts Learned
- JDBC workflow
- DriverManager
- SQL connection handling
- Maven dependency management

---

# ✅ Day 2 — Spring Boot REST APIs

### Implemented
- Spring Boot setup
- REST Controllers
- API endpoints
- JSON request/response handling

### Concepts Learned
- @RestController
- @RequestMapping
- @PostMapping
- @GetMapping
- Dependency Injection

---

# ✅ Day 3 — Wallet System & Transactions

### Implemented
- Wallet entity
- Transaction entity
- Send money functionality
- Wallet balance updates
- Transaction history

### Concepts Learned
- JPA/Hibernate
- Entity relationships
- Service layer architecture
- Repository pattern

---

# ✅ Day 4 — JWT Authentication

### Implemented
- JWT token generation
- JWT validation
- Secure login API
- Protected APIs

### Concepts Learned
- Stateless authentication
- JWT internals
- Authentication flow
- Token-based security

---

# ✅ Day 5 — Protected APIs & Transaction History

### Implemented
- Transaction history APIs
- Protected transaction routes
- JWT filter integration

### Concepts Learned
- Request filtering
- Authorization headers
- Protected endpoint architecture

---

# ✅ Day 6 — BCrypt Password Encryption

### Implemented
- BCrypt password hashing
- Secure password storage
- Encrypted login verification

### Concepts Learned
- Password hashing
- BCryptPasswordEncoder
- Secure authentication systems

---

# ✅ Day 7 — Role-Based Authorization

### Implemented
- USER / ADMIN roles
- Admin-only APIs
- Role-based API protection

### Concepts Learned
- Authorization vs Authentication
- Role-based access control
- Secure admin architecture

---

# ✅ Day 8 — Global Exception Handling

### Implemented
- Custom exceptions
- Global exception handler
- Professional JSON error responses

### Concepts Learned
- RuntimeException
- @RestControllerAdvice
- @ExceptionHandler
- Enterprise API error handling

---

# ✅ Day 9 — Spring Security Internals

### Implemented
- CustomUserDetails
- UserDetailsService
- SecurityContextHolder integration
- UsernamePasswordAuthenticationToken

### Concepts Learned
- Spring Security internals
- Authentication lifecycle
- Security context architecture

---

# ✅ Day 10 — Method-Level Security

### Implemented
- @PreAuthorize
- Method-level authorization
- Service-layer security
- Enterprise authorization architecture

### Concepts Learned
- Method security
- Spring authorization architecture
- Clean controller design
- Enterprise backend practices

---

# 🔐 Security Features

- JWT Authentication
- BCrypt Password Encryption
- Role-Based Authorization
- Method-Level Security
- SecurityContext Authentication
- Protected APIs
- Global Exception Handling

---

# 💳 Core Functionalities

- User Registration
- Secure Login
- Wallet Creation
- Wallet Balance Management
- Send Money
- Transaction History
- Admin/User Roles
- Protected REST APIs

---

# 🏗️ Backend Architecture

```text
Controller Layer
↓
Service Layer
↓
Repository Layer
↓
MySQL Database
```

---

# 🧠 Enterprise Concepts Learned

- REST API Architecture
- Spring Security
- JWT Authentication
- BCrypt Encryption
- Role-Based Access Control
- Global Exception Handling
- SecurityContextHolder
- UserDetailsService
- Method-Level Authorization

---

# 🚀 Future Roadmap (Phase 2+)

## Upcoming Features

- Refresh Tokens
- Swagger API Documentation
- Redis Caching
- Docker Deployment
- Kafka Event Streaming
- AI Fraud Detection
- Smart Analytics
- Microservices Architecture
- API Gateway
- React Frontend
- Cloud Deployment

---

# 🎯 Project Goal

Build a production-level AI-powered fintech ecosystem capable of simulating real-world digital payment infrastructure with enterprise backend security and scalable architecture.

---

# 👨‍💻 Developer

### Arya Patel
- Competitive Programmer
- Backend Developer
- Spring Security Enthusiast
- AI + FinTech Explorer

---

# ⭐ Current Status

✅ Phase 1 Completed Successfully  
🚀 Moving toward Enterprise AI FinTech Architecture



//Phase -2

## 🚀 Day 11 — Enterprise Wallet Architecture & Secure Balance System

### ✅ Completed Features

- Created Wallet entity
- Added One-to-One relationship between User and Wallet
- Auto-created wallet during user registration
- Added wallet balance using BigDecimal
- Added wallet status and currency fields
- Implemented secure `/api/wallet/me` endpoint
- Used JWT token to fetch logged-in user's wallet
- Fixed Spring Security JWT filter flow
- Fixed role-based access issue with `@PreAuthorize`
- Added Access Denied handling in Global Exception Handler

---

### 🧠 Key Concepts Learned

- Why money should use `BigDecimal`
- Why wallet data should be separate from user data
- One-to-One entity mapping in JPA
- `@JoinColumn` usage
- `SecurityContextHolder` usage
- JWT authentication flow
- Role-based authorization using `hasRole('USER')`
- Why JwtFilter should not skip protected APIs
- Why JPA entities need a no-argument constructor

---

### 🔐 API Added

#### Get My Wallet

```http


## 🚀 Day 12 — Add Money API & Transaction Safety

### ✅ Completed Features

- Created AddMoneyRequest DTO
- Implemented secure add-money API
- Added wallet balance update logic
- Used BigDecimal for money calculations
- Added amount validation
- Used SecurityContextHolder for authenticated user
- Added transactional wallet update using @Transactional
- Integrated JWT-secured wallet operations
- Tested wallet balance updates in MySQL
- Added negative amount validation handling

---

### 🧠 Key Concepts Learned

- DTO (Data Transfer Object)
- Financial precision using BigDecimal
- Why fintech systems avoid double for money
- Secure balance update flow
- Spring Security authenticated user extraction
- @Transactional database consistency
- Business validation logic
- Secure wallet APIs using JWT authentication

---

### 🔐 APIs Added

#### Add Money API

```http
POST /api/wallet/add-money
GET /api/wallet/me


## 🚀 Day 13 — Withdraw Money API & Debit Transaction Validation

### ✅ Completed Features

- Created WithdrawRequest DTO
- Implemented secure withdraw money API
- Added wallet debit balance update logic
- Added insufficient balance validation
- Prevented negative withdrawal amounts
- Used BigDecimal subtract operations
- Added JWT-secured withdrawal operations
- Used SecurityContextHolder for authenticated wallet access
- Added transactional debit operations using @Transactional
- Verified wallet balance updates in MySQL

---

### 🧠 Key Concepts Learned

- Debit transaction systems
- Insufficient balance protection
- Financial validation rules
- BigDecimal subtract operations
- Transaction safety in fintech systems
- JWT-secured balance updates
- Secure authenticated wallet access
- Business logic validation
- Transaction rollback concepts using @Transactional

---

### 🔐 APIs Added

#### Withdraw Money API

```http
POST /api/wallet/withdraw



## 🚀 Day 14 — Peer-to-Peer Transaction Engine

### ✅ Completed Features

- Created TransactionRequest DTO
- Implemented secure send money API
- Added peer-to-peer wallet transfer system
- Added sender and receiver validation
- Added insufficient balance protection
- Prevented self-transfers
- Created Transaction entity
- Stored transaction history in database
- Added transaction history retrieval API
- Used JWT authentication for secure sender identification
- Implemented atomic debit-credit transaction flow using @Transactional
- Verified wallet balances and transaction records in MySQL

---

### 🧠 Key Concepts Learned

- Peer-to-peer transaction systems
- Atomic database transactions
- Debit-credit transaction flow
- Secure JWT-based sender identification
- Transaction persistence
- Financial consistency validation
- BigDecimal transaction calculations
- Transaction history systems
- Business validation rules
- Transaction rollback concepts

---

### 🔐 APIs Added

#### Send Money API

```http
POST /api/transactions/send


## 🚀 Day 15 — Transaction Analytics + Pagination + Sorting

### ✅ Completed Features

- Implemented paginated transaction history APIs
- Added latest-first transaction sorting
- Added sent transactions retrieval API
- Added received transactions retrieval API
- Used Spring Data Pageable and PageRequest
- Added scalable transaction retrieval architecture
- Implemented query parameter pagination
- Added transaction filtering system
- Tested multiple transaction pages
- Verified transaction analytics in MySQL

---

### 🧠 Key Concepts Learned

- Pagination in Spring Boot
- Pageable and PageRequest
- Enterprise transaction retrieval systems
- Latest-first sorting using Sort.by()
- Query parameter handling
- Scalable API design
- Financial transaction analytics
- Sent vs received transaction filtering
- Optimized backend retrieval systems

---

### 🔐 APIs Added

#### Paginated Transaction History

```http
GET /api/transactions/history?page=0&size=5




## 🚀 Day 16 — Transaction Status System & Failed Transaction Handling

### ✅ Completed Features

- Created TransactionStatus enum
- Added PENDING, SUCCESS, and FAILED transaction lifecycle
- Updated Transaction entity to use enum-based status
- Implemented professional transaction state management
- Added failed transaction handling
- Added pending transaction creation before money transfer
- Updated successful transaction completion flow
- Added audit-style transaction lifecycle tracking
- Improved fintech transaction architecture
- Verified transaction status updates in MySQL

---

### 🧠 Key Concepts Learned

- Transaction lifecycle management
- Enum usage in Spring Boot JPA
- Financial transaction state systems
- Audit-friendly transaction architecture
- Failed transaction handling
- Transaction consistency design
- Pending transaction processing
- Transaction rollback concepts
- Enterprise fintech transaction flow

---

### 🔐 Transaction Lifecycle Implemented

```text id="f6shif"
PENDING → SUCCESS
PENDING → FAILED




# 🚀 Day 17 — Admin Analytics & Failed Transaction Monitoring

## ✅ Features Implemented

- Transaction Status System
  - SUCCESS
  - FAILED
  - PENDING

- Failed Transaction Audit Logging

- Admin Analytics APIs

- Success Transaction Monitoring

- Failed Transaction Monitoring

- Pagination for Transaction APIs

- Role-Based Admin Access

- Secure JWT Protected APIs

---

## ✅ APIs Built

### User APIs

- Send Money
- Transaction History
- Sent Transactions
- Received Transactions

### Admin APIs

- View Failed Transactions
- View Successful Transactions
- Transaction Analytics Dashboard

---

## ✅ Security

- Spring Security
- JWT Authentication
- Role-Based Authorization
- Method-Level Security
- ADMIN & USER role handling

---

## ✅ Enterprise Concepts Learned

- Fintech Transaction Monitoring
- Audit Logging
- Failed Transaction Persistence
- Admin Dashboard Backend
- Pagination Architecture
- Secure Role-Based APIs
- Transaction Analytics Systems

---

## ✅ Database Verification

Verified:
- SUCCESS transactions
- FAILED transactions
- Analytics counts
- Transaction history
- Audit records

---

## ✅ Tech Stack

- Spring Boot
- Spring Security
- JWT
- MySQL
- JPA/Hibernate
- Maven
- REST APIs




# 🚀 Day 18 — Transaction Search & Filtering System

## ✅ Features Implemented

- Transaction search by email
- Amount range transaction filtering
- Admin search APIs
- Latest-first transaction sorting
- Pageable transaction filtering
- Search using Spring Data JPA derived queries
- Enterprise admin transaction monitoring

---

## ✅ APIs Added

### Search Transactions by Email

```http
GET /api/transactions/admin/search?email=rahul@gmail.com&page=0&size=5




🚀 Day 19 — Scheduled Transactions System
✅ Features Built
Schedule future wallet transactions
Future time validation
Scheduled transaction database storage
PENDING transaction status support
JWT secured scheduling API
MySQL persistence verification
🧠 Backend Concepts Learned
Spring Boot scheduling architecture
Fintech payment scheduling flow
LocalDateTime handling
Entity persistence with JPA
DTO to Entity mapping
Enum status management




🚀 Day 20 — Automatic Scheduled Transaction Processor
✅ Features Built
Automatic scheduled transaction execution
Background scheduler service
Scheduled payment processor
Auto wallet balance transfer
SUCCESS / FAILED status update
Fintech cron-job architecture
MySQL scheduler verification
🧠 Enterprise Backend Concepts Learned
Spring Scheduler (@Scheduled)
Background job processing
Automated transaction execution
Payment scheduling systems
Transaction automation
Fintech backend architecture
Real-world cron systems
⚙ Technologies Used
Spring Boot
Spring Scheduler
Spring Security JWT
MySQL
JPA Hibernate
🔄 Scheduler Flow
PENDING
   ↓
Scheduler checks every 30 sec
   ↓
If scheduled time reached
   ↓
Balance transfer executes
   ↓
SUCCESS / FAILED
🛠 APIs Tested
Schedule Transaction
POST /api/scheduled-transactions/schedule
✅ Database Verification

Verified:

scheduled_transaction table
wallet balance auto-update
SUCCESS status update
FAILED status handling




## 🚀 Phase 3 - Enterprise Backend Infrastructure Engineering

### ✅ Day 21 - Wallet Caching & Performance Optimization

Implemented caching for the wallet module to improve API performance and reduce unnecessary database queries.

### 🔥 Features Added
- Added Spring Boot cache support
- Implemented wallet cache using `@Cacheable`
- Implemented cache clearing using `@CacheEvict`
- Optimized `/api/wallet/me` API
- Prevented stale wallet balance after add money / withdraw / send money
- Improved read-heavy fintech API performance

### 🧠 Concepts Learned
- Cache Hit
- Cache Miss
- Cache Eviction
- Stale Cache Problem
- Cache-Aside Pattern
- Read Optimization
- Fintech Wallet Consistency
- Backend Performance Engineering

### 🏦 Real Fintech Use Case
Wallet balance is a read-heavy feature. Users repeatedly check their balance from dashboard, profile, and transaction pages. Caching avoids repeated MySQL queries and improves response speed.

### ✅ Testing Completed
- First `/api/wallet/me` request fetched data from MySQL
- Second `/api/wallet/me` request returned data from cache
- `add-money` cleared old wallet cache
- Next `/api/wallet/me` request fetched fresh updated balance
- MySQL connection verified
- Postman testing completed successfully

### 🧪 API Tested
```http
GET /api/wallet/me
POST /api/wallet/add-money
POST /api/wallet/withdraw
POST /api/transactions/send





## 🚀 Day 22 - OTP Verification System & Email Integration

### 🔥 Features Implemented

- OTP Generation System
- Email OTP Delivery
- OTP Verification API
- OTP Expiration Validation
- Gmail SMTP Integration
- App Password Authentication
- Secure OTP Storage
- OTP Cleanup After Verification

### 🏦 Fintech Use Cases

- Forgot Password Verification
- User Email Verification
- Transaction Confirmation
- Two-Factor Authentication (2FA)
- Account Recovery Security

### 📦 APIs Developed

#### Send OTP

POST /api/otp/send

```json
{
  "email": "user@gmail.com"
}

Day 23 Completed ✅

Features Added:
- API Rate Limiting
- Login Request Protection
- OTP Request Protection
- Brute Force Attack Prevention
- Gmail OTP Integration
- Secure Configuration Setup

Tech Stack:
- Spring Boot
- Spring Security
- JWT
- MySQL
- Java Mail Sender
- HashMap-based Rate Limiter

Status:
Phase 3 Security Module In Progress





# 🚀 NeuroWallet AI Fintech Platform

## Day 25 – Apache Kafka Integration

### 📌 Overview

Day 25 introduces **Apache Kafka** into NeuroWallet to enable an event-driven architecture.

Instead of tightly coupling transaction processing with notifications and analytics, NeuroWallet now publishes transaction events to Kafka topics and allows consumers to process those events asynchronously.

This improves:

- Scalability
- Reliability
- Performance
- Decoupling of Services
- Real-Time Event Processing

---

## 🏗 Kafka Architecture

```text
User
 |
 v
Transaction Service
 |
 v
Transaction Saved (MySQL)
 |
 v
Kafka Producer
 |
 v
Topic: transaction-events
 |
 +---------------------------+
 |            |              |
 v            v              v
Email      Audit       Analytics
Consumer   Consumer    Consumer
```

---

## ✨ Features Added

### Kafka Producer

Publishes transaction events whenever a transaction is successfully completed.

Example Event:

```json
{
  "senderEmail": "ary@gmail.com",
  "receiverEmail": "rahul@gmail.com",
  "amount": 1000,
  "status": "SUCCESS"
}
```

### Kafka Topic

```text
transaction-events
```

Stores all transaction-related events.

### Kafka Consumer

Consumes transaction events asynchronously.

Current Consumer Actions:

- Receive Event
- Print Event Details
- Prepare Notification Flow

Future Enhancements:

- Email Notifications
- SMS Notifications
- Audit Logging
- Analytics Processing

---

## 📂 Kafka Components

### TransactionEvent

DTO representing a transaction event.

Fields:

- senderEmail
- receiverEmail
- amount
- status

---

### TransactionEventProducer

Responsible for:

```text
Java Object
      ↓
JSON Conversion
      ↓
Kafka Topic Publish
```

Uses:

```java
KafkaTemplate<String, String>
ObjectMapper
```

---

### TransactionEventConsumer

Responsible for:

```text
Kafka Topic
      ↓
Receive Message
      ↓
Process Event
```

Uses:

```java
@KafkaListener
```

---

## ⚙ Kafka Configuration

```properties
spring.kafka.bootstrap-servers=localhost:9092

spring.kafka.consumer.group-id=neurowallet-group
spring.kafka.consumer.auto-offset-reset=earliest

spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer

spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer

logging.level.org.springframework.kafka=INFO
logging.level.org.apache.kafka=WARN
```

---

## 🔄 Event Flow

### Step 1

User sends money:

```http
POST /api/transactions/send
```

### Step 2

Transaction saved to MySQL.

### Step 3

Transaction event created.

```java
TransactionEvent event =
    new TransactionEvent(
        senderEmail,
        receiverEmail,
        amount,
        "SUCCESS"
    );
```

### Step 4

Producer publishes event.

```java
transactionEventProducer
        .publishTransactionEvent(event);
```

### Step 5

Kafka stores event in:

```text
transaction-events
```

### Step 6

Consumer receives event.

```java
@KafkaListener(
    topics = "transaction-events",
    groupId = "neurowallet-group"
)
```

### Step 7

Notification, Audit, or Analytics actions can be executed.

---

## 📸 Sample Console Output

### Producer Output

```text
Kafka event published:
{
 "senderEmail":"ary@gmail.com",
 "receiverEmail":"rahul@gmail.com",
 "amount":1000,
 "status":"SUCCESS"
}
```

### Consumer Output

```text
Kafka event received:
{
 "senderEmail":"ary@gmail.com",
 "receiverEmail":"rahul@gmail.com",
 "amount":1000,
 "status":"SUCCESS"
}

Notification can be sent from here
```

---

## 🎯 Benefits of Kafka Integration

### Asynchronous Processing

Transaction service does not wait for notifications.

### Loose Coupling

Services communicate through Kafka instead of direct API calls.

### Fault Tolerance

Events remain in Kafka even if consumers are temporarily unavailable.

### Scalability

Multiple consumers can process events in parallel.

### Real-Time Streaming

Events are processed immediately after transaction completion.

---

## 🛠 Technologies Used

### Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate

### Database

- MySQL

### Messaging

- Apache Kafka

### Security

- JWT Authentication
- BCrypt Password Encoding

### Additional Features

- Redis Cache
- OTP Verification
- Email Integration
- API Rate Limiting
- Login Attempt Tracking
- Audit Logging
- Scheduled Transactions

---

## 📈 Project Progress

### Phase 1 – Authentication & Wallet

- ✅ JWT Authentication
- ✅ BCrypt Password Encryption
- ✅ Role-Based Authorization
- ✅ Wallet Management
- ✅ Transaction Management

### Phase 2 – Enterprise Features

- ✅ Pagination
- ✅ Search & Filtering
- ✅ Admin Analytics
- ✅ Scheduled Transactions

### Phase 3 – Performance & Scalability

- ✅ Redis Caching
- ✅ OTP Verification
- ✅ Email Service Integration
- ✅ API Rate Limiting
- ✅ Login Attempt Tracking
- ✅ Audit Logging
- ✅ Apache Kafka Integration

---

## 🚀 Next Milestone (Day 26)

Planned Enhancements:

- Kafka-Based Email Notifications
- Kafka-Based Audit Processing
- Kafka Analytics Consumer
- Docker Integration
- Project Deployment Preparation

---

## 👨‍💻 Author

**Ary Patel**

B.Tech Information Technology  
Dharmsinh Desai University (DDU)

### NeuroWallet AI Fintech Platform

A secure, scalable, and event-driven digital wallet platform built using Spring Boot, MySQL, Redis, Kafka, and JWT Security.




# Day 26 — Dockerization + Kafka Integration

## Objective

Containerize the NeuroWallet backend using Docker and verify end-to-end Kafka event streaming inside the application.

---

## Features Implemented

### Docker Integration

* Created Dockerfile for Spring Boot application
* Built Docker image using Java 17
* Generated executable JAR file
* Ran NeuroWallet inside Docker container
* Exposed application on port 8080

### Kafka Integration

* Kafka Producer publishes transaction events
* Kafka Consumer receives transaction events
* Kafka topic: `transaction-events`
* Consumer group: `neurowallet-group`
* Real-time event processing verified

### Event Flow

User Sends Money

↓

TransactionService

↓

TransactionEventProducer

↓

Kafka Topic (`transaction-events`)

↓

TransactionEventConsumer

↓

Notification Service (Future Extension)

---

## Docker Commands Used

### Build JAR

```bash
mvn clean package
```

### Build Docker Image

```bash
docker build -t neurowallet-backend .
```

### Run Container

```bash
docker run --name neurowallet-app -p 8080:8080 \
-e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/smartwalletdb \
-e SPRING_DATASOURCE_USERNAME=root \
-e SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD \
-e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
neurowallet-backend
```

---

## Kafka Verification

### Create Topic

```bash
kafka-topics.bat --create \
--topic transaction-events \
--bootstrap-server localhost:9092
```

### Verify Topic

```bash
kafka-topics.bat --list \
--bootstrap-server localhost:9092
```

Output:

```text
transaction-events
```

---

## Postman Testing

### Login

```http
POST /api/auth/login
```

### Add Money

```http
POST /api/wallet/add-money
```

### Send Money

```http
POST /api/transactions/send
```

### Transaction History

```http
GET /api/transactions/history
```

---

## Successful Output

Producer Log:

```text
Kafka event published
```

Consumer Log:

```text
Kafka event received
Notification can be sent from here
```

---

## Technologies Used

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* MySQL
* Redis Cache
* Apache Kafka
* Docker
* Maven

---

## Day 26 Outcome

Successfully containerized NeuroWallet backend and verified real-time Kafka event streaming between producer and consumer services running with Docker support.

Status: ✅ Completed



# Day 27 – Docker Compose with MySQL and Kafka

## Overview

Day 27 focused on orchestrating the complete NeuroWallet platform using Docker Compose. Instead of manually starting MySQL, Kafka, and Spring Boot separately, the entire system can now be launched using a single command.

## Features Implemented

### Docker Compose Setup

* Created `docker-compose.yml`
* Automated container orchestration
* Simplified development environment

### MySQL Container

* MySQL 8.0 containerized
* Database exposed on port 3307
* Persistent database configuration

### Kafka Container

* Apache Kafka containerized
* Event streaming infrastructure
* Exposed on port 9092

### Spring Boot Container

* NeuroWallet application containerized
* Exposed on port 8080
* Connected to MySQL and Kafka

## Containers

### NeuroWallet Application

* Port: 8080

### MySQL

* Port: 3307

### Kafka

* Port: 9092

## Docker Commands

Build and start:

```bash
docker compose up -d
```

Stop:

```bash
docker compose down
```

View running containers:

```bash
docker ps
```

View logs:

```bash
docker compose logs
```

## Learning Outcomes

* Docker Compose fundamentals
* Multi-container applications
* Container networking
* Service orchestration
* Spring Boot container deployment
* Kafka container deployment
* MySQL container deployment

## Result

NeuroWallet can now be started using a single Docker Compose command, making the application easier to deploy, manage, and scale.




## Day 30 - Enterprise Logging

Implemented industry-level logging using SLF4J and Spring Boot Logback.

### Features Added
- Replaced System.out.println with logger
- Added INFO logs for successful flow
- Added ERROR logs for failure cases
- Added Kafka producer and consumer logging
- Improved debugging and production readiness

### Concepts Learned
- Why logging is important in backend systems
- Difference between System.out.println and Logger
- Logging levels: INFO, WARN, ERROR, DEBUG
- How industry debugs production issues




## Day 32 - Request/Response Tracking

Implemented request/response tracking using Spring Boot filter and MDC.

### Features Added
- Added unique request ID for every API call
- Logged incoming API request method and URI
- Logged response status code
- Logged API execution time
- Added MDC-based request tracing
- Improved production debugging and monitoring

### Files Added
- RequestResponseLoggingFilter.java

### Files Updated
- application.properties

### Concepts Learned
- OncePerRequestFilter
- MDC
- Request ID
- API tracing
- Execution time tracking
- Production request monitoring

### Testing
Tested with:
- Login API
- Get Wallet API
- Add Money API
- Withdraw API
- Send Money API





# Day 33 — Advanced Validation

## Overview

Implemented enterprise-level request validation using Jakarta Bean Validation in NeuroWallet AI Fintech Platform.

This ensures invalid requests are rejected before reaching business logic, improving security, data integrity, and API reliability.

---

## Technologies Used

* Spring Boot Validation
* Jakarta Validation
* Global Exception Handling
* DTO Validation
* REST API Best Practices

---

## Features Implemented

### Login Validation

* Email cannot be blank
* Email format validation
* Password cannot be blank

### User Registration Validation

* Name required
* Email required
* Email format validation
* Password minimum length validation
* Role required

### Wallet Validation

* Add money amount required
* Minimum amount validation
* Withdraw amount required
* Minimum amount validation

### Transaction Validation

* Receiver email required
* Receiver email format validation
* Amount required
* Minimum amount validation

---

## Controller Improvements

Added:

```java
@Valid
```

to request DTOs in:

* AuthController
* UserController
* WalletController
* TransactionController

This automatically validates request payloads before executing service logic.

---

## Global Exception Handling

Added validation exception handling using:

```java
MethodArgumentNotValidException
```

Invalid requests now return clean JSON responses.

Example:

```json
{
  "timestamp": "2026-06-05T19:30:00",
  "status": 400,
  "message": "Receiver email is required"
}
```

---

## DTOs Updated

### LoginRequest

* @NotBlank
* @Email

### RegisterRequest

* @NotBlank
* @Email
* @Size

### AddMoneyRequest

* @NotNull
* @DecimalMin

### WithdrawRequest

* @NotNull
* @DecimalMin

### TransactionRequest

* @NotBlank
* @Email
* @NotNull
* @DecimalMin

---

## Postman Testing

### Test 1 — Empty Email

Expected:

```json
{
  "status": 400,
  "message": "Email is required"
}
```

### Test 2 — Invalid Email

Expected:

```json
{
  "status": 400,
  "message": "Invalid email format"
}
```

### Test 3 — Negative Amount

Expected:

```json
{
  "status": 400,
  "message": "Amount must be at least 1"
}
```

### Test 4 — Empty Receiver Email

Expected:

```json
{
  "status": 400,
  "message": "Receiver email is required"
}
```

### Test 5 — Invalid Receiver Email

Expected:

```json
{
  "status": 400,
  "message": "Invalid receiver email format"
}
```

---

## Industry Benefits

* Prevents invalid requests
* Reduces service-layer validation code
* Improves API security
* Improves frontend integration
* Provides consistent error responses
* Follows enterprise Spring Boot standards

---

## Concepts Learned

* Bean Validation
* Jakarta Validation
* @Valid
* @NotBlank
* @NotNull
* @Email
* @DecimalMin
* MethodArgumentNotValidException
* Global Exception Handling
* API Input Validation

---

## Project Status

Day 33 Successfully Completed ✅

NeuroWallet AI Fintech Platform now supports enterprise-grade request validation.





# Day 34 — JUnit Testing Fundamentals

## Overview

Implemented JUnit 5 testing fundamentals for NeuroWallet AI Fintech Platform.

This phase introduces automated unit testing, assertions, test lifecycle management, and the Arrange-Act-Assert testing pattern used in enterprise software development.

---

## Technologies Used

* JUnit 5
* Maven Test Framework
* Spring Boot Test Dependencies

---

## Objectives

* Understand Unit Testing
* Learn JUnit 5 Fundamentals
* Implement Assertions
* Learn Test Lifecycle
* Understand Arrange-Act-Assert Pattern
* Execute Automated Test Cases

---

## Test Package Structure

```text
src/test/java
└── com.smartwallet.service
    └── WalletServiceTest.java
```

---

## Features Implemented

### Basic Assertion Testing

Implemented:

```java
assertEquals()
assertTrue()
assertFalse()
assertNotNull()
```

---

### Test Lifecycle

Implemented:

```java
@BeforeEach
@AfterEach
```

Used to perform setup and cleanup operations before and after each test.

---

### AAA Testing Pattern

Every test follows:

```text
Arrange
Act
Assert
```

Example:

```java
// Arrange
int balance = 1000;

// Act
int updatedBalance = balance + 500;

// Assert
assertEquals(1500, updatedBalance);
```

---

## Test Cases Implemented

### shouldAddTwoNumbers()

Validates arithmetic assertion.

### amountShouldBePositive()

Validates positive amount condition.

### amountShouldNotBeNegative()

Validates negative amount handling.

### objectShouldNotBeNull()

Validates object existence.

### withdrawShouldReduceBalance()

Simulates wallet withdrawal logic.

### depositShouldIncreaseBalance()

Simulates wallet deposit logic.

---

## Test Results

```text
Tests Run: 6
Failures: 0
Errors: 0
BUILD SUCCESS
```

All test cases executed successfully.

---

## Concepts Learned

* Unit Testing
* Automated Testing
* JUnit 5
* Assertions
* Test Lifecycle
* AAA Pattern
* Test Execution
* Build Verification

---

## Industry Relevance

Unit testing is used by:

* Google
* Amazon
* Netflix
* Uber
* Banking Applications
* Fintech Platforms

Automated tests help prevent bugs before deployment and are integrated into CI/CD pipelines.

---

## Interview Questions Covered

### What is JUnit?

A Java testing framework used for automated unit testing.

### What does @Test do?

Marks a method as a test case.

### What is assertEquals?

Verifies expected value equals actual value.

### What is @BeforeEach?

Runs before every test method.

### What is @AfterEach?

Runs after every test method.

### What is AAA Pattern?

Arrange → Act → Assert

Standard structure used in professional test design.

---

## Project Status

Day 34 Successfully Completed ✅

NeuroWallet AI Fintech Platform now includes foundational automated unit testing using JUnit 5.






# Day 35 - JUnit & Mockito Testing

## Features Implemented

- JUnit 5 Unit Testing
- Mockito Mocking Framework
- Repository Mock Testing
- Maven Test Lifecycle
- ByteBuddy Integration for Mockito
- WalletService Unit Tests

## Test Results

Tests run: 7
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS

## Technologies Used

- JUnit 5
- Mockito
- Maven Surefire
- ByteBuddy
- Spring Boot Test

## Learning Outcomes

- Created first JUnit tests
- Learned Mockito annotations
- Mocked repository layer
- Executed Maven test lifecycle
- Fixed Java/Mockito compatibility issues




## Day 36 - Integration Testing

- User Registration API Test
- Login API Test
- Wallet API Test
- Transaction API Test
- JWT Security Validation
- Redis Cache Verification
- Kafka Producer Verification
- MockMvc Integration Testing



Day 37 - Enterprise Logging & Kafka Integration ✅

✔ Integrated Apache Kafka Producer
✔ Transaction Event Publishing
✔ Enterprise Logging Configuration
✔ Request/Response Logging
✔ Integration Tests
✔ Swagger Validation
✔ Maven Build Success
✔ MySQL Integration
✔ JWT Security Validation

Tests Passed: 15/15
Build Status: SUCCESS






# Day 39 - Docker Compose Integration

## Overview

Today I containerized the complete NeuroWallet AI Fintech Platform using Docker Compose.

The application now runs as a multi-container system with:

- Spring Boot Backend
- MySQL Database
- Apache Kafka
- Docker Compose Orchestration

---

## Technologies Used

- Java 17
- Spring Boot 3
- Spring Security
- JWT Authentication
- MySQL 8
- Apache Kafka
- Docker
- Docker Compose
- Swagger OpenAPI
- Spring Actuator

---

## Docker Architecture

+----------------------+
| Spring Boot Backend  |
| Port: 8080           |
+----------+-----------+
           |
           |
           v
+----------------------+
| MySQL Database       |
| Port: 3307           |
+----------------------+

           |
           |
           v

+----------------------+
| Apache Kafka         |
| Port: 9092           |
+----------------------+

---

## Docker Compose Configuration

### Services

### MySQL

- Image: mysql:8.0
- Port: 3307
- Database: smartwalletdb

### Kafka

- Image: apache/kafka:latest
- Port: 9092

### NeuroWallet Backend

- Spring Boot Application
- Port: 8080

---

## Docker Commands Used

### Build Containers

```bash
docker compose build
```

### Start Containers

```bash
docker compose up -d
```

### Check Running Containers

```bash
docker compose ps
```

### View Logs

```bash
docker logs neurowallet-app
docker logs neurowallet-kafka
docker logs neurowallet-mysql
```

### Stop Containers

```bash
docker compose down
```




# Day 40.5 — Database Indexing

## Objective

Improve database query performance using indexing.

## Why Indexing?

Without indexing, the database performs a full table scan when searching for records.

With indexing, the database uses a B-Tree structure for faster lookups.

## Implementation

Updated User entity:

* Added unique constraint on email
* Added database index on email column

```java
@Column(unique = true, nullable = false)
private String email;
```

## Verification

Executed:

```sql
SHOW INDEX FROM users;
```

Result:

* PRIMARY index on user_id
* BTREE index on email

## Industry Usage

Used in:

* Google Accounts
* Amazon Users
* Banking Systems
* Fintech Platforms

## Interview Questions

### Why use indexing?

Improves query performance by avoiding full table scans.

### Which columns should be indexed?

Frequently searched columns:

* Email
* Username
* Transaction ID
* Phone Number

### Does indexing always improve performance?

No.

Read operations become faster.

Write operations become slightly slower because indexes must also be updated.

## Outcome

Database indexing successfully implemented and verified.





# Day 40.6 — Performance Testing

## Objective

Validate NeuroWallet API performance under concurrent load.

## Tool Used

Apache JMeter 5.6.3

## API Tested

POST /api/auth/login

## Test Configuration

Users (Threads): 100

Ramp-Up Period: 10 Seconds

Loop Count: 1

## Results

Samples: 100

Average Response Time: 1913 ms

Minimum Response Time: 113 ms

Maximum Response Time: 5242 ms

Error Rate: 0%

Throughput: 10.2 Requests/Second

## Outcome

- Login API successfully handled 100 requests.
- No request failures occurred.
- Backend remained stable under load.

## Industry Usage

Performance testing is used by:

- Google
- Amazon
- Netflix
- Banking Systems
- Fintech Platforms

before production deployment.

## Interview Questions

Q: What is Throughput?

Number of requests processed per second.

Q: What is Response Time?

Time required to process a request.

Q: Why perform Load Testing?

To verify system stability under concurrent users.

## Status

Day 40.6 Completed Successfully.





## Day 40.7 — Integration Testing

### Objectives

* Validate login API using Spring Boot Integration Testing.
* Test complete authentication flow.
* Verify JWT and Refresh Token generation.
* Validate MockMvc configuration.

### Technologies

* Spring Boot Test
* JUnit 5
* MockMvc
* BCrypt Password Encoder

### Implemented

* LoginControllerIntegrationTest
* Test user creation before execution
* API request simulation using MockMvc
* Authentication response validation

### Results

* Login API returns HTTP 200
* JWT Access Token generated successfully
* Refresh Token generated successfully
* BUILD SUCCESS achieved

### Industry Relevance

Integration testing verifies complete application behavior by testing Controller → Service → Repository → Database interaction together.

### Status

✅ Completed




Day 40.8 — Password Reset Flow
Objective

Implement a secure Password Reset Flow that allows users to reset forgotten passwords using a temporary reset token.

Features Implemented
Password Reset Token Entity

Created a dedicated entity to store:

Reset Token
User Email
Expiry Time

This ensures password reset requests are time-bound and secure.

Password Reset Token Repository

Implemented repository support for:

Saving reset tokens
Fetching tokens
Validating token existence
Forgot Password API

Endpoint:

POST /api/auth/forgot-password

Request:

{
  "email": "admin@gmail.com"
}

Response:

Reset Token: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx

Functionality:

Verifies user exists
Generates unique token
Stores token in database
Returns token
Reset Password API

Endpoint:

POST /api/auth/reset-password

Request:

{
  "token": "generated-token",
  "newPassword": "Admin@123"
}

Functionality:

Validates token
Checks expiry
Encrypts password using BCrypt
Updates user password
Invalidates token
Security Configuration Updates

Added public access for:

/api/auth/forgot-password
/api/auth/reset-password
/api/auth/refresh

Updated SecurityConfig to prevent 403 errors during password recovery.

Testing Performed
Forgot Password

Test Result:

200 OK
Reset Token Generated
Reset Password

Test Result:

Password Updated Successfully
Login Verification

Login tested with new password:

{
  "email": "admin@gmail.com",
  "password": "Admin@123"
}

Result:

{
  "accessToken": "...",
  "refreshToken": "..."
}

Authentication successful.

Security Benefits
BCrypt password encryption
Token-based password recovery
Expiry-based validation
Secure password updates
JWT authentication compatibility
Technologies Used
Spring Boot
Spring Security
Spring Data JPA
MySQL
BCryptPasswordEncoder
JWT Authentication





# Day 40.9 — Security Review

## Features Reviewed

### JWT Authentication
- Login API tested
- JWT generation verified
- Protected endpoints validated

### Role Based Authorization
- Admin APIs restricted
- USER role access validated
- Spring Security role mapping verified

### Password Reset Flow
- Forgot Password API tested
- Reset Password API tested
- Password update validation completed

### Refresh Token Validation
- Refresh token generation verified
- Access token regeneration tested

### Rate Limiting & Brute Force Protection
- Login attempt tracking implemented
- Temporary account lock verified
- Protection against repeated failed logins

### Swagger Security Audit
- Public endpoints verified
- Protected endpoints require authentication
- Admin endpoints require ADMIN role

### Audit Logging
- Authentication events logged
- Security actions recorded

## Security Checklist

- JWT Authentication ✅
- Role Based Authorization ✅
- Refresh Tokens ✅
- Password Reset Flow ✅
- Rate Limiting ✅
- Brute Force Protection ✅
- Audit Logging ✅
- BCrypt Password Hashing ✅
- Protected APIs ✅
- Swagger Security Validation ✅

## Build Status

BUILD SUCCESS

## Progress

Day 40.9 Completed ✅
