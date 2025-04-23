<div align="center">

# 🎬 Movie Review & Reservation System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-yellow.svg)](https://github.com/jwtk/jjwt)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**A modern, secure, and scalable RESTful API for movie reviews and theater seat reservations**

[Features](#-key-features) •
[Architecture](#-architecture) •
[Installation](#-installation) •
[API Reference](#-api-reference) •
[Security](#-security) •
[Documentation](#-documentation) •
[Contributing](#-contributing)

</div>

## 📋 Overview

The **Movie Review & Reservation System** is an enterprise-grade Spring Boot application that provides a comprehensive solution for movie enthusiasts. It seamlessly integrates movie reviews with theater seat reservations, offering a complete platform for users to discover movies, share opinions, and book seats for upcoming shows.

Built with modern Java and Spring technologies, this API implements industry best practices including JWT authentication, role-based access control, rate limiting, and comprehensive API documentation.

## ✨ Key Features

<div align="center">
  <table>
    <tr>
      <td align="center" width="33%">
        <h3>🎬 Movie Platform</h3>
        <ul align="left">
          <li>Browse & search movies</li>
          <li>Rate & review movies</li>
          <li>Manage user reviews</li>
          <li>Movie recommendations</li>
        </ul>
      </td>
      <td align="center" width="33%">
        <h3>🎟️ Reservation System</h3>
        <ul align="left">
          <li>Theater management</li>
          <li>Showtime scheduling</li>
          <li>Seat selection & booking</li>
          <li>Reservation management</li>
        </ul>
      </td>
      <td align="center" width="33%">
        <h3>🔒 Enterprise Security</h3>
        <ul align="left">
          <li>JWT authentication</li>
          <li>Role-based access control</li>
          <li>Rate limiting protection</li>
          <li>Secure transactions</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

## 🏗️ Architecture

<div align="center">
  <table>
    <tr>
      <td align="center" width="20%">
        <img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/java/java.png" width="48" height="48" alt="Java" />
        <br>Java 17
      </td>
      <td align="center" width="20%">
        <img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/spring-boot/spring-boot.png" width="48" height="48" alt="Spring Boot" />
        <br>Spring Boot 3
      </td>
      <td align="center" width="20%">
        <img src="https://raw.githubusercontent.com/github/explore/80688e429a7d4ef2fca1e82350fe8e3517d3494d/topics/mysql/mysql.png" width="48" height="48" alt="MySQL" />
        <br>MySQL 8
      </td>
      <td align="center" width="20%">
        <img src="https://avatars.githubusercontent.com/u/317776?s=200&v=4" width="48" height="48" alt="Spring Security" />
        <br>Spring Security
      </td>
      <td align="center" width="20%">
        <img src="https://avatars.githubusercontent.com/u/30959722?s=200&v=4" width="48" height="48" alt="OpenAPI" />
        <br>Swagger OpenAPI
      </td>
    </tr>
  </table>
</div>

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.3, Spring Data JPA
- **Database**: MySQL 8.0
- **Security**: Spring Security, JJWT 0.12.6
- **API Documentation**: Swagger OpenAPI 3.0
- **Utilities**: Lombok, Resilience4j, MessageSource
- **Testing**: JUnit 5, Mockito

### Project Structure

```
├── src/                        # Source code
│   ├── main/
│   │   ├── java/in/lakshay/    # Java source files
│   │   │   ├── config/         # Configuration files (Security, Swagger, JWT)
│   │   │   ├── controller/     # REST Controllers with rate limiting
│   │   │   ├── entity/         # JPA Entities
│   │   │   ├── repo/           # Data access layer
│   │   │   ├── service/        # Business logic layer
│   │   │   ├── util/           # Constants and utility classes
│   │   │   └── exception/      # Global exception handling
│   │   └── resources/          # Application resources
│   │       ├── application.properties  # Application configuration
│   │       └── messages.properties     # i18n messages
│   └── test/                   # Test files
├── docs/                       # Documentation
│   ├── images/                 # Screenshots and images
│   └── postman/                # Postman collection for API testing
├── target/                     # Compiled output (generated)
├── pom.xml                     # Maven configuration
├── README.md                   # Project documentation
└── problem_statement.md        # Original problem statement
```

## 🔍 Installation

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

### Quick Start

1. **Clone the repository**

```bash
git clone https://github.com/lakshay1341/Movie-Review-System-API.git
cd Movie-Review-System-API
```

2. **Configure the database**

Create a MySQL database named `moviereviewdbupdated`

```sql
CREATE DATABASE moviereviewdbupdated;
```

3. **Configure application properties**

Update `src/main/resources/application.properties` with your database credentials and JWT configuration.

> **⚠️ Security Note**: Generate a secure JWT secret using `openssl rand -base64 64` and never commit it to version control.

4. **Build and run the application**

```bash
mvn clean install
mvn spring-boot:run
```

5. **Access the application**

- API: [http://localhost:8080](http://localhost:8080)
- Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Default Credentials

The system automatically creates two users on startup:

| Role  | Username | Password |
|-------|----------|----------|
| Admin | `admin`  | `password` |
| User  | `user`   | `password` |

## 🔥 API Reference

<div align="center">
  <img src="docs/images/postman-public-collection.png" alt="Public Postman Collection" width="700">
  <p><strong>Public Postman Collection Available</strong></p>
</div>

<div align="center">
  <table>
    <tr>
      <td><img src="docs/images/Movie and Auth controller.png" alt="Movie & Auth APIs" width="400"></td>
      <td><img src="docs/images/Review Management and Theaters.png" alt="Review & Theater APIs" width="400"></td>
    </tr>
    <tr>
      <td><img src="docs/images/Seats and Showtimes.png" alt="Seat & Showtime APIs" width="400"></td>
      <td><img src="docs/images/Reservations.png" alt="Reservation APIs" width="400"></td>
    </tr>
  </table>
</div>

### 🔑 Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/auth/register` | Register new user | Public |
| POST | `/api/v1/auth/login` | Get JWT token | Public |

### 🎬 Movies

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/movies` | Get all movies | Public |
| GET | `/api/v1/movies?search={query}` | Search movies | Public |
| GET | `/api/v1/movies/{id}` | Get movie by ID | Public |
| POST | `/api/v1/movies` | Add movie | Admin |
| PUT | `/api/v1/movies/{id}` | Update movie | Admin |
| DELETE | `/api/v1/movies/{id}` | Delete movie | Admin |

### ⭐ Reviews

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/reviews/movies/{movieId}` | Add review | User, Admin |
| GET | `/api/v1/reviews/my-reviews` | Get user reviews | User, Admin |
| PUT | `/api/v1/reviews/{reviewId}` | Update review | Owner, Admin |
| DELETE | `/api/v1/reviews/{reviewId}` | Delete review | Owner, Admin |

### 🏛️ Theaters

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/theaters` | Get all theaters | Public |
| GET | `/api/v1/theaters/{id}` | Get theater by ID | Public |
| GET | `/api/v1/theaters/search?location={location}` | Search theaters | Public |
| POST | `/api/v1/theaters` | Add theater | Admin |
| PUT | `/api/v1/theaters/{id}` | Update theater | Admin |
| DELETE | `/api/v1/theaters/{id}` | Delete theater | Admin |

### 📅 Showtimes

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/showtimes?date={date}` | Get showtimes by date | Public |
| GET | `/api/v1/showtimes/movies/{movieId}` | Get showtimes by movie | Public |
| GET | `/api/v1/showtimes/theaters/{theaterId}` | Get showtimes by theater | Public |
| GET | `/api/v1/showtimes/available` | Get available showtimes | Public |
| POST | `/api/v1/showtimes` | Add showtime | Admin |
| PUT | `/api/v1/showtimes/{id}` | Update showtime | Admin |
| DELETE | `/api/v1/showtimes/{id}` | Delete showtime | Admin |

### 💺 Seats

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| GET | `/api/v1/seats/showtimes/{showtimeId}` | Get all seats | Public |
| GET | `/api/v1/seats/showtimes/{showtimeId}/available` | Get available seats | Authenticated |

### 🎟️ Reservations

| Method | Endpoint | Description | Access |
|--------|----------|-------------|--------|
| POST | `/api/v1/reservations` | Create reservation | Authenticated |
| GET | `/api/v1/reservations/my-reservations` | Get user reservations | Authenticated |
| GET | `/api/v1/reservations/{id}` | Get reservation by ID | Owner, Admin |
| DELETE | `/api/v1/reservations/{id}` | Cancel reservation | Owner, Admin |
| GET | `/api/v1/reservations` | Get all reservations | Admin |
| GET | `/api/v1/reservations/reports/revenue` | Get revenue report | Admin |

## 🔒 Security

The API implements several security features:

- **JWT Authentication**: Secure token-based authentication with expiration
- **Password Encryption**: BCrypt password encoding (strength 12)
- **Role-Based Access Control**: Different permissions for users and admins
- **Rate Limiting**: Protection against API abuse (100 requests per minute)
- **Concurrent Access Control**: Pessimistic locking for seat reservations
- **Transactional Operations**: Ensuring data integrity

### Authentication Flow

1. User registers or logs in with credentials
2. Server validates credentials and returns a JWT token
3. Client includes the JWT token in the Authorization header for subsequent requests
4. Server validates the token and grants access based on user roles

### Security Headers

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 📖 Documentation

### API Documentation

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Spec**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Postman Collection

<div align="center">
  <img src="docs/images/postman-public-collection.png" alt="Public Postman Collection" width="700">
</div>

#### Public Collection

<div align="center">
  <h4>Movie Review & Reservation System API Collection</h4>
  <p>A comprehensive collection for testing all API endpoints with pre-configured requests and environments</p>
  <a href="https://www.postman.com/me3333-6732/movie-review-reservation-system/collection/27a274x/movie-review-reservation-system?action=share&source=copy-link&creator=40686830">
    <img src="https://img.shields.io/badge/Postman-Run%20in%20Postman-orange?logo=postman&logoColor=white&style=for-the-badge" alt="Run in Postman">
  </a>
</div>

##### Collection Features

- **Complete API Coverage**: Test all endpoints from authentication to reservations
- **Environment Variables**: Pre-configured environments for development and testing
- **Authentication Handling**: Automatic JWT token extraction and storage
- **Test Scripts**: Validation scripts to verify responses
- **Request Examples**: Sample payloads for all operations
- **Documentation**: Detailed descriptions for each request

##### Getting Started

1. Click the "Run in Postman" button above
2. Import the collection into your Postman workspace
3. Set your environment variables (base URL, credentials)
4. Start with the authentication requests to get your tokens
5. Explore the API endpoints organized by functional areas

#### Local Collection

A comprehensive Postman collection is also available in the `docs/postman` directory:

- Complete API endpoints
- Environment variables
- Test scripts
- Example requests and responses

### Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "operation.success.message",
  "data": {
    // Response payload
  }
}
```

## 💻 Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 👨‍💻 Contact

**Lakshay Chaudhary**
📧 Email: [lakshaychaudhary2003@gmail.com](mailto:lakshaychaudhary2003@gmail.com)
💼 GitHub: [lakshay1341](https://github.com/lakshay1341)

---

<div align="center">
  <p>⭐ Star this repository if you find it helpful!</p>
  <p>
    <sub>Built with ❤️ by Lakshay Chaudhary</sub>
  </p>
</div>
