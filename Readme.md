Here is a rewritten `Readme.md` tailored to your project structure and implementation:

---

## 🚀 Project Overview

This project is a **Java-based web application framework** inspired by Cabin.jv, featuring a clean architecture with clear separation between **Handlers (API layer), Services (Business logic), and DAOs (Persistence)**. It uses **Hibernate ORM** for database management and supports **JWT-based authentication**.

---

## 📦 Project Structure

```
cabin-demo/
 ┣ src/
 ┃ ┣ main/
 ┃ ┃ ┣ java/
 ┃ ┃ ┃ ┣ com.cabin.demo/
 ┃ ┃ ┃ ┃ ┣ dao/         # Data Access Objects (DAO Layer)
 ┃ ┃ ┃ ┃ ┣ services/    # Business Logic (Service Layer)
 ┃ ┃ ┃ ┃ ┣ handler/     # API Layer (Handlers)
 ┃ ┃ ┃ ┃ ┣ entity/      # Hibernate Entities
 ┃ ┃ ┃ ┃ ┣ dto/         # Data Transfer Objects (DTOs)
 ┃ ┃ ┃ ┃ ┣ exception/   # Exception Handling
 ┃ ┃ ┃ ┃ ┣ datasource/  # Database Configuration
 ┃ ┃ ┃ ┃ ┣ util/        # Utilities (e.g., JWT, ID obfuscation)
 ┃ ┃ ┃ ┃ ┣ helper/      # Helpers (e.g., S3/R2 integration)
 ┃ ┃ ┃ ┃ ┣ server/      # Server Setup
 ┃ ┃ ┃ ┃ ┗ MainApp.java # Application Entry Point
 ┃ ┃ ┣ resources/
 ┃ ┃ ┃ ┣ application.properties # App configuration
 ┃ ┃ ┃ ┗ hibernate.cfg.xml     # Hibernate config
 ┗ README.md
```

---

## 🔹 Features

- **Separation of Concerns:** Handler → Service → DAO → Database
- **Hibernate ORM:** Entity mapping and database operations
- **REST API:** User management (CRUD), authentication, photo management
- **JWT Authentication:** Secure, stateless login
- **Exception Handling:** Centralized error responses
- **Cloud Storage Integration:** Upload photos to S3-compatible storage (Cloudflare R2, MinIO)
- **Singleton SessionFactory:** Efficient Hibernate session management

---

## ⚡ Getting Started

### 1️⃣ Prerequisites

- Java 17+
- PostgreSQL (or MySQL, with config changes)
- Gradle

### 2️⃣ Configure Database

Edit `src/main/resources/application.properties` and `hibernate.cfg.xml` to match your database settings.

Example for PostgreSQL in `application.properties`:
```
DB_URL=jdbc:postgresql://localhost:5432/photo_db
DB_USER=youruser
DB_PASSWORD=yourpassword
```

Create the database:
```sql
CREATE DATABASE photo_db;
```

### 3️⃣ Build the Project

```bash
./gradlew clean build
```

### 4️⃣ Run the Application

```bash
java -jar build/libs/cabin-demo-1.0-SNAPSHOT.jar
```

The server will start and listen for requests (default port: 8888).

---

## 🌍 API Endpoints

| HTTP Method | Endpoint           | Description                |
|-------------|--------------------|----------------------------|
| GET         | `/users`           | Get all users              |
| GET         | `/users/:id`       | Get user by ID             |
| POST        | `/users`           | Register a new user        |
| POST        | `/login`           | User login (returns JWT)   |
| GET         | `/photos/:userId`  | Get photos by user         |
| POST        | `/photos/upload`   | Upload a photo             |

---

## 🔒 Authentication

- **Register:** `POST /users` with name, email, password
- **Login:** `POST /login` with email, password → returns JWT
- **Protected Endpoints:** Pass JWT in `Authorization: Bearer <token>` header

---

## 🛠 Implementation Details

- **Handlers:** Parse HTTP requests, call services, return responses
- **Services:** Business logic, validation, call DAOs
- **DAOs:** Hibernate-based database access
- **Entities:** JPA-annotated classes for ORM
- **JWT:** Generated on login, not stored in DB (stateless)
- **Photo Upload:** Uses S3-compatible API (Cloudflare R2, MinIO)

---

## 🧩 Extending the Project

- Add new entities: Create Entity, DAO, Service, Handler
- Add new endpoints: Implement in Handler, wire to Service
- Add new storage: Implement helper for your storage provider

---

## 👨‍💻 Contributors

- [Your Name] - Initial Development

---

## 📄 License

This project is licensed under the **MIT License**.

---