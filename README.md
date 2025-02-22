# Movie Review System API

## 📌 Project Overview
The **Movie Review System API** is a RESTful web service that allows users to manage movies and reviews. Built using **Spring Boot, JPA, and MySQL**, this API enables users to perform CRUD operations on movies and their corresponding reviews. The system includes **Spring Security** for basic authentication, **Swagger OpenAPI** for API documentation, and **Lombok** for reducing boilerplate code.

---

## 🚀 Tech Stack
- **Java 17**
- **Spring Boot 3.4.2**
- **Spring Data JPA**
- **MySQL**
- **Spring Security**
- **Swagger OpenAPI**
- **Lombok**

---

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

## 🐜 API Documentation (Swagger UI)
After running the application, you can access the **Swagger UI** at:
```
http://localhost:8080/swagger-ui/index.html
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
```

### 3⃣ Add Users to the Database
To authenticate with the API, you need to add users to the database. Passwords must be encoded using **BCrypt (strength 12)**. You can use the following SQL query to insert a user (replace `username` and `password` with your values):

```sql
INSERT INTO users (username, password) VALUES ('your_username', 'bcrypt_encoded_password');
```

### 4⃣ Build and Run the Application
Use Maven to clean, build, and start the application:
```bash
mvn clean install
mvn spring-boot:run
```

---

## 🔥 API Endpoints
### 🎬 Movie Endpoints
| Method | Endpoint          | Description                     | Authentication Required |
|--------|-------------------|---------------------------------|-------------------------|
| GET    | `/movies`         | Fetch all movies               | Yes                     |
| POST   | `/movies`         | Add a new movie                | Yes                     |

### ⭐ Review Endpoints
| Method | Endpoint                          | Description                     | Authentication Required |
|--------|-----------------------------------|---------------------------------|-------------------------|
| POST   | `/reviews/movies/{movieId}`       | Add a new review for a movie   | Yes                     |
| PUT    | `/reviews/{reviewId}`             | Update an existing review      | Yes                     |

---

## 🔐 Security
The API is secured with **Basic Authentication**. Ensure you provide valid credentials when making requests. Passwords for users must be encoded using **BCrypt (strength 12)** as configured in the `SecurityConfig.java` file.

---

## 🤝 Contributing
If you want to contribute to this project, follow these steps:
1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -m 'Add new feature'`).
4. Push to your branch (`git push origin feature-branch`).
5. Open a Pull Request.

---

## 🛠️ Future Enhancements
- Add **JWT-based authentication** for enhanced security.
- Implement **pagination** for large datasets.
- Introduce **rating system** with an aggregate score for movies.
- Add **unit and integration tests** for better code coverage.

---

## 📞 Contact
**Lakshay Chaudhary**  
📧 Email: [lakshaychaudhary2003@gmail.com](mailto:lakshaychaudhary2003@gmail.com)  
💎 GitHub: [lakshay1341](https://github.com/lakshay1341)

---

💡 *Star this repository if you found it helpful!* ⭐

