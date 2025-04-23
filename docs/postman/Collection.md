# Movie Review & Reservation System Postman Collection

This folder contains a comprehensive Postman collection for testing the Movie Review & Reservation System API.

## Files

- `Movie_Review_Reservation_System.postman_collection.json`: The main Postman collection with all API endpoints
- `Movie_Review_Reservation_System.postman_environment.json`: Environment variables for the collection

## Setup Instructions

1. Import both files into Postman:
   - Open Postman
   - Click "Import" in the top left
   - Select both files
   - Click "Import"

2. Select the "Movie Review & Reservation System" environment from the dropdown in the top right

3. Make sure the Movie Review & Reservation System API is running on http://localhost:8080

## Collection Structure

The collection is organized into logical sections that follow a typical user flow:

1. **Authentication**
   - Login as User
   - Login as Admin
   - Register New User

2. **Movie Management**
   - Get All Movies
   - Search Movies
   - Get Movie by ID
   - Add Movie (Admin)

3. **Review Management**
   - Add Review for Movie
   - Get My Reviews
   - Update Review

4. **Theater Management**
   - Get All Theaters
   - Get Theater by ID
   - Search Theaters by Location
   - Add Theater (Admin)

5. **Showtime Management**
   - Get Showtimes by Date
   - Get Showtimes by Movie
   - Get Showtimes by Theater
   - Get Available Showtimes
   - Get Showtime by ID
   - Add Showtime (Admin)

6. **Seat Management**
   - Get All Seats for Showtime
   - Get Available Seats for Showtime

7. **Reservation Management**
   - Create Reservation
   - Get My Reservations
   - Get Reservation by ID
   - Cancel Reservation
   - Get All Reservations (Admin)

## Testing Flow

For a complete test of the user flow, follow these steps:

1. Run "Login as User" to get a user token
2. Run "Get All Movies" to see available movies
3. Run "Get Showtimes by Movie" for a specific movie
4. Run "Get All Seats for Showtime" to see available seats
5. Run "Create Reservation" to book seats
6. Run "Get My Reservations" to see your bookings
7. Run "Add Review for Movie" to leave a review
8. Run "Get My Reviews" to see your reviews

For admin operations:

1. Run "Login as Admin" to get an admin token
2. Run "Add Movie" to create a new movie
3. Run "Add Theater" to create a new theater
4. Run "Add Showtime" to create a new showtime
5. Run "Get All Reservations" to see all user reservations

## Notes

- The collection uses environment variables to store tokens, IDs, and other values
- Pre-request scripts automatically set date variables and check for required values
- Test scripts automatically extract and store tokens and IDs from responses
- The collection works with the data from data.sql in the application

## Troubleshooting

If you encounter any issues:

1. Make sure the API is running on http://localhost:8080
2. Check that the database has been initialized with data.sql
3. Verify that you're using the correct environment
4. Try running the requests in order, starting with authentication
