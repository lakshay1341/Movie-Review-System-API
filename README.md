<div align="center">

# 🎬 Movie Review & Reservation System

[![Java](https://img.shields.io/badge/Java-17-4a4e69?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-4a4e69?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4a4e69?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-4a4e69?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://github.com/jwtk/jjwt)
[![License](https://img.shields.io/badge/License-MIT-4a4e69?style=for-the-badge&logo=opensourceinitiative&logoColor=white)](LICENSE)

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

[![Java](https://img.shields.io/badge/Java-17-4a4e69?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-4a4e69?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4a4e69?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Spring Security](https://img.shields.io/badge/Spring_Security-4a4e69?style=for-the-badge&logo=spring-security&logoColor=white)](https://spring.io/projects/spring-security)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3.0-4a4e69?style=for-the-badge&logo=swagger&logoColor=white)](https://swagger.io/)

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
  <a href="https://www.postman.com/me3333-6732/movie-review-reservation-system/collection/27a274x/movie-review-reservation-system?action=share&source=copy-link&creator=40686830">
    <img src="https://img.shields.io/badge/Postman-View%20Complete%20Collection-4a4e69?style=for-the-badge&logo=postman&logoColor=white" alt="View Complete Collection">
  </a>
</div>

<div align="center">
  <h3>API Categories</h3>
  <p>
    <a href="#-authentication"><img src="https://img.shields.io/badge/🔑_Authentication-2_Endpoints-4a4e69?style=flat-square" alt="Authentication: 2 Endpoints"></a>
    <a href="#-movies"><img src="https://img.shields.io/badge/🎬_Movies-6_Endpoints-4a4e69?style=flat-square" alt="Movies: 6 Endpoints"></a>
    <a href="#-reviews"><img src="https://img.shields.io/badge/⭐_Reviews-4_Endpoints-4a4e69?style=flat-square" alt="Reviews: 4 Endpoints"></a>
    <a href="#-theaters"><img src="https://img.shields.io/badge/🏛️_Theaters-6_Endpoints-4a4e69?style=flat-square" alt="Theaters: 6 Endpoints"></a>
  </p>
  <p>
    <a href="#-showtimes"><img src="https://img.shields.io/badge/📅_Showtimes-7_Endpoints-4a4e69?style=flat-square" alt="Showtimes: 7 Endpoints"></a>
    <a href="#-seats"><img src="https://img.shields.io/badge/💺_Seats-2_Endpoints-4a4e69?style=flat-square" alt="Seats: 2 Endpoints"></a>
    <a href="#-reservations"><img src="https://img.shields.io/badge/🎟️_Reservations-6_Endpoints-4a4e69?style=flat-square" alt="Reservations: 6 Endpoints"></a>
  </p>
</div>

### 🔑 Authentication

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/auth/register</code></td>
      <td>Register new user</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/auth/login</code></td>
      <td>Get JWT token</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
  </tbody>
</table>

### 🎬 Movies

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/movies</code></td>
      <td>Get all movies</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/movies?search={query}</code></td>
      <td>Search movies</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/movies/{id}</code></td>
      <td>Get movie by ID</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/movies</code></td>
      <td>Add movie</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>PUT</code></td>
      <td><code>/api/v1/movies/{id}</code></td>
      <td>Update movie</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>DELETE</code></td>
      <td><code>/api/v1/movies/{id}</code></td>
      <td>Delete movie</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
  </tbody>
</table>

### ⭐ Reviews

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/reviews/movies/{movieId}</code></td>
      <td>Add review</td>
      <td><img src="https://img.shields.io/badge/User-5a6268?style=flat-square" alt="User"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/reviews/my-reviews</code></td>
      <td>Get user reviews</td>
      <td><img src="https://img.shields.io/badge/User-5a6268?style=flat-square" alt="User"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>PUT</code></td>
      <td><code>/api/v1/reviews/{reviewId}</code></td>
      <td>Update review</td>
      <td><img src="https://img.shields.io/badge/Owner-495057?style=flat-square" alt="Owner"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>DELETE</code></td>
      <td><code>/api/v1/reviews/{reviewId}</code></td>
      <td>Delete review</td>
      <td><img src="https://img.shields.io/badge/Owner-495057?style=flat-square" alt="Owner"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
  </tbody>
</table>

### 🏛️ Theaters

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/theaters</code></td>
      <td>Get all theaters</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/theaters/{id}</code></td>
      <td>Get theater by ID</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/theaters/search?location={location}</code></td>
      <td>Search theaters</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/theaters</code></td>
      <td>Add theater</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>PUT</code></td>
      <td><code>/api/v1/theaters/{id}</code></td>
      <td>Update theater</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>DELETE</code></td>
      <td><code>/api/v1/theaters/{id}</code></td>
      <td>Delete theater</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
  </tbody>
</table>

### 📅 Showtimes

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/showtimes?date={date}</code></td>
      <td>Get showtimes by date</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/showtimes/movies/{movieId}</code></td>
      <td>Get showtimes by movie</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/showtimes/theaters/{theaterId}</code></td>
      <td>Get showtimes by theater</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/showtimes/available</code></td>
      <td>Get available showtimes</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/showtimes</code></td>
      <td>Add showtime</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>PUT</code></td>
      <td><code>/api/v1/showtimes/{id}</code></td>
      <td>Update showtime</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>DELETE</code></td>
      <td><code>/api/v1/showtimes/{id}</code></td>
      <td>Delete showtime</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
  </tbody>
</table>

### 💺 Seats

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/seats/showtimes/{showtimeId}</code></td>
      <td>Get all seats</td>
      <td><img src="https://img.shields.io/badge/Public-6c757d?style=flat-square" alt="Public"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/seats/showtimes/{showtimeId}/available</code></td>
      <td>Get available seats</td>
      <td><img src="https://img.shields.io/badge/Authenticated-4a4e69?style=flat-square" alt="Authenticated"></td>
    </tr>
  </tbody>
</table>

### 🎟️ Reservations

<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
      <th>Access</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><code>POST</code></td>
      <td><code>/api/v1/reservations</code></td>
      <td>Create reservation</td>
      <td><img src="https://img.shields.io/badge/Authenticated-4a4e69?style=flat-square" alt="Authenticated"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/reservations/my-reservations</code></td>
      <td>Get user reservations</td>
      <td><img src="https://img.shields.io/badge/Authenticated-4a4e69?style=flat-square" alt="Authenticated"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/reservations/{id}</code></td>
      <td>Get reservation by ID</td>
      <td><img src="https://img.shields.io/badge/Owner-495057?style=flat-square" alt="Owner"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>DELETE</code></td>
      <td><code>/api/v1/reservations/{id}</code></td>
      <td>Cancel reservation</td>
      <td><img src="https://img.shields.io/badge/Owner-495057?style=flat-square" alt="Owner"> <img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/reservations</code></td>
      <td>Get all reservations</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
    <tr>
      <td><code>GET</code></td>
      <td><code>/api/v1/reservations/reports/revenue</code></td>
      <td>Get revenue report</td>
      <td><img src="https://img.shields.io/badge/Admin-343a40?style=flat-square" alt="Admin"></td>
    </tr>
  </tbody>
</table>

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
  <h4>Movie Review & Reservation System API Collection</h4>
  <p>A comprehensive collection for testing all API endpoints with pre-configured requests and environments</p>
  <a href="https://me3333-6732.postman.co/workspace/4f28700f-ee89-485e-a928-767cd44234f9">
    <img src="https://img.shields.io/badge/Postman-Run%20in%20Postman-4a4e69?style=for-the-badge&logo=postman&logoColor=white" alt="Run in Postman">
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
