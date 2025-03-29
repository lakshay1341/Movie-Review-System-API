# Movie Review System API

## 📌 Project Overview
The **Movie Review System API** is a RESTful web service designed to manage movies and reviews. Built with **Spring Boot, JPA, and MySQL**, it enables CRUD operations on movies and reviews. The system features **JWT Authentication**, **Rate Limiting**, **Internationalization**, **Swagger OpenAPI** documentation, and uses **Lombok** to minimize boilerplate code.

---

## 🚀 Tech Stack
- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Data JPA**
- **MySQL**
- **Spring Security**
- **JJWT 0.12.6**
- **Swagger OpenAPI**
- **Lombok**
- **Resilience4j** (Rate Limiting)
- **MessageSource** (Internationalization)

## 🐂 Project Structure
```
MovieReviewSystemAPI
│── src/main/java/in/lakshay
│   ├── config/       # Configuration files (Security, Swagger, JWT)
│   ├── controller/   # REST Controllers with rate limiting
│   ├── entity/       # JPA Entities
│   ├── repo/         # Data access layer
│   ├── service/      # Business logic layer
│   ├── util/         # Constants and utility classes
│   └── exception/    # Global exception handling
│── src/main/resources
│   ├── application.properties  # Application configuration
│   └── messages.properties     # i18n messages
```

## 🔧 Setup & Installation

### 1⃣ Clone the Repository
```bash
git clone https://github.com/lakshay1341/MovieReviewSystemAPI.git
cd MovieReviewSystemAPI
```

### 2⃣ Configure Database and Security
Update **`src/main/resources/application.properties`**:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviereviewdbupdated
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true

# JWT Configuration
# Generate your own secure secret key using:
# openssl rand -base64 64
jwt.secret=<your-generated-secret>
jwt.expiration=3600000

# Rate Limiting Configuration
resilience4j.ratelimiter.instances.basic.limitForPeriod=100
resilience4j.ratelimiter.instances.basic.limitRefreshPeriod=1m
resilience4j.ratelimiter.instances.basic.timeoutDuration=1s
```

⚠️ **Important Security Note**: 
- Never use default or example secrets in production
- Always generate a new secure secret using `openssl rand -base64 64`
- Keep your generated secret private and never commit it to version control

### 3⃣ Initial Admin User
Default admin credentials (automatically created):
- **Username**: `admin`
- **Password**: `admin123` (BCrypt encoded, strength 12)

### 4⃣ Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

## 🔥 API Endpoints

### 🔑 Authentication
| Method | Endpoint     | Description         | Rate Limited | Roles |
|--------|-------------|---------------------|--------------|-------|
| POST   | `/api/v1/auth/register` | Register new user | Yes | None |
| POST   | `/api/v1/auth/login`    | Get JWT token    | Yes | None |

### 🎬 Movies
| Method | Endpoint          | Description        | Rate Limited | Roles |
|--------|------------------|--------------------|--------------|-------|
| GET    | `/api/v1/movies` | Get movies (paginated) | Yes | None (Public) |
| POST   | `/api/v1/movies` | Add movie         | Yes | ADMIN |

### ⭐ Reviews
| Method | Endpoint                    | Description      | Rate Limited | Roles |
|--------|----------------------------|------------------|--------------|-------|
| POST   | `/api/v1/reviews/movies/{movieId}` | Add review | Yes | USER, ADMIN |
| PUT    | `/api/v1/reviews/{reviewId}`      | Update review | Yes | Owner, ADMIN |
| GET    | `/api/v1/reviews/my-reviews`      | Get user reviews | Yes | USER, ADMIN |

## 🔐 Security Features
- **JWT Authentication** with configurable expiration
- **BCrypt Password Encoding** (strength 12)
- **Role-Based Access Control**
- **Rate Limiting** (100 requests per minute)
- **Global Exception Handling**

## 📋 Response Format
```json
{
  "success": boolean,
  "message": "Internationalized message key",
  "data": {
    // Response payload
  }
}
```

## 📚 API Documentation
Access Swagger UI: `http://localhost:8080/swagger-ui/index.html`
OpenAPI Spec: `http://localhost:8080/api-docs`

## 🌍 Internationalization
Messages are externalized in `messages.properties` supporting:
- User notifications
- Error messages
- Validation messages
- System messages

## 🛠️ Future Enhancements
- Implement Redis caching
- Add comprehensive test coverage
- Add OAuth2 support
- Implement WebSocket for real-time updates
- Add metrics monitoring (Prometheus/Grafana)

## 📞 Contact
**Lakshay Chaudhary**  
📧 Email: [lakshaychaudhary2003@gmail.com](mailto:lakshaychaudhary2003@gmail.com)  
💎 GitHub: [lakshay1341](https://github.com/lakshay1341)

---
⭐ Star this repository if you find it helpful!