# Movie Review System - Problem Statement

## 1. The Challenge

Meet Pranvi, a passionate YouTube movie reviewer. She loves sharing her thoughts on the latest releases, but managing her growing collection of reviews has become a nightmare. Juggling notes, spreadsheets, and scattered comments from her subscribers, she finds herself drowning in disorganized data.

That's where you come in! As a backend developer, it's your mission to build a powerful movie review system that helps Pranvi streamline her workflow. With your expertise, she can finally focus on what she does best—reviewing movies—without worrying about data chaos.

## 2. Your Toolbox

To be Pranvi’s hero, you need the right set of tools:

- **Spring Boot** to develop robust backend services.
- **MySQL** to store and organize movie and review data.
- **JPA/Hibernate** to seamlessly interact with the database.
- **Swagger UI (optional)** to document and test APIs.
- **GitHub** to track progress and share your work.

## 3. Understanding the Data

Pranvi needs two key entities in her system:

### Movies  
Each movie should have:
- An **ID**
- A **title**
- A **genre**
- A **release year**  

This will allow her to keep track of everything she has reviewed.

### Reviews  
Each review should include:
- **Reviewer's name**
- **Comment**
- **Rating**  

Since Pranvi often updates her opinions on movies, she needs a way to modify reviews when necessary. How will you ensure the data structure supports this?

## 4. API Endpoints

To make life easier for Pranvi, you need to build the following API endpoints:

### 1. Get All Movies with Reviews  
**Endpoint:** `GET /movies`  
Returns a list of movies along with their reviews.

### 2. Add a Movie  
**Endpoint:** `POST /movies`  
Accepts movie details and stores them in the database.

### 3. Add a Review for a Movie  
**Endpoint:** `POST /movies/{movieId}/reviews`  
Accepts a review and associates it with a given movie.

### 4. Update a Review  
**Endpoint:** `PUT /reviews/{reviewId}`  
Updates the review comment and rating.

## 5. Completing the Task

Now that you have a plan, it's time to make it happen:

1. Build the APIs that will power Pranvi’s movie review system.
2. Push your code to a GitHub repository.
3. Submit the repository URL in the provided textbox.

With your solution in place, Pranvi can efficiently manage her reviews, engage with her audience, and continue growing her YouTube channel.  

**Will you rise to the challenge and be the hero she needs?**
