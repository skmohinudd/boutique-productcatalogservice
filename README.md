# boutique-productcatalogservice

Owns product catalog data, SKU validation, product details and catalog lifecycle APIs.

## Overview

- **Type:** Spring Boot service
- **Stack:** Java 21, Spring Boot, Maven, JPA, PostgreSQL, Flyway, Actuator, Docker
- **Port:** `8080`

## Flow

```text
Client / service → Controller → Business logic → Database / events / downstream services
```

## Main APIs

```text
Get /productId
```

## Database

```text
products
```

## Configuration

```text
DB_CONNECTION_TIMEOUT_MS
DB_MAX_LIFETIME_MS
DB_PASSWORD
DB_POOL_MAX_SIZE
DB_POOL_MIN_IDLE
DB_URL
DB_USERNAME
DB_VALIDATION_TIMEOUT_MS
```

## Run

```bash
./mvnw spring-boot:run
./mvnw clean verify
```

## Docker

```bash
docker build -t boutique-productcatalogservice:local .
```

## Health

```bash
curl http://localhost:8080/actuator/health
```

## CI/CD

This repository is built and deployed independently through its own GitHub Actions workflow.
