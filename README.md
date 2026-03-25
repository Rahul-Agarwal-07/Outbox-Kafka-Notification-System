# 📬 Outbox Kafka Notification System

A **production-grade event-driven notification service** built using **Clean Architecture**, **Outbox Pattern**, **Apache Kafka**, and **Dead Letter Queue (DLQ)**.

This project demonstrates how to design **resilient, idempotent, and recoverable distributed systems** for handling asynchronous workflows like email notifications.

---

# 🚀 Features

* ✅ Event-driven architecture (no direct REST dependency)
* ✅ Outbox Pattern for reliable event publishing
* ✅ Kafka-based asynchronous communication
* ✅ Idempotent consumer design (no duplicate side-effects)
* ✅ Retry mechanism with backoff
* ✅ Dead Letter Queue (DLQ) for failure handling
* ✅ Validation layer to prevent poison messages
* ✅ Clean Architecture (Use Cases, Ports, Adapters)

---

# 🧠 Architecture Overview

```
User Service
   ↓
Outbox Table
   ↓
Kafka (user-events)
   ↓
Notification Service (this project)
   ↓
Email Sender
```

---

# 🔄 Event Processing Flow

```
Kafka → Listener → Validate → UseCase
                              ↓
                    Try Insert (idempotency)
                              ↓
                    Check Status (SENT/PENDING/FAILED)
                              ↓
                        Send Email
                              ↓
                  markAsSent / markAsFailed
```

---

# ⚙️ Kafka Flow (Retry + DLQ)

```
Consume Event
   ↓
Process
   ↓
Failure?
   ↓
Retry (3 times, 2s backoff)
   ↓
Still failing?
   ↓
→ DLQ (user-events-dlq)
```

---

# 🧱 Core Design Patterns Used

## 1. Outbox Pattern

Ensures reliable event publishing even if Kafka is temporarily unavailable.

## 2. Idempotent Consumer

Prevents duplicate processing using:

* Unique `eventId`
* `tryInsert` pattern

## 3. State Machine

```
NOT EXISTS → PENDING → SENT
                 ↓
              FAILED → RETRY
```

## 4. DLQ (Dead Letter Queue)

Handles unrecoverable failures without blocking the system.

---

# 📦 Project Structure

```
application/
  ├── notification/
  │     ├── usecase/
  │     ├── port/
  │
infrastructure/
  ├── kafka/
  ├── persistence/
  ├── email/
  
domain/
  ├── model/
  ├── exception/
```

---

# 🔐 Key Design Decisions

### ✔ Idempotency

Each event is processed exactly once logically using `eventId`.

### ✔ At-Least-Once Delivery

Kafka guarantees delivery, system ensures safe reprocessing.

### ✔ Failure Handling

* Retry for transient errors
* DLQ for permanent failures
* Validation prevents bad events

### ✔ Separation of Concerns

* UseCase → business logic
* Ports → abstraction
* Adapters → implementation

---

# 🛠️ Tech Stack

* Java
* Spring Boot
* Spring Kafka
* JPA / Hibernate
* Apache Kafka

---

# 📡 Example Events

```json
{
  "id": "uuid",
  "eventType": "USER_CREATED",
  "payload": {
    "email": "user@example.com"
  }
}
```

---

# 🧪 How to Run

1. Start Kafka locally
2. Run the application
3. Publish events to `user-events` topic
4. Monitor:

   * Logs
   * Database (notification table)
   * DLQ topic (`user-events-dlq`)

---

# 📊 Future Improvements

* Metrics & Monitoring (Prometheus/Grafana)
* DLQ Reprocessing API
* Email idempotency (deduplication)
* Circuit breaker for email service
* Distributed tracing

---

# 🏁 Summary

This project demonstrates how to build a **resilient notification system** capable of handling:

* Failures
* Retries
* Duplicate events
* Distributed consistency challenges

It reflects real-world backend architecture used in scalable systems.

---

# 👨‍💻 Author

Rahul Agarwal
