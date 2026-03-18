![User Service Banner](https://raw.githubusercontent.com/Vishnu-Yadav0/Revshop-user-service/main/banner.png)

# 👤 RevShop — User Service

Handles everything auth and identity related across the RevShop platform — registration, login, JWT generation, and role-based access control for buyers, sellers, and shippers.

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-green?style=flat-square)](https://spring.io/projects/spring-security)
[![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=flat-square&logo=mysql)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=flat-square&logo=docker)](https://www.docker.com/)

---

## What it does

- Registers and manages **Buyer**, **Seller**, and **Shipper** accounts
- Issues **JWT tokens** on successful login for stateless authentication
- Enforces **Role-Based Access Control (RBAC)** across the platform
- Handles password reset flows and profile management
- Exposes user info endpoints consumed by other services via OpenFeign

## Tech Stack

| Layer | Tech |
|---|---|
| Framework | Spring Boot 3, Spring Security |
| Auth | JWT (JSON Web Tokens) |
| Database | MySQL |
| Service Discovery | Netflix Eureka Client |
| Config | Spring Cloud Config |
| Tracing | Zipkin, Micrometer |
| Container | Docker |

## Running Locally

```bash
./mvnw spring-boot:run
```

## Default Port

`8081`

---

## Part of RevShop Microservices

| Service | Repo |
|---|---|
| 🌐 Frontend | [Revshop-frontend](https://github.com/Vishnu-Yadav0/Revshop-frontend) |
| ⚙️ API Gateway | [Revshop-api-gateway](https://github.com/Vishnu-Yadav0/Revshop-api-gateway) |
| 🔍 Service Discovery | [Revshop-service-discovery](https://github.com/Vishnu-Yadav0/Revshop-service-discovery) |
| 🗂️ Config Server | [Revshop-config-server](https://github.com/Vishnu-Yadav0/Revshop-config-server) |
| 👤 User Service | [Revshop-user-service](https://github.com/Vishnu-Yadav0/Revshop-user-service) |
| 🛍️ Product Catalog | [Revshop-product-catalog-service](https://github.com/Vishnu-Yadav0/Revshop-product-catalog-service) |
| 📦 Inventory | [Revshop-inventory-service](https://github.com/Vishnu-Yadav0/Revshop-inventory-service) |
| 🛒 Order/Sales | [Revshop-order-sales-service](https://github.com/Vishnu-Yadav0/Revshop-order-sales-service) |
| 💳 Payment | [Revshop-payment-service](https://github.com/Vishnu-Yadav0/Revshop-payment-service) |
| 🔔 Notification | [Revshop-notification-service](https://github.com/Vishnu-Yadav0/Revshop-notification-service) |
| 🚚 Shipping | [Revshop-shipping-service](https://github.com/Vishnu-Yadav0/Revshop-shipping-service) |
| 🤖 AI Chat | [Revshop-ai-chat-service](https://github.com/Vishnu-Yadav0/Revshop-ai-chat-service) |

