-- Insert component types if they don't exist
MERGE INTO component_types (id, name) KEY(id) VALUES (1, 'RESERVATION_STATUS');

-- Insert master data for reservation statuses if they don't exist
MERGE INTO master_data (master_data_id, data_value, component_type_id) KEY(component_type_id, master_data_id) VALUES (1, 'CONFIRMED', 1);
MERGE INTO master_data (master_data_id, data_value, component_type_id) KEY(component_type_id, master_data_id) VALUES (2, 'PAID', 1);
MERGE INTO master_data (master_data_id, data_value, component_type_id) KEY(component_type_id, master_data_id) VALUES (3, 'CANCELED', 1);

-- Update any existing reservations to use the new status IDs
UPDATE reservations SET status_id = 1 WHERE status_id IS NULL;

-- If roles table is empty, insert default roles
INSERT INTO roles (name) SELECT 'ROLE_USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_USER');
INSERT INTO roles (name) SELECT 'ROLE_ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_ADMIN');

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
SELECT 3, 'The Shawshank Redemption', 'Drama', 1994, 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', 'https://m.media-amazon.com/images/M/MV5BNDE3ODcxYzMtY2YzZC00NmNlLWJiNDMtZDViZWM2MzIxZDYwXkEyXkFqcGdeQXVyNjAwNDUxODI@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 3);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 4, 'The Dark Knight', 'Action', 2008, 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', 'https://m.media-amazon.com/images/M/MV5BMTMxNTMwODM0NF5BMl5BanBnXkFtZTcwODAyMTk2Mw@@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 4);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 5, 'Pulp Fiction', 'Crime', 1994, 'The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.', 'https://m.media-amazon.com/images/M/MV5BNGNhMDIzZTUtNTBlZi00MTRlLWFjM2ItYzViMjE3YzI5MjljXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_.jpg'
WHERE NOT EXISTS (SELECT 1 FROM movies WHERE id = 5);

INSERT INTO movies (id, title, genre, release_year, description, poster_image_url)
SELECT 6, 'Forrest Gump', 'Drama', 1994, 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75, whose only desire is to be reunited with his childhood sweetheart.', 'https://m.media-amazon.com/images/M/MV5BNWIwODRlZTUtY2U3ZS00Yzg1LWJhNzYtMmZiYmEyNmU1NjMzXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_.jpg'
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

-- For The Matrix at Cineplex
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 1, 1, 1, DATEADD('DAY', 1, CURRENT_DATE()), '14:30:00', 150, 150, 9.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 1);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 2, 1, 1, DATEADD('DAY', 1, CURRENT_DATE()), '18:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 2);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 3, 1, 1, DATEADD('DAY', 1, CURRENT_DATE()), '21:30:00', 150, 150, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 3);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 4, 1, 2, DATEADD('DAY', 3, CURRENT_DATE()), '15:00:00', 200, 200, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 4);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 5, 1, 2, DATEADD('DAY', 3, CURRENT_DATE()), '19:00:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 5);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 6, 1, 3, DATEADD('DAY', 5, CURRENT_DATE()), '17:30:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 6);

-- For Inception at MovieMax
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 7, 2, 2, DATEADD('DAY', 2, CURRENT_DATE()), '14:00:00', 200, 200, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 7);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 8, 2, 2, DATEADD('DAY', 2, CURRENT_DATE()), '17:30:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 8);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 9, 2, 2, DATEADD('DAY', 2, CURRENT_DATE()), '21:00:00', 200, 200, 15.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 9);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 10, 2, 1, DATEADD('DAY', 4, CURRENT_DATE()), '16:30:00', 150, 150, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 10);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 11, 2, 1, DATEADD('DAY', 4, CURRENT_DATE()), '20:00:00', 150, 150, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 11);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 12, 2, 3, DATEADD('DAY', 6, CURRENT_DATE()), '18:30:00', 100, 100, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 12);

-- For Shawshank at FilmHouse
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 13, 3, 3, DATEADD('DAY', 1, CURRENT_DATE()), '15:30:00', 100, 100, 8.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 13);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 14, 3, 3, DATEADD('DAY', 1, CURRENT_DATE()), '19:00:00', 100, 100, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 14);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 15, 3, 3, DATEADD('DAY', 3, CURRENT_DATE()), '16:00:00', 100, 100, 9.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 15);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 16, 3, 3, DATEADD('DAY', 3, CURRENT_DATE()), '20:30:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 16);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 17, 3, 1, DATEADD('DAY', 5, CURRENT_DATE()), '14:00:00', 150, 150, 9.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 17);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 18, 3, 2, DATEADD('DAY', 7, CURRENT_DATE()), '17:00:00', 200, 200, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 18);

-- For The Dark Knight
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 19, 4, 1, DATEADD('DAY', 2, CURRENT_DATE()), '15:30:00', 150, 150, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 19);

INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 20, 4, 2, DATEADD('DAY', 4, CURRENT_DATE()), '18:00:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 20);

-- For Pulp Fiction
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 21, 5, 3, DATEADD('DAY', 3, CURRENT_DATE()), '19:30:00', 100, 100, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 21);

-- For Forrest Gump
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 22, 6, 2, DATEADD('DAY', 5, CURRENT_DATE()), '16:00:00', 200, 200, 11.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 22);

-- For Lord of the Rings
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 23, 7, 3, DATEADD('DAY', 6, CURRENT_DATE()), '17:30:00', 100, 100, 10.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 23);

-- For Interstellar
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 24, 8, 1, DATEADD('DAY', 7, CURRENT_DATE()), '20:00:00', 150, 150, 12.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 24);

-- For The Godfather
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 25, 9, 2, DATEADD('DAY', 8, CURRENT_DATE()), '19:00:00', 200, 200, 13.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 25);

-- For Avengers: Endgame
INSERT INTO showtimes (id, movie_id, theater_id, show_date, show_time, total_seats, available_seats, price)
SELECT 26, 10, 3, DATEADD('DAY', 9, CURRENT_DATE()), '18:30:00', 100, 100, 14.99
WHERE NOT EXISTS (SELECT 1 FROM showtimes WHERE id = 26);

-- Reset auto-increment sequence after explicit IDs
ALTER TABLE showtimes AUTO_INCREMENT = 27;
