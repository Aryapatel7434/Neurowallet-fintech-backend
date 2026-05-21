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
GET /api/wallet/me
