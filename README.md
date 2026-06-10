# 🚀 NeuroWallet AI Fintech Platform

Enterprise-grade FinTech backend platform built using Spring Boot, Spring Security, JWT, MySQL, Docker, Kafka, and modern backend engineering practices.

## Overview

NeuroWallet is a secure digital wallet and transaction processing platform designed to simulate real-world fintech systems.

The platform demonstrates:

* Secure Authentication & Authorization
* Digital Wallet Management
* Peer-to-Peer Transactions
* OTP Verification
* Password Reset Flow
* Refresh Token Management
* Event-Driven Architecture with Kafka
* Dockerized Deployment
* Enterprise Logging & Monitoring
* Integration & Performance Testing

---

## Key Features

### Authentication & Security

* JWT Authentication
* Refresh Tokens
* BCrypt Password Hashing
* Role-Based Authorization
* Method-Level Security
* OTP Verification
* Password Reset Flow
* Rate Limiting
* Brute Force Protection

### Wallet Management

* Wallet Creation
* Balance Management
* Add Money
* Withdraw Money
* Wallet Analytics

### Transaction Engine

* Peer-to-Peer Transfers
* Scheduled Transactions
* Transaction History
* Transaction Status Tracking
* Failed Transaction Monitoring
* Admin Analytics

### Performance & Scalability

* Database Indexing
* Redis-style Caching Concepts
* Kafka Event Streaming
* Docker Compose Deployment
* Request Tracking
* Enterprise Logging

---

## System Architecture

Client

↓

REST APIs

↓

Spring Security

↓

Controller Layer

↓

Service Layer

↓

Repository Layer

↓

MySQL Database

↓

Kafka Event Streaming

---

## Technology Stack

### Backend

* Java 17
* Spring Boot 3
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* MySQL 8

### Messaging

* Apache Kafka

### Containerization

* Docker
* Docker Compose

### Testing

* JUnit 5
* Mockito
* MockMvc
* JMeter

### Documentation

* Swagger OpenAPI

---

## Security Highlights

* JWT Authentication
* Refresh Tokens
* BCrypt Encryption
* Role-Based Access Control
* Password Reset Tokens
* OTP Verification
* Rate Limiting
* Brute Force Protection
* Security Auditing

---

## Testing Coverage

### Unit Testing

* JUnit 5
* Mockito

### Integration Testing

* MockMvc
* Authentication Flow Testing
* Wallet API Testing
* Transaction API Testing

### Performance Testing

* Apache JMeter
* Concurrent Login Testing
* API Load Validation

---

## Docker Deployment

Build:

```bash
mvn clean package
docker compose build
```

Run:

```bash
docker compose up -d
```

Stop:

```bash
docker compose down
```

---

## Documentation

Detailed documentation is available inside the `/docs` directory:

* Architecture Documentation
* Database Design
* API Documentation
* Security Review
* Development Journal

---

## Current Status

### Backend Development

Completed ✅

* Authentication
* Authorization
* Wallet System
* Transaction Engine
* Kafka Integration
* Docker Deployment
* Security Hardening
* Testing & Documentation

### Next Phase

Frontend Development (React)

* Dashboard
* Wallet UI
* Transaction UI
* Analytics
* Deployment

---

## Developer

Ary Patel

Backend Developer | Java | Spring Boot | Security | FinTech Engineering

---

## License

This project is developed for educational, portfolio, and professional learning purposes.
