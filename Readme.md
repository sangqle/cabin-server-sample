## **🚀 Project Overview**
This project is a **Java-based web framework** inspired by Cabin.jv framework, using **Hibernate for database management** and a clean architecture separating **Handlers, Services, and DAOs**.

### **🔹 Features**
- 🏗 **Separation of Concerns** (Handlers → Services → DAOs → Database).
- 🔥 **Hibernate ORM** for database operations.
- 🌍 **REST API** for user management (`CRUD` operations).
- 🔧 **Exception Handling** with `GlobalExceptionHandler`.
- 📦 **Singleton Pattern for SessionFactory** to optimize database connections.

---

## **📂 Project Structure**
```
📦 cabin-demo
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java
 ┃ ┃ ┃ ┣ 📂 com.cabin.demo
 ┃ ┃ ┃ ┃ ┣ 📂 dao                # Database Access (DAO Layer)
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 UserDAO.java
 ┃ ┃ ┃ ┃ ┣ 📂 service            # Business Logic (Service Layer)
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 UserService.java
 ┃ ┃ ┃ ┃ ┣ 📂 handler            # API Layer (Handlers)
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 UserHandler.java
 ┃ ┃ ┃ ┃ ┣ 📂 entity             # Hibernate Entities
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 User.java
 ┃ ┃ ┃ ┃ ┣ 📂 dto                # Data Transfer Objects (DTOs)
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 request
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜 UserRequestDTO.java
 ┃ ┃ ┃ ┃ ┃ ┣ 📂 response
 ┃ ┃ ┃ ┃ ┃ ┃ ┗ 📜 UserResponseDTO.java
 ┃ ┃ ┃ ┃ ┣ 📂 exception          # Exception Handling
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 GlobalExceptionHandler.java
 ┃ ┃ ┃ ┃ ┣ 📂 datasource         # Database Configuration
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 HibernateUtil.java
 ┃ ┃ ┃ ┃ ┣ 📂 server             # Server Setup
 ┃ ┃ ┃ ┃ ┃ ┗ 📜 HServer.java
 ┃ ┃ ┃ ┃ ┗ 📜 MainApp.java        # Application Entry Point
 ┃ ┃ ┣ 📂 resources              # Configuration Files
 ┃ ┃ ┃ ┗ 📜 hibernate.cfg.xml
 ┗ 📜 README.md                   # Documentation
```
✅ **Separation of Concerns** → `Handler (Controller)` → `Service` → `DAO` → `Database`.

---

## **⚡️ Getting Started**
### **1️⃣ Install Dependencies**
Ensure you have:
- Java 17+
- MySQL 8.0+
- Gradle

### **2️⃣ Configure Database**
Edit **`src/main/resources/hibernate.cfg.xml`**:
```xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/cabin_db?useSSL=false</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">yourpassword</property>
```
Then **run MySQL command**:
```sql
CREATE DATABASE cabin_db;
```

### **3️⃣ Build the Project**
```bash
./gradlew clean build
```

### **4️⃣ Run the Application**
```bash
java -jar build/libs/cabin-demo-1.0-SNAPSHOT.jar
```
✅ The server will start and listen for requests.

---

## **🌍 API Endpoints**
| HTTP Method | Endpoint          | Description |
|------------|------------------|-------------|
| `GET`      | `/users`          | Get all users |
| `GET`      | `/users/:id`      | Get user by ID |
| `POST`     | `/users`          | Create a new user |
| `PUT`      | `/users/:id`      | Update a user |
| `DELETE`   | `/users/:id`      | Delete a user |

---

## **🔹 Implementation Details**
### **1️⃣ API Layer (`UserHandler.java`)**
Handles HTTP requests and responses.
```java
public class UserHandler {
    private static final UserService userService = new UserService(HibernateUtil.getSessionFactory());

    public static void getAllUsers(Request req, Response resp) {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            resp.writeBody(ApiResponse.success(users));
        } catch (Exception e) {
            GlobalExceptionHandler.handleException(e, resp);
        } finally {
            resp.send();
        }
    }
}
```
✅ **Delegates logic to `UserService` (no database access here!).**

---

### **2️⃣ Business Layer (`UserService.java`)**
Contains business logic before calling `UserDAO`.
```java
public class UserService {
    private final UserDAO userDAO;

    public UserService(SessionFactory sessionFactory) {
        this.userDAO = new UserDAO(sessionFactory);
    }

    public boolean createUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO.getName() == null || userRequestDTO.getEmail() == null) {
            throw new IllegalArgumentException("Missing required fields");
        }
        User user = new User(userRequestDTO.getName(), userRequestDTO.getEmail());
        return userDAO.save(user);
    }
}
```
✅ **Handles validation and business rules before interacting with `DAO`.**

---

### **3️⃣ Persistence Layer (`UserDAO.java`)**
Handles database interactions using Hibernate.
```java
public class UserDAO extends BaseDAO<User> {
    public UserDAO(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    public User findByEmail(String email) {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM User WHERE email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
        }
    }
}
```
✅ **Only handles database queries, no business logic.**

---

### **4️⃣ Exception Handling (`GlobalExceptionHandler.java`)**
Centralized exception handling to ensure consistent error responses.
```java
public class GlobalExceptionHandler {
    public static void handleException(Exception e, Response resp) {
        int statusCode = 500;
        String message = "Internal Server Error";

        if (e instanceof IllegalArgumentException) {
            statusCode = 400;
            message = e.getMessage();
        } else if (e instanceof NoSuchElementException) {
            statusCode = 404;
            message = "Resource not found";
        }

        resp.setStatusCode(statusCode);
        resp.writeBody(ApiResponse.error(message));
        resp.send();
    }
}
```
✅ **Now, all API errors are handled in one place!**

---

## **⚡️ Extending the Project**
### **1️⃣ Add More Entities**
- Create a new entity in `com.cabin.demo.entity`.
- Create a corresponding `DAO`, `Service`, and `Handler`.

### **2️⃣ Add Authentication**
- Implement `AuthService` for JWT authentication.
- Create `AuthHandler` to handle login and token verification.

---

## **👨‍💻 Contributors**
- **[Your Name]** - Initial Development

---

## **📄 License**
This project is licensed under the **MIT License**.

---