-- If roles table is empty, insert default roles
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

-- If admin user doesn't exist, create it
INSERT INTO users (user_name, password, role_id)
SELECT 'admin', '$2a$12$ZFqD8mVpocAMPR.Tgrwkee2ChoY8wEpVCCKjOtRo1tX7KggSYb5Iq',
(SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');

-- If test user doesn't exist, create it
INSERT INTO users (user_name, password, role_id)
SELECT 'user', '$2a$12$ZFqD8mVpocAMPR.Tgrwkee2ChoY8wEpVCCKjOtRo1tX7KggSYb5Iq',
(SELECT id FROM roles WHERE name = 'ROLE_USER')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'user');

-- username = admin, password = password
-- username = user, password = password

-- Sample theaters with explicit IDs
INSERT INTO theaters (id, name, location, capacity)
SELECT 1, 'Cineplex', 'Downtown', 150
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 1);

INSERT INTO theaters (id, name, location, capacity)
SELECT 2, 'MovieMax', 'Uptown', 200
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 2);

INSERT INTO theaters (id, name, location, capacity)
SELECT 3, 'FilmHouse', 'Westside', 100
WHERE NOT EXISTS (SELECT 1 FROM theaters WHERE id = 3);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE theaters AUTO_INCREMENT = 4;

-- Sample movies with descriptions and poster URLs
INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 1, 'The Matrix', 'Sci-Fi', 1999, 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', 'https://example.com/matrix.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 1);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 2, 'Inception', 'Sci-Fi', 2010, 'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.', 'https://example.com/inception.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 2);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 3, 'The Shawshank Redemption', 'Drama', 1994, 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 'https://example.com/shawshank.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 3);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE movies AUTO_INCREMENT = 4;

-- Sample showtimes for the next 7 days
-- For The Matrix at Cineplex
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 1, 1, 1, CURDATE() + INTERVAL 1 DAY, '18:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 1);

-- For Inception at MovieMax
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 2, 2, 2, CURDATE() + INTERVAL 1 DAY, '19:30:00', 200, 200, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 2);

-- For Shawshank at FilmHouse
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 3, 3, 3, CURDATE() + INTERVAL 2 DAY, '20:00:00', 100, 100, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 3);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE showtimes AUTO_INCREMENT = 4;

-- Add seats for Showtime 1 (The Matrix at Cineplex - 150 seats)
INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 1, CONCAT('A', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 1 AND seat_number LIKE 'A%');

INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 1, CONCAT('B', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 1 AND seat_number LIKE 'B%');

INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 1, CONCAT('C', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 1 AND seat_number LIKE 'C%');

-- Add seats for Showtime 2 (Inception at MovieMax - 200 seats)
INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 2, CONCAT('A', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 2 AND seat_number LIKE 'A%');

INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 2, CONCAT('B', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 2 AND seat_number LIKE 'B%');

-- Add seats for Showtime 3 (Shawshank at FilmHouse - 100 seats)
INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 3, CONCAT('A', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 3 AND seat_number LIKE 'A%');

INSERT INTO seats (showtime_id, seat_number, is_reserved)
SELECT 3, CONCAT('B', seat_num), false
FROM (
    SELECT 1 AS seat_num UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION
    SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) AS t
WHERE NOT EXISTS (SELECT 1 FROM seats WHERE showtime_id = 3 AND seat_number LIKE 'B%');
