# Project Architecture and Rules (SPEC.md)

## Tech Stack
* **Java/Spring Boot:** Microservices, Consumers, REST APIs (Java 21, Spring Boot 3.x).
* **Security:** Spring Security with stateless JWT (Roles: VIEWER, OPERATOR, ADMIN).
* **Data Layer:** PostgreSQL (metadata, users) with Flyway, Elasticsearch (parsed logs).
* **Messaging:** Apache Kafka (Topics: `logs.raw`, `logs.parsed`, `logs.anomaly`).
* **AI/ML:** Python 3, Flask, scikit-learn (Isolation Forest).
* **Infrastructure:** Docker, Docker Compose (for agent testing), Kubernetes (Deployments, Services, NGINX Ingress, HPA).

## Architecture Flow
1. **Producers:** Dummy Spring Boot services emit JSON logs via Logback Kafka Appender to `logs.raw`.
2. **Ingestion Consumer:** Spring Boot service reads `logs.raw`, enriches data via Postgres, saves to Elasticsearch, and forwards to `logs.parsed`.
3. **AI Engine:** Spring Boot consumer reads `logs.parsed`, makes HTTP calls to a Python Flask ML service, and pushes anomalies to `logs.anomaly`.
4. **Alert Publisher:** Spring Boot service reads `logs.anomaly` and logs an alert (mocking email/Slack).
5. **API Gateway:** Spring Security REST API over Elasticsearch for log querying.

## Strict Autonomous Execution Rules
* **Testing Environment:** Use Docker Compose to spin up Kafka, Zookeeper/KRaft, Postgres, and Elasticsearch to verify your code compiles and runs.
* **No Frontend:** Do not build any React, HTML, or WebSocket components. 
* **Self-Correction:** Run `mvn clean package` or `python -m pytest` after each step. Read stack traces and fix errors autonomously. Do not check off tasks until tests pass.
* **Idempotency:** Ensure all database migrations (Flyway) and Kafka topic creations are handled automatically on startup.
