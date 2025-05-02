<div align="center">

# 🎬 CineTicket - Movie Review & Reservation System

[![Java](https://img.shields.io/badge/Java-17-4a4e69?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-4a4e69?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4a4e69?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-0.12.6-4a4e69?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://github.com/jwtk/jjwt)
[![Stripe](https://img.shields.io/badge/Stripe-22.0.0-4a4e69?style=for-the-badge&logo=stripe&logoColor=white)](https://stripe.com/)
[![License](https://img.shields.io/badge/License-MIT-4a4e69?style=for-the-badge&logo=opensourceinitiative&logoColor=white)](LICENSE)

**A modern, secure, and scalable RESTful API for movie reviews and theater seat reservations**

🌐 **[Live Demo: https://cineticket.onrender.com](https://cineticket.onrender.com)**

[Overview](#-overview) •
[Features](#-key-features) •
[Demo](#️-frontend-demo) •
[Architecture](#-architecture) •
[Installation](#-installation) •
[API Reference](#-api-reference) •
[Security](#-security) •
[Documentation](#-documentation) •
[Contributing](#-contributing) •
[Contact](#-contact)

</div>

## 📋 Overview

**CineTicket** is an enterprise-grade Spring Boot application that provides a comprehensive solution for movie enthusiasts. It seamlessly integrates movie reviews with theater seat reservations, offering a complete platform for users to discover movies, share opinions, and book seats for upcoming shows.

Built with modern Java and Spring technologies, this API implements industry best practices including JWT authentication, role-based access control, Stripe payment integration, PDF receipt generation, email notifications, rate limiting, and comprehensive API documentation.

The system is deployed and fully functional at [https://cineticket.onrender.com](https://cineticket.onrender.com).

## 🖥️ Frontend Demo

<div align="center">
  <table>
    <tr>
      <td align="center" width="50%">
        <h4>👨‍💼 Admin Dashboard</h4>
        <p><small>Manage movies, theaters, showtimes, and view reservation data</small></p>
        <img src="docs/gifs/admin.gif" alt="CineTicket Admin Dashboard" width="400">
      </td>
      <td align="center" width="50%">
        <h4>🧑‍💻 User Experience</h4>
        <p><small>Browse movies, read reviews, select seats, and complete the reservation process</small></p>
        <img src="docs/gifs/user.gif" alt="CineTicket User Experience" width="400">
      </td>
    </tr>
  </table>
</div>

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
          <li>Stripe payment integration</li>
          <li>PDF receipt generation</li>
          <li>Email notifications</li>
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
    <tr>
      <td align="center" colspan="3">
        <h3>🌟 Advanced Features</h3>
        <table>
          <tr>
            <td width="33%">
              <h4>💳 Payment Processing</h4>
              <ul align="left">
                <li>Stripe integration</li>
                <li>Checkout sessions</li>
                <li>Webhook handling</li>
                <li>Receipt generation</li>
              </ul>
            </td>
            <td width="33%">
              <h4>📧 Email Notifications</h4>
              <ul align="left">
                <li>Confirmation emails</li>
                <li>PDF attachments</li>
                <li>Branded templates</li>
              </ul>
            </td>
            <td width="33%">
              <h4>🎫 Reservation Management</h4>
              <ul align="left">
                <li>Interactive seat selection</li>
                <li>Status tracking</li>
                <li>Concurrent booking protection</li>
              </ul>
            </td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
</div>

## 🏗️ Architecture

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.3, Spring Data JPA
- **Database**: MySQL 8.0
- **Security**: Spring Security, JJWT 0.12.6
- **API Documentation**: Swagger OpenAPI 3.0
- **Payment Processing**: Stripe API 22.0.0
- **PDF Generation**: iText 5.5.13.3
- **Email Service**: Spring Mail
- **Utilities**: Lombok, Resilience4j, MessageSource
- **Testing**: JUnit 5, Mockito

## 🔍 Installation

<div align="center">
  <table>
    <tr>
      <td width="30%" align="center">
        <h3>Prerequisites</h3>
        <ul align="left">
          <li>Java 17 or higher</li>
          <li>Maven 3.6+</li>
          <li>MySQL 8.0+</li>
        </ul>
      </td>
      <td width="70%" align="center">
        <h3>Quick Start Guide</h3>
        <ol align="left">
          <li>
            <strong>Clone the repository</strong>
            <pre><code>git clone https://github.com/lakshay1341/Movie-Review-System-API.git
cd Movie-Review-System-API</code></pre>
          </li>
          <li>
            <strong>Configure the database</strong>
            <pre><code>CREATE DATABASE moviereviewdbupdated;</code></pre>
          </li>
          <li>
            <strong>Configure application properties</strong><br>
            Update <code>src/main/resources/application.properties</code> with your database credentials, JWT configuration, and Stripe API keys.
            <blockquote>
              <p>⚠️ <strong>Security Note</strong>: Generate a secure JWT secret using <code>openssl rand -base64 64</code> and never commit it to version control.</p>
            </blockquote>
          </li>
          <li>
            <strong>Set up environment variables</strong><br>
            Create a <code>.env</code> file based on the provided <code>.env.example</code> with your configuration.<br>
            <pre><code># Copy the example file
cp .env.example .env

# Edit the .env file with your actual values
# For security, generate a new JWT secret:
openssl rand -base64 64</code></pre>
          </li>
          <li>
            <strong>Build and run the application</strong>
            <pre><code>mvn clean install
mvn spring-boot:run</code></pre>
          </li>
          <li>
            <strong>Access the application</strong>
            <ul>
              <li>API: <a href="http://localhost:8080">http://localhost:8080</a></li>
              <li>Swagger UI: <a href="http://localhost:8080/swagger-ui/index.html">http://localhost:8080/swagger-ui/index.html</a></li>
            </ul>
          </li>
        </ol>
      </td>
    </tr>
  </table>
</div>

### Default Credentials

The system automatically creates two users on startup:

| Role  | Username | Password |
|-------|----------|----------|
| Admin | `admin`  | `password` |
| User  | `user`   | `password` |

## 🔥 API Reference

<div align="center">
  <table>
    <tr>
      <td align="center" colspan="3">
        <a href="https://me3333-6732.postman.co/workspace/4f28700f-ee89-485e-a928-767cd44234f9">
          <img src="https://img.shields.io/badge/Postman-View%20Complete%20Collection-4a4e69?style=for-the-badge&logo=postman&logoColor=white" alt="View Complete Collection">
        </a>
      </td>
    </tr>
    <tr>
      <td align="center" colspan="3">
        <h3>API Categories</h3>
      </td>
    </tr>
    <tr>
      <td align="center">
        <a href="#-authentication"><img src="https://img.shields.io/badge/🔑_Authentication-2_Endpoints-4a4e69?style=flat-square" alt="Authentication: 2 Endpoints"></a>
      </td>
      <td align="center">
        <a href="#-movies"><img src="https://img.shields.io/badge/🎬_Movies-6_Endpoints-4a4e69?style=flat-square" alt="Movies: 6 Endpoints"></a>
      </td>
      <td align="center">
        <a href="#-reviews"><img src="https://img.shields.io/badge/⭐_Reviews-4_Endpoints-4a4e69?style=flat-square" alt="Reviews: 4 Endpoints"></a>
      </td>
    </tr>
    <tr>
      <td align="center">
        <a href="#-theaters"><img src="https://img.shields.io/badge/🏛️_Theaters-6_Endpoints-4a4e69?style=flat-square" alt="Theaters: 6 Endpoints"></a>
      </td>
      <td align="center">
        <a href="#-showtimes"><img src="https://img.shields.io/badge/📅_Showtimes-7_Endpoints-4a4e69?style=flat-square" alt="Showtimes: 7 Endpoints"></a>
      </td>
      <td align="center">
        <a href="#-seats"><img src="https://img.shields.io/badge/💺_Seats-2_Endpoints-4a4e69?style=flat-square" alt="Seats: 2 Endpoints"></a>
      </td>
    </tr>
    <tr>
      <td align="center" colspan="3">
        <a href="#-reservations"><img src="https://img.shields.io/badge/🎟️_Reservations-6_Endpoints-4a4e69?style=flat-square" alt="Reservations: 6 Endpoints"></a>
      </td>
    </tr>
  </table>
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

<div align="center">
  <table>
    <tr>
      <td align="center" width="50%">
        <h4>Security Features</h4>
        <ul align="left">
          <li><strong>JWT Authentication</strong>: Secure token-based authentication</li>
          <li><strong>Password Encryption</strong>: BCrypt encoding (strength 12)</li>
          <li><strong>Role-Based Access Control</strong>: User/admin permissions</li>
          <li><strong>Rate Limiting</strong>: 100 requests per minute</li>
          <li><strong>Concurrent Access Control</strong>: Pessimistic locking</li>
          <li><strong>Transactional Operations</strong>: Data integrity</li>
          <li><strong>Secure Payments</strong>: Stripe integration</li>
          <li><strong>Environment Variables</strong>: Secure credential management</li>
        </ul>
      </td>
      <td align="center" width="50%">
        <h4>Authentication Flow</h4>
        <ol align="left">
          <li>User registers or logs in with credentials</li>
          <li>Server validates credentials and returns a JWT token</li>
          <li>Client includes JWT in Authorization header</li>
          <li>Server validates token and grants access based on roles</li>
        </ol>
        <p><strong>Example Header:</strong></p>
        <pre><code>Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...</code></pre>
      </td>
    </tr>
  </table>
</div>

## 📖 Documentation

### API Documentation

<div align="center">
  <table>
    <tr>
      <td align="center" width="50%">
        <h4>Swagger UI</h4>
        <p>Interactive API documentation with request/response examples</p>
        <ul align="left">
          <li><a href="http://localhost:8080/swagger-ui/index.html">Local Development</a></li>
          <li><a href="https://cineticket.onrender.com/swagger-ui/index.html">Production</a></li>
          <li><a href="https://cineticket.onrender.com/api-docs">OpenAPI Specification</a></li>
        </ul>
      </td>
      <td align="center" width="50%">
        <h4>Postman Collection</h4>
        <p>Complete API testing suite with environments</p>
        <a href="https://me3333-6732.postman.co/workspace/4f28700f-ee89-485e-a928-767cd44234f9">
          <img src="https://img.shields.io/badge/Postman-Run%20in%20Postman-4a4e69?style=for-the-badge&logo=postman&logoColor=white" alt="Run in Postman">
        </a>
        <p><small>Also available in <code>docs/postman</code> directory</small></p>
      </td>
    </tr>
  </table>
</div>

### Response Format

All API responses follow a consistent format:

```json
{
  "success": true,
  "message": "operation.success.message",
  "data": {
    // Response data here
  }
}
```

### Postman Collection Features

- **Complete API Coverage**: All endpoints from authentication to reservations
- **Environment Variables**: Pre-configured for development and testing
- **Authentication Handling**: Automatic JWT token management
- **Test Scripts**: Response validation and environment variable extraction
- **Request Examples**: Sample payloads for all operations

## 💻 Contributing

Contributions are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add some amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request


## 🔗 Related Links

- 🎬 **Original Problem Statement**: [roadmap.sh](https://roadmap.sh/projects/movie-reservation-system/solutions?u=658efb1fae22c12523f4de17)
- 🌐 **Live Demo**: [https://cineticket.onrender.com](https://cineticket.onrender.com)
- 📚 **API Documentation**: [https://cineticket.onrender.com/swagger-ui/index.html](https://cineticket.onrender.com/swagger-ui/index.html)


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
