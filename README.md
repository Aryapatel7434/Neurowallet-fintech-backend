# NeuroWallet — AI-Powered FinTech Backend

**A Java 17 and Spring Boot backend for a digital wallet platform, with authentication, wallet and transaction workflows, financial intelligence services, and supporting infrastructure.**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.12-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?logo=mysql)](https://www.mysql.com/)
[![Kafka](https://img.shields.io/badge/Messaging-Apache%20Kafka-231F20?logo=apachekafka)](https://kafka.apache.org/)
[![Redis](https://img.shields.io/badge/Cache-Redis-DC382D?logo=redis)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Container-Docker-2496ED?logo=docker)](https://www.docker.com/)

## Overview

NeuroWallet is an AI-powered FinTech application project that models common digital-wallet and personal-finance workflows. This repository contains the Spring Boot REST API and backend services used by the NeuroWallet frontend.

The codebase includes authentication and account workflows, wallet and transaction operations, scheduled transfers, budgeting and goals, notifications, audit capabilities, financial analytics, and AI-oriented services using Spring AI with Google Gemini.

> **Project scope:** NeuroWallet is an educational and portfolio engineering project. It is not a licensed bank, payment institution, or production payment processor. Do not use it to hold or transfer real customer funds.

## Backend capabilities

### Identity, authentication, and security

- User registration and login endpoints
- Spring Security-based authentication and authorization
- JWT access-token and refresh-token workflows
- BCrypt password encoding
- OTP generation and verification endpoints
- Forgot-password and reset-password workflows
- Role-aware access control
- Login-attempt and request-security components
- Audit-related services and endpoints

### Wallet and transactions

- Wallet creation and wallet retrieval
- Wallet balance and wallet service operations
- Peer-to-peer transfer workflows
- Transaction history and transaction analytics
- Scheduled transaction creation and processing
- Failed-transaction audit and tracking services
- Wallet caching service

### Financial management

- Budget and budget-spending services
- Savings-goal services and goal-advisor functionality
- Financial health score service
- Spending-pattern and category-analysis services
- Financial intelligence and smart-insight services
- Dashboard and cash-flow related APIs

### AI capabilities

The source includes AI controllers and services for:

- AI assistant and AI conversation workflows
- AI prompt construction
- Budget-advisor functionality
- Goal-advisor functionality
- Financial intelligence and smart insights
- Fraud detection and AI fraud analysis

The AI integration uses Spring AI's Google GenAI starter. Actual availability depends on valid server-side provider configuration, model access, and service quotas.

### Platform and operations

- Apache Kafka integration
- Redis integration and caching
- Outbox-event services and publisher/claim components
- Scheduled background processing
- Spring Boot Actuator
- Swagger/OpenAPI documentation
- Dockerfile and Docker Compose configuration
- JUnit / Spring Boot test dependencies

## Architecture

The backend uses a layered Spring application structure:

```text
Frontend / API Client
        |
        v
REST Controllers
        |
        v
Spring Security / Filters
        |
        v
Application Services
        |
        +--------------------+
        |                    |
        v                    v
Repositories           External Services
        |                |    |    |
        v                v    v    v
     MySQL              Redis Kafka Gemini
```

The source tree is organized into packages including:

| Package | Responsibility |
|---|---|
| `controller` | REST endpoints for application features |
| `service` | Business logic and orchestration |
| `repository` | Persistence access |
| `model` | Domain and persistence models |
| `dto` | API request and response objects |
| `security` | Authentication and authorization components |
| `config` | Application and integration configuration |
| `scheduler` | Scheduled/background processing |
| `filter` | HTTP request filtering and tracking |
| `exception` | Error handling |

The exact classes and package contents may evolve as the project develops.

## Technology stack

| Area | Technologies found in the project |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.12 |
| Web | Spring Boot Web / REST |
| Security | Spring Security, JJWT |
| Persistence | Spring Data JPA, Hibernate |
| Database driver | MySQL Connector/J |
| Validation | Spring Boot Validation |
| Cache | Spring Cache, Spring Data Redis |
| Messaging | Spring for Apache Kafka |
| AI | Spring AI Google GenAI starter |
| Email | Spring Boot Mail |
| Monitoring | Spring Boot Actuator |
| API documentation | Springdoc OpenAPI |
| Build | Maven / Maven Wrapper |
| Containers | Docker, Docker Compose |
| Tests | Spring Boot Test, JUnit ecosystem, Mockito |

## Repository layout

```text
.
├── docs/
│   ├── API_DOCUMENTATION.md
│   ├── ARCHITECTURE.md
│   ├── DATABASE_DESIGN.md
│   ├── DEPLOYMENT_GUIDE.md
│   ├── FUTURE_ROADMAP.md
│   ├── PROJECT_STRUCTURE.md
│   └── SECURITY_GUIDELINES.md
├── src/
│   ├── main/
│   │   ├── java/com/smartwallet/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   └── ...
│   │   └── resources/
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Getting started

### Prerequisites

- JDK 17
- Git
- MySQL 8.x
- Maven (or use the included Maven Wrapper)
- Docker Desktop and Docker Compose, if using containers
- Redis and Kafka when running the features configured to use them
- Google GenAI credentials when using Gemini-backed features

### Clone

```bash
git clone https://github.com/Aryapatel7434/Neurowallet-fintech-backend.git
cd Neurowallet-fintech-backend
```

### Configure the application

Configure the database, JWT, mail, Redis, Kafka, and AI provider settings through the application's supported configuration properties and environment variables.

Typical configuration categories include:

| Configuration | Purpose |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection URL |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| JWT signing configuration | Secure access/refresh token signing |
| Mail configuration | OTP and password-reset email delivery |
| `SPRING_DATA_REDIS_*` | Redis connection configuration |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address |
| Google GenAI configuration | Server-side Gemini API access |

Use the exact property names expected by the current application configuration. Do not assume every setting is enabled in every profile.

**Never commit real passwords, tokens, JWT secrets, SMTP credentials, database credentials, or Gemini API keys.** Keep them in local environment configuration or your deployment provider's secret manager. Rotate any credential that has previously been committed to a public repository.

### Run with Maven

Windows PowerShell:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

macOS / Linux:

```bash
chmod +x mvnw
./mvnw clean package
./mvnw spring-boot:run
```

The project documentation and local configuration use port `8080` as the default application port.

### Run tests

Windows:

```powershell
.\mvnw.cmd test
```

macOS / Linux:

```bash
./mvnw test
```

### Run with Docker Compose

Review `docker-compose.yml` and provide credentials securely before starting containers. Do not publish database ports or use development credentials in a public deployment.

```bash
docker compose build
docker compose up -d
docker compose ps
```

View application logs:

```bash
docker compose logs -f neurowallet-app
```

Stop the stack:

```bash
docker compose down
```

To remove persisted volumes, Docker Compose requires an additional `-v` option. This deletes stored container data; do not use it unless you intentionally want to erase the local database and Kafka data.

## API documentation

The repository includes API documentation in `docs/API_DOCUMENTATION.md` and Swagger/OpenAPI configuration.

When the application is running and the documentation endpoint is enabled:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

The documented API surface includes authentication, user, wallet, transaction, scheduled-transaction, and OTP workflows. Consult the source controllers and current Swagger output for the authoritative endpoint contract.

## Tests and quality

The Maven project includes Spring Boot test dependencies and Mockito. The repository documentation describes unit, MVC/API, integration, and performance-testing approaches. Run the current test suite with `mvnw test`; do not interpret the presence of a testing dependency as proof that every feature has complete automated coverage.

## Operational and security notes

Before exposing an instance to the public internet:

- Supply all secrets through deployment environment variables or a secrets manager.
- Use HTTPS and restrict CORS to the deployed frontend origin.
- Review JWT expiry, signing-key strength, refresh-token lifecycle, and password-reset behavior.
- Restrict Swagger UI and Actuator endpoints in production.
- Use a production database user with least privilege; do not run the application as MySQL root.
- Review database migrations and schema changes before production use.
- Configure backups and verify restore procedures.
- Avoid logging credentials, tokens, OTPs, or sensitive financial data.
- Review rate limiting, authorization checks, and transaction consistency.
- Configure Kafka and Redis with appropriate network access and credentials.
- Use synthetic/demo data only until a formal security, legal, and compliance review is complete.

## Documentation

Additional project documentation is available in [`docs/`](docs/):

- [Architecture](docs/ARCHITECTURE.md)
- [API Documentation](docs/API_DOCUMENTATION.md)
- [Database Design](docs/DATABASE_DESIGN.md)
- [Deployment Guide](docs/DEPLOYMENT_GUIDE.md)
- [Project Structure](docs/PROJECT_STRUCTURE.md)
- [Security Guidelines](docs/SECURITY_GUIDELINES.md)
- [Future Roadmap](docs/FUTURE_ROADMAP.md)

## Related repository

**Frontend:** [NeuroWallet FinTech Frontend](https://github.com/Aryapatel7434/Neurowallet-fintech-frontend)

## Developer

**Arya Patel**  
Java | Spring Boot | Backend Engineering | AI FinTech

## License

This repository is intended for educational, portfolio, and professional learning purposes. No separate license file was identified in the supplied project archive. Unless a license is added, all rights are reserved by the author.
