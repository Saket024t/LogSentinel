# Execution Checklist (TASKS.md)

## Phase 1: Infrastructure & Base Configurations
- [x] 1.1 Create `docker-compose.yml` containing PostgreSQL, Elasticsearch, and Kafka (KRaft mode).
- [x] 1.2 Start the containers and verify they are running via a local script.
- [x] 1.3 Create a multi-module Maven project or separate directories for backend microservices.

## Phase 2: Dummy Producers
- [x] 2.1 Create two dummy Spring Boot services (OrderService, AuthService) with a few REST endpoints.
- [x] 2.2 Add `logback-spring.xml` to both, configuring the Logback Kafka Appender to send JSON logs to `logs.raw`.
- [x] 2.3 Verify services compile and run.

## Phase 3: Ingestion Consumer & Databases
- [x] 3.1 Create `IngestionConsumer` Spring Boot service. Add Flyway scripts for a `services_metadata` table in Postgres.
- [x] 3.2 Configure a Kafka Consumer to read from `logs.raw`.
- [x] 3.3 Parse incoming logs, save full-text data to Elasticsearch, and publish the enriched log to `logs.parsed`.
- [x] 3.4 Write an integration test using Testcontainers. Verify it passes.

## Phase 4: AI Anomaly Engine (Python + Spring Boot)
- [x] 4.1 Create a Python Flask service `/ai-engine` with a scikit-learn Isolation Forest model exposing a `/predict` endpoint. Add a `requirements.txt`.
- [x] 4.2 Create an `AnomalyConsumer` Spring Boot service. Read from `logs.parsed`, call the Flask `/predict` endpoint, and if an anomaly is detected, publish to `logs.anomaly`.
- [x] 4.3 Verify both services start and communicate successfully.

## Phase 5: Security & API Gateway
- [x] 5.1 Create an `ApiGateway` Spring Boot service.
- [x] 5.2 Implement Spring Security with JWT FilterChain. Define roles: VIEWER, OPERATOR, ADMIN.
- [x] 5.3 Build a `@PreAuthorize` secured REST controller that queries Elasticsearch for logs.
- [x] 5.4 Write unit tests for the JWT authentication flow.

## Phase 6: Alert Publisher
- [x] 6.1 Create `AlertPublisher` Spring Boot service.
- [x] 6.2 Configure a Kafka Consumer to read from `logs.anomaly` and output a formatted alert to standard out (simulating an email/Slack webhook).

## Phase 7: Kubernetes & Load Balancing
- [x] 7.1 Write `Dockerfile`s for all Spring Boot services and the Python Flask service.
- [x] 7.2 Create Kubernetes `Deployment` and `Service` YAML manifests for all components.
- [x] 7.3 Create an `Ingress` YAML manifest (NGINX) acting as the load balancer to route external traffic to the `ApiGateway`.
- [x] 7.4 Create a `HorizontalPodAutoscaler` (HPA) manifest for the `IngestionConsumer` configured to scale based on CPU or custom Kafka metrics.
