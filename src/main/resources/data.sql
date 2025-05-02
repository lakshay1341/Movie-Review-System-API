-- component types for lookup values
-- added these first - they're referenced by other tables
INSERT INTO component_types (id, name) VALUES (1, 'RESERVATION_STATUS')
ON DUPLICATE KEY UPDATE name = 'RESERVATION_STATUS'; -- avoid dupes

-- reservation statuses - used in the reservations table
-- 1=confirmed (initial state), 2=paid, 3=canceled
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (1, 'CONFIRMED', 1)
ON DUPLICATE KEY UPDATE data_value = 'CONFIRMED';
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (2, 'PAID', 1)
ON DUPLICATE KEY UPDATE data_value = 'PAID';
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (3, 'CANCELED', 1)
ON DUPLICATE KEY UPDATE data_value = 'CANCELED';

-- Update any existing reservations to use the new status IDs
UPDATE reservations SET status_id = 1 WHERE status_id IS NULL;

-- roles for spring security
-- keep ids fixed - they're referenced by user accounts
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN') -- admin role
ON DUPLICATE KEY UPDATE name = 'ROLE_ADMIN';
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER') -- regular user
ON DUPLICATE KEY UPDATE name = 'ROLE_USER';

-- create admin account if it doesn't exist yet
-- note: had to fix the bcrypt format to work with spring security
INSERT INTO users (user_name, email, password, role_id)
SELECT 'admin', 'admin@moviereview.com', '{bcrypt}$2a$12$v7OOmf67vtCyNVQBcqMhbuW6pWgd7i1Z0b35qUQ5S1jkNo8CRNZrG', -- hashed 'password'
(SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');

-- test user account - for testing the app
INSERT INTO users (user_name, email, password, role_id)
SELECT 'user', 'user@moviereview.com', '{bcrypt}$2a$12$v7OOmf67vtCyNVQBcqMhbuW6pWgd7i1Z0b35qUQ5S1jkNo8CRNZrG', -- same pwd
(SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'user');

-- login info for testing:
-- username: admin, password: password
-- username: user, password: password

-- sample theaters - using fixed IDs for easier reference
-- todo: add more theaters with different capacities
INSERT INTO theaters (id, name, location, capacity)
SELECT 1, 'Cineplex', 'Downtown', 150 -- medium size
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 1);

INSERT INTO theaters (id, name, location, capacity)
SELECT 2, 'MovieMax', 'Uptown', 200 -- large theater
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 2);

INSERT INTO theaters (id, name, location, capacity)
SELECT 3, 'FilmHouse', 'Westside', 100 -- small indie theater
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 3);

-- fix auto-increment so new theaters start at id=4
ALTER TABLE theaters AUTO_INCREMENT = 4;

-- Sample movies with descriptions and real poster URLs
INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 1, 'The Matrix', 'Sci-Fi', 1999, 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 'https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 1);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 2, 'Inception', 'Sci-Fi', 2010, 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'https://m.media-amazon.com/images/M/MV5BMjAxMzY3NjcxNF5BMl5BanBnXkFtZTcwNTI5OTM0Mw@@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 2);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 3, 'Pulp Fiction', 'Crime', 1994, 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 'https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 3);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 4, 'The Dark Knight', 'Action', 2008, 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 4);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 5, 'Fight Club', 'Drama', 1999, 'An insomniac office worker and a devil-may-care soapmaker form an underground fight club that evolves into something much, much more.', 'https://m.media-amazon.com/images/M/MV5BMmEzNTkxYjQtZTc0MC00YTVjLTg5ZTEtZWMwOWVlYzY0NWIwXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 5);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 6, 'Forrest Gump', 'Drama', 1994, 'The presidencies of Kennedy and Johnson, the events of Vietnam, Watergate, and other historical events unfold through the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.', 'https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 6);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 7, 'The Lord of the Rings: The Fellowship of the Ring', 'Fantasy', 2001, 'A meek Hobbit from the Shire and eight companions set out on a journey to destroy the powerful One Ring and save Middle-earth from the Dark Lord Sauron.', 'https://m.media-amazon.com/images/M/MV5BN2EyZjM3NzUtNWUzMi00MTgxLWI0NTctMzY4M2VlOTdjZWRiXkEyXkFqcGdeQXVyNDUzOTQ5MjY@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 7);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 8, 'Interstellar', 'Sci-Fi', 2014, 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', 'https://m.media-amazon.com/images/M/MV5BZjdkOTU3MDktN2IxOS00OGEyLWFmMjktY2FiMmZkNWIyODZiXkEyXkFqcGdeQXVyMTMxODk2OTU@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 8);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 9, 'The Godfather', 'Crime', 1972, 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', 'https://m.media-amazon.com/images/M/MV5BM2MyNjYxNmUtYTAwNi00MTYxLWJmNWYtYzZlODY3ZTk3OTFlXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 9);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 10, 'Avengers: Endgame', 'Action', 2019, 'After the devastating events of Avengers: Infinity War, the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos'' actions and restore balance to the universe.', 'https://m.media-amazon.com/images/M/MV5BMTc5MDE2ODcwNV5BMl5BanBnXkFtZTgwMzI2NzQ2NzM@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 10);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE movies AUTO_INCREMENT = 11;

-- sample showtimes for the next 14 days
-- clean up old showtimes first - don't want stale data
DELETE FROM showtimes WHERE show_date < CURDATE(); -- remove past dates

-- The Matrix showtimes
-- using DATE_ADD to make sure dates are always in the future
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00', 150, 150, 12.99 -- evening show
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 1);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 2, 1, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '19:30:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 2);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 3, 1, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '20:00:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 3);

-- For Inception
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 4, 2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '20:30:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 4);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 5, 2, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '21:00:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 5);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 6, 2, 3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '17:30:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 6);

-- For Pulp Fiction
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 7, 3, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '19:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 7);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 8, 3, 2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '20:30:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 8);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 9, 3, 3, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '18:00:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 9);

-- For Inception (additional showtimes)
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 10, 2, 1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '16:30:00', 150, 150, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 10);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 11, 2, 1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '20:00:00', 150, 150, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 11);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 12, 2, 3, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '18:30:00', 100, 100, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 12);

-- For The Dark Knight
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 13, 4, 1, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '19:30:00', 150, 150, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 13);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 14, 4, 2, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '20:00:00', 200, 200, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 14);

-- For Fight Club
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 15, 5, 2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '21:30:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 15);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 16, 5, 3, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '19:00:00', 100, 100, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 16);

-- For Forrest Gump
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 17, 6, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '17:00:00', 150, 150, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 17);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 18, 6, 2, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '18:30:00', 200, 200, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 18);

-- For Lord of the Rings
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 19, 7, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '16:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 19);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 20, 7, 2, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '17:30:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 20);

-- For Interstellar
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 21, 8, 2, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '19:00:00', 200, 200, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 21);

-- For The Godfather
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 22, 9, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '18:00:00', 150, 150, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 22);

-- For Lord of the Rings
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 23, 7, 3, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '17:30:00', 100, 100, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 23);

-- For Interstellar
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 24, 8, 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '20:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 24);

-- For The Godfather
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 25, 9, 2, DATE_ADD(CURDATE(), INTERVAL 8 DAY), '19:00:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 25);

-- For Avengers: Endgame
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 26, 10, 3, DATE_ADD(CURDATE(), INTERVAL 9 DAY), '18:30:00', 100, 100, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 26);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE showtimes AUTO_INCREMENT = 27;

-- Create seats for all showtimes
-- First, clean up any existing seats to avoid duplicates
DELETE FROM seats WHERE showtime_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26);

-- Create seats for showtime ID 26 (Avengers: Endgame at FilmHouse)
-- FilmHouse (theater_id = 3) has 100 seats (10x10 grid)
INSERT INTO seats (showtime_id, seat_number, is_reserved) VALUES
-- Row A
(26, 'A1', false), (26, 'A2', false), (26, 'A3', false), (26, 'A4', false), (26, 'A5', false),
(26, 'A6', false), (26, 'A7', false), (26, 'A8', false), (26, 'A9', false), (26, 'A10', false),
-- Row B
(26, 'B1', false), (26, 'B2', false), (26, 'B3', false), (26, 'B4', false), (26, 'B5', false),
(26, 'B6', false), (26, 'B7', false), (26, 'B8', false), (26, 'B9', false), (26, 'B10', false),
-- Row C
(26, 'C1', false), (26, 'C2', false), (26, 'C3', false), (26, 'C4', false), (26, 'C5', false),
(26, 'C6', false), (26, 'C7', false), (26, 'C8', false), (26, 'C9', false), (26, 'C10', false),
-- Row D
(26, 'D1', false), (26, 'D2', false), (26, 'D3', false), (26, 'D4', false), (26, 'D5', false),
(26, 'D6', false), (26, 'D7', false), (26, 'D8', false), (26, 'D9', false), (26, 'D10', false),
-- Row E
(26, 'E1', false), (26, 'E2', false), (26, 'E3', false), (26, 'E4', false), (26, 'E5', false),
(26, 'E6', false), (26, 'E7', false), (26, 'E8', false), (26, 'E9', false), (26, 'E10', false),
-- Row F
(26, 'F1', false), (26, 'F2', false), (26, 'F3', false), (26, 'F4', false), (26, 'F5', false),
(26, 'F6', false), (26, 'F7', false), (26, 'F8', false), (26, 'F9', false), (26, 'F10', false),
-- Row G
(26, 'G1', false), (26, 'G2', false), (26, 'G3', false), (26, 'G4', false), (26, 'G5', false),
(26, 'G6', false), (26, 'G7', false), (26, 'G8', false), (26, 'G9', false), (26, 'G10', false),
-- Row H
(26, 'H1', false), (26, 'H2', false), (26, 'H3', false), (26, 'H4', false), (26, 'H5', false),
(26, 'H6', false), (26, 'H7', false), (26, 'H8', false), (26, 'H9', false), (26, 'H10', false),
-- Row I
(26, 'I1', false), (26, 'I2', false), (26, 'I3', false), (26, 'I4', false), (26, 'I5', false),
(26, 'I6', false), (26, 'I7', false), (26, 'I8', false), (26, 'I9', false), (26, 'I10', false),
-- Row J
(26, 'J1', false), (26, 'J2', false), (26, 'J3', false), (26, 'J4', false), (26, 'J5', false),
(26, 'J6', false), (26, 'J7', false), (26, 'J8', false), (26, 'J9', false), (26, 'J10', false);

-- Create seats for other showtimes
-- For brevity, we'll create a few seats for each showtime (A1-A5)
-- In a real scenario, you'd want to create all seats for each showtime

-- Create seats for showtimes at Cineplex (theater_id = 1) - IDs 1, 4, 7, 10, 11, 13, 17, 19, 22, 24
INSERT INTO seats (showtime_id, seat_number, is_reserved) VALUES
-- Showtime 1
(1, 'A1', false), (1, 'A2', false), (1, 'A3', false), (1, 'A4', false), (1, 'A5', false),
-- Showtime 4
(4, 'A1', false), (4, 'A2', false), (4, 'A3', false), (4, 'A4', false), (4, 'A5', false),
-- Showtime 7
(7, 'A1', false), (7, 'A2', false), (7, 'A3', false), (7, 'A4', false), (7, 'A5', false),
-- Showtime 10
(10, 'A1', false), (10, 'A2', false), (10, 'A3', false), (10, 'A4', false), (10, 'A5', false),
-- Showtime 11
(11, 'A1', false), (11, 'A2', false), (11, 'A3', false), (11, 'A4', false), (11, 'A5', false),
-- Showtime 13
(13, 'A1', false), (13, 'A2', false), (13, 'A3', false), (13, 'A4', false), (13, 'A5', false),
-- Showtime 17
(17, 'A1', false), (17, 'A2', false), (17, 'A3', false), (17, 'A4', false), (17, 'A5', false),
-- Showtime 19
(19, 'A1', false), (19, 'A2', false), (19, 'A3', false), (19, 'A4', false), (19, 'A5', false),
-- Showtime 22
(22, 'A1', false), (22, 'A2', false), (22, 'A3', false), (22, 'A4', false), (22, 'A5', false),
-- Showtime 24
(24, 'A1', false), (24, 'A2', false), (24, 'A3', false), (24, 'A4', false), (24, 'A5', false);

-- Create seats for showtimes at MovieMax (theater_id = 2) - IDs 2, 5, 8, 14, 15, 18, 20, 21, 25
INSERT INTO seats (showtime_id, seat_number, is_reserved) VALUES
-- Showtime 2
(2, 'A1', false), (2, 'A2', false), (2, 'A3', false), (2, 'A4', false), (2, 'A5', false),
-- Showtime 5
(5, 'A1', false), (5, 'A2', false), (5, 'A3', false), (5, 'A4', false), (5, 'A5', false),
-- Showtime 8
(8, 'A1', false), (8, 'A2', false), (8, 'A3', false), (8, 'A4', false), (8, 'A5', false),
-- Showtime 14
(14, 'A1', false), (14, 'A2', false), (14, 'A3', false), (14, 'A4', false), (14, 'A5', false),
-- Showtime 15
(15, 'A1', false), (15, 'A2', false), (15, 'A3', false), (15, 'A4', false), (15, 'A5', false),
-- Showtime 18
(18, 'A1', false), (18, 'A2', false), (18, 'A3', false), (18, 'A4', false), (18, 'A5', false),
-- Showtime 20
(20, 'A1', false), (20, 'A2', false), (20, 'A3', false), (20, 'A4', false), (20, 'A5', false),
-- Showtime 21
(21, 'A1', false), (21, 'A2', false), (21, 'A3', false), (21, 'A4', false), (21, 'A5', false),
-- Showtime 25
(25, 'A1', false), (25, 'A2', false), (25, 'A3', false), (25, 'A4', false), (25, 'A5', false);

-- Create seats for showtimes at FilmHouse (theater_id = 3) - IDs 3, 6, 9, 12, 16, 23
INSERT INTO seats (showtime_id, seat_number, is_reserved) VALUES
-- Showtime 3
(3, 'A1', false), (3, 'A2', false), (3, 'A3', false), (3, 'A4', false), (3, 'A5', false),
-- Showtime 6
(6, 'A1', false), (6, 'A2', false), (6, 'A3', false), (6, 'A4', false), (6, 'A5', false),
-- Showtime 9
(9, 'A1', false), (9, 'A2', false), (9, 'A3', false), (9, 'A4', false), (9, 'A5', false),
-- Showtime 12
(12, 'A1', false), (12, 'A2', false), (12, 'A3', false), (12, 'A4', false), (12, 'A5', false),
-- Showtime 16
(16, 'A1', false), (16, 'A2', false), (16, 'A3', false), (16, 'A4', false), (16, 'A5', false),
-- Showtime 23
(23, 'A1', false), (23, 'A2', false), (23, 'A3', false), (23, 'A4', false), (23, 'A5', false);
