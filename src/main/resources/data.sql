-- Insert component types if they don't exist
INSERT INTO component_types (id, name) VALUES (1, 'RESERVATION_STATUS')
ON DUPLICATE KEY UPDATE name = 'RESERVATION_STATUS';

-- Insert master data for reservation statuses if they don't exist
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (1, 'CONFIRMED', 1)
ON DUPLICATE KEY UPDATE data_value = 'CONFIRMED';
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (2, 'PAID', 1)
ON DUPLICATE KEY UPDATE data_value = 'PAID';
INSERT INTO master_data (master_data_id, data_value, component_type_id) VALUES (3, 'CANCELED', 1)
ON DUPLICATE KEY UPDATE data_value = 'CANCELED';

-- Update any existing reservations to use the new status IDs
UPDATE reservations SET status_id = 1 WHERE status_id IS NULL;

-- Insert roles if they don't exist
INSERT INTO roles (id, name) VALUES (1, 'ROLE_ADMIN')
ON DUPLICATE KEY UPDATE name = 'ROLE_ADMIN';
INSERT INTO roles (id, name) VALUES (2, 'ROLE_USER')
ON DUPLICATE KEY UPDATE name = 'ROLE_USER';

-- If admin user doesn't exist, create it
INSERT INTO users (user_name, email, password, role_id)
SELECT 'admin', 'admin@moviereview.com', '{bcrypt}$2a$12$v7OOmf67vtCyNVQBcqMhbuW6pWgd7i1Z0b35qUQ5S1jkNo8CRNZrG',
(SELECT id FROM roles WHERE name = 'ROLE_ADMIN')
WHERE NOT EXISTS (SELECT 1 FROM users WHERE user_name = 'admin');

-- If test user doesn't exist, create it
INSERT INTO users (user_name, email, password, role_id)
SELECT 'user', 'user@moviereview.com', '{bcrypt}$2a$12$v7OOmf67vtCyNVQBcqMhbuW6pWgd7i1Z0b35qUQ5S1jkNo8CRNZrG',
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

-- Sample showtimes for the next 14 days
-- First, delete any existing showtimes with past dates
DELETE FROM showtimes WHERE show_date < CURDATE();

-- For The Matrix
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 1, 1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '18:00:00', 150, 150, 12.99
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
