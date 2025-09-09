# 📸 Photo Sharing Platform

A distributed, microservices-based social platform that allows users to register, authenticate, follow others, upload images, and view personalized feeds. Designed for scalability and performance using Spring Boot, React, Kafka, Redis, MinIO, and Kubernetes.

---

## 📁 Project Structure

```

photo-sharing-platform/
├── auth-service/ # Handles user authentication and JWT issuance
├── feed-service/ # Manages timeline generation and feed distribution
├── follow_service/ # Manages follow/unfollow relations between users
├── Gateway-Service/ # API Gateway that routes requests and handles security
├── infrastructure/ # Contains docker-compose setups for Kafka, Elasticsearch, MinIO, etc.
├── post-service/ # for post images
├── user-service/ # for management user(signup-edit profile)


```

---

## 🔧 Services Overview

### `auth-service/`

- Implements login and JWT token generation
- Built with Spring Security + JWT

### `feed-service/`

- manages timeline generation and feed distribution

### `follow_service/`

- Handles follow/unfollow actions between users

### `Gateway-Service/`

- Built using Spring Cloud Gateway
- Verifies JWT tokens via `JwtAuthenticationFilter`
- Routes requests to `/api/**` endpoints

### `infrastructure/`

- `kafka/`: Docker Compose setup for Kafka & Zookeeper
- `elasticsearch/`: ELK for search indexing
- `MinIO/`: Object storage for user and post images
- Utility scripts: `start-all.sh`, `stop-all.sh`

---

## ▶️ How to Run the Project (Locally with Docker)

> **Prerequisites**:
>
> - Docker & Docker Compose installed
> - Java 17 + Maven installed (for building services)

1. **Start infrastructure dependencies** (Kafka, MinIO, Elasticsearch, etc.)
   ```bash
   cd infrastructure
   ./start-all.sh
   ```

```

```

2. **Build all services**

   ```bash
   mvn clean package -DskipTests
   ```

3. **Run services with Docker**

   ```bash
   docker build -t auth-service ./auth-service
   docker build -t feed-service ./feed-service/feed-backend
   docker build -t follow-service ./follow_service/follow-backend
   docker build -t gateway-service ./Gateway-Service

   docker run -p 8081:8080 auth-service
   docker run -p 8082:8080 feed-service
   docker run -p 8083:8080 follow-service
   docker run -p 8080:8080 gateway-service
   ```

> You can also deploy to Kubernetes .

---

## 📦 Main Technologies & Libraries

| Component     | Tech Stack                                 |
| ------------- | ------------------------------------------ |
| Backend       | Java 17, Spring Boot, Spring Cloud Gateway |
| Messaging     | Apache Kafka                               |
| Storage       | Redis, MongoDB, MinIO, mysql               |
| Search        | Elasticsearch                              |
| Image Hosting | MinIO with pre-signed URLs                 |
| API Gateway   | Spring Cloud Gateway + Filters             |
| DevOps/Infra  | Docker, Docker Compose, Kubernetes         |

---

## 🧠 Notes

- All microservices follow a clean hexagonal structure (controller → service → repository)
- Kafka topics used: `post.created`, `user.updated....`
- Feed service supports real-time and scheduled updates
- MinIO buckets: `user-images`, `post-images`

---
