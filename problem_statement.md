# Movie Review & Reservation System - Problem Statement

## 1. The Challenge

Meet Pranvi, a passionate YouTube movie reviewer and theater owner. She loves sharing her thoughts on the latest releases, but managing her growing collection of reviews and theater reservations has become a nightmare. Juggling notes, spreadsheets, scattered comments from her subscribers, and manual theater bookings, she finds herself drowning in disorganized data.

That's where you come in! As a backend developer, it's your mission to build a powerful movie review and reservation system that helps Pranvi streamline her workflow. With your expertise, she can finally focus on what she does best—reviewing movies and running her theater business—without worrying about data chaos.

## 2. Your Toolbox

To be Pranvi's hero, you need the right set of tools:

- **Spring Boot** to develop robust backend services.
- **MySQL** to store and organize movie and review data.
- **JPA/Hibernate** to seamlessly interact with the database.
- **Swagger UI (optional)** to document and test APIs.
- **GitHub** to track progress and share your work.

## 3. Understanding the Data

Pranvi needs several key entities in her system:

### Movies
Each movie should have:
- An **ID**
- A **title**
- A **genre**
- A **release year**
- A **description**
- A **poster image URL**

This will allow her to keep track of everything she has reviewed and display movie information to customers.

### Reviews
Each review should include:
- **Reviewer's name**
- **Comment**
- **Rating**

Since Pranvi often updates her opinions on movies, she needs a way to modify reviews when necessary.

### Theaters
Each theater should have:
- An **ID**
- A **name**
- A **location**
- A **capacity**

This allows Pranvi to manage multiple theater locations.

### Showtimes
Each showtime should include:
- A **movie**
- A **theater**
- A **date**
- A **time**
- **Total seats**
- **Available seats**
- A **price**

This allows scheduling of movie screenings at specific theaters.

### Seats
Each seat should include:
- A **showtime**
- A **seat number**
- A **reservation status**

This allows tracking of individual seats for each showtime.

### Reservations
Each reservation should include:
- A **user**
- A **showtime**
- **Selected seats**
- A **reservation time**
- A **status** (confirmed/canceled)
- A **total price**

This allows customers to reserve seats for specific showtimes.

## 4. API Endpoints

To make life easier for Pranvi, you need to build the following API endpoints:

### Movie Review System

#### 1. Get All Movies with Reviews
**Endpoint:** `GET /api/v1/movies`
Returns a list of movies along with their reviews.

#### 2. Add a Movie
**Endpoint:** `POST /api/v1/movies`
Accepts movie details and stores them in the database.

#### 3. Add a Review for a Movie
**Endpoint:** `POST /api/v1/reviews/movies/{movieId}`
Accepts a review and associates it with a given movie.

#### 4. Update a Review
**Endpoint:** `PUT /api/v1/reviews/{reviewId}`
Updates the review comment and rating.

### Movie Reservation System

#### 5. Theater Management
**Endpoints:**
- `GET /api/v1/theaters` - Get all theaters
- `GET /api/v1/theaters/{id}` - Get theater by ID
- `POST /api/v1/theaters` - Add a new theater (admin only)
- `PUT /api/v1/theaters/{id}` - Update a theater (admin only)
- `DELETE /api/v1/theaters/{id}` - Delete a theater (admin only)

#### 6. Showtime Management
**Endpoints:**
- `GET /api/v1/showtimes?date={date}` - Get showtimes by date
- `GET /api/v1/showtimes/movies/{movieId}` - Get showtimes by movie
- `GET /api/v1/showtimes/theaters/{theaterId}` - Get showtimes by theater
- `POST /api/v1/showtimes` - Add a new showtime (admin only)
- `PUT /api/v1/showtimes/{id}` - Update a showtime (admin only)
- `DELETE /api/v1/showtimes/{id}` - Delete a showtime (admin only)

#### 7. Seat Management
**Endpoints:**
- `GET /api/v1/seats/showtimes/{showtimeId}` - Get all seats for a showtime
- `GET /api/v1/seats/showtimes/{showtimeId}/available` - Get available seats for a showtime

#### 8. Reservation Management
**Endpoints:**
- `GET /api/v1/reservations/my-reservations` - Get user's reservations
- `GET /api/v1/reservations/{id}` - Get reservation by ID
- `POST /api/v1/reservations` - Create a new reservation
- `DELETE /api/v1/reservations/{id}` - Cancel a reservation

## 5. Completing the Task

Now that you have a plan, it's time to make it happen:

1. Build the APIs that will power Pranvi's movie review and reservation system.
2. Implement proper security with role-based access control.
3. Ensure data integrity with transaction management and concurrency control.
4. Document the API with Swagger.
5. Push your code to a GitHub repository.
6. Submit the repository URL in the provided textbox.

With your solution in place, Pranvi can efficiently manage her reviews, theater operations, engage with her audience, and continue growing her business.

**Will you rise to the challenge and be the hero she needs?**
