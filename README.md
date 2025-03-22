# Movie Review System API

## 📌 Project Overview
The **Movie Review System API** is a RESTful web service designed to manage movies and reviews. Built with **Spring Boot, JPA, and MySQL**, it enables CRUD operations on movies and reviews. The system now uses **JWT Authentication** for secure access, **Swagger OpenAPI** for API documentation, and **Lombok** to minimize boilerplate code.

---

## 🚀 Tech Stack
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **MySQL**
- **Spring Security**
- **JJWT (JSON Web Tokens)**
- **Swagger OpenAPI**
- **Lombok**


## 🐂 Project Structure
```
MovieReviewSystemAPI
│── src/main/java/in/lakshay
│   ├── config/       # Configuration files (Security, Swagger, etc.)
│   ├── controller/   # Controllers handling API requests
│   ├── entity/       # JPA Entities (Movie, Review, User)
│   ├── repo/         # Data access layer (Repositories)
│   ├── service/      # Business logic layer (Services)
│── src/main/resources
│   └── application.properties  # Application configuration
│── pom.xml  # Maven dependencies
│── README.md  # Project documentation
│── HELP.md  # Getting started guide
│── LICENSE  # MIT License
│── mvnw  # Maven wrapper script
│── mvnw.cmd  # Maven wrapper script for Windows
```

---

## 🔧 Setup & Installation
### 1⃣ Clone the Repository
```bash
git clone https://github.com/lakshay1341/MovieReviewSystemAPI.git
cd MovieReviewSystemAPI
```

### 2⃣ Configure Database
Update **`src/main/resources/application.properties`** with your MySQL database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/moviereviewdb
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=your_very_secure_secret_key_here
jwt.expiration=3600000
```

### 3⃣ Initial Admin User
An initial admin user is created automatically via `data.sql`:
- **Username**: `admin`
- **Password**: `admin123` (BCrypt encoded)
Use these credentials to log in and perform administrative actions.

### 4⃣ Build and Run the Application
Use Maven to build and start the application:
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🐜 API Documentation

### Swagger UI
After running the application, access the **Swagger UI** at:
```
http://localhost:8080/swagger-ui/index.html
```

#### Swagger Screenshots
*Swagger UI Overview*
![Swagger UI Overview](https://github.com/lakshay1341/Movie-Review-System-API/blob/main/updated_jwt_impl_images/swagger_overview.png)

*Swagger Endpoint Post*
![Swagger Endpoint Example](https://github.com/lakshay1341/Movie-Review-System-API/blob/main/updated_jwt_impl_images/swagger_post_movie.png)

---

## 🔥 API Endpoints

### 🔑 Authentication Endpoints
| Method | Endpoint     | Description               | Authentication Required | Roles         |
|--------|--------------|---------------------------|-------------------------|---------------|
| POST   | `/register`  | Register a new user       | No                      | None          |
| POST   | `/login`     | Login and get JWT token   | No                      | None          |

### 🎬 Movie Endpoints
| Method | Endpoint          | Description                     | Authentication Required | Roles         |
|--------|-------------------|---------------------------------|-------------------------|---------------|
| GET    | `/movies`         | Fetch all movies (paginated)   | Yes                     | Any           |
| POST   | `/movies`         | Add a new movie                | Yes                     | ROLE_ADMIN    |

*Note*: GET `/movies` supports pagination and search. Example: `/movies?page=0&size=10&sort=releaseYear,desc&search=action`.

### ⭐ Review Endpoints
| Method | Endpoint                          | Description                     | Authentication Required | Roles                     |
|--------|-----------------------------------|---------------------------------|-------------------------|---------------------------|
| POST   | `/reviews/movies/{movieId}`       | Add a review for a movie       | Yes                     | ROLE_USER, ROLE_ADMIN     |
| PUT    | `/reviews/{reviewId}`             | Update an existing review      | Yes                     | Review Owner, ROLE_ADMIN  |
| GET    | `/reviews/my-reviews`             | Fetch user's own reviews       | Yes                     | ROLE_USER, ROLE_ADMIN     |

---

## 🔐 Security
The API is secured with **JWT Authentication**. To access protected endpoints:
1. Register via `/register` or use the initial admin credentials.
2. Login via `/login` to obtain a JWT token.
3. Include the token in the `Authorization` header as `Bearer <token>` for subsequent requests.

Passwords are encoded using **BCrypt (strength 12)** as configured in `SecurityConfig.java`.

---

## 📋 Response Format
All API responses follow this structure:
```json
{
  "success": true/false,
  "message": "description or error code",
  "data": {response data}
}
```

---

## 🛠️ Testing with Postman
You can test the API using Postman. Import the collection and environment files (if provided) or manually configure requests with the JWT token.

#### Postman Screenshots
*Postman Login Request*
![Postman Login Request](https://github.com/lakshay1341/Movie-Review-System-API/blob/main/updated_jwt_impl_images/postman_login.png)

*Postman Movies Request*
![Postman Movies Request](https://github.com/lakshay1341/Movie-Review-System-API/blob/main/updated_jwt_impl_images/postman_get_movies.png)

---

## 🤝 Contributing
To contribute:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to your branch (`git push origin feature-branch`).
5. Open a Pull Request.

---

## 🛠️ Future Enhancements
- Introduce a **rating system** with aggregate scores for movies.
- Add **unit and integration tests** for better code coverage.
- Implement **caching** to improve performance.

---

## 📞 Contact
**Lakshay Chaudhary**  
📧 Email: [lakshaychaudhary2003@gmail.com](mailto:lakshaychaudhary2003@gmail.com)  
💎 GitHub: [lakshay1341](https://github.com/lakshay1341)

---

💡 *Star this repository if you found it helpful!* ⭐
