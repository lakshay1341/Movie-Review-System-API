-- component types for master data
-- added 2023-04-15
CREATE TABLE IF NOT EXISTS component_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE  -- like 'RESERVATION_STATUS', 'PAYMENT_STATUS', etc
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- master data table - for lookup values and enums
CREATE TABLE IF NOT EXISTS master_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    master_data_id INT NOT NULL, -- numeric id within component type
    data_value VARCHAR(100) NOT NULL, -- display value
    component_type_id INT NOT NULL,
    FOREIGN KEY (component_type_id) REFERENCES component_types(id),
    UNIQUE KEY unique_master_data (component_type_id, master_data_id) -- prevent dupes
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create roles table if it doesn't exist
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- users table - stores all user accounts
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE, -- login name
    email VARCHAR(100) NOT NULL, -- for notifications
    password VARCHAR(255) NOT NULL, -- bcrypt hashed
    role_id INT NOT NULL, -- ROLE_USER or ROLE_ADMIN
    FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; -- utf8 for intl support

-- Create movies table if it doesn't exist
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT,
    genre VARCHAR(100),
    poster_image_url VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create theaters table if it doesn't exist
CREATE TABLE IF NOT EXISTS theaters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    capacity INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create showtimes table if it doesn't exist
CREATE TABLE IF NOT EXISTS showtimes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT,
    theater_id BIGINT,
    show_date DATE NOT NULL,
    show_time TIME NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    total_seats INT NOT NULL,
    available_seats INT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (theater_id) REFERENCES theaters(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- reservations - when a user books seats for a showtime
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL, -- who made the reservation
    showtime_id BIGINT NOT NULL, -- what movie/theater/time
    reservation_time TIMESTAMP NOT NULL, -- when they made it
    status_id INT NOT NULL DEFAULT 1, -- 1=pending, 2=confirmed, 3=cancelled etc
    total_price DECIMAL(10, 2) NOT NULL, -- sum of all seats
    paid BOOLEAN NOT NULL DEFAULT FALSE, -- payment status
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
    -- No FK for status_id cuz it's from master_data
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create seats table if it doesn't exist
CREATE TABLE IF NOT EXISTS seats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    showtime_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    is_reserved BOOLEAN NOT NULL DEFAULT FALSE,
    reservation_id BIGINT,
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    UNIQUE KEY unique_seat (showtime_id, seat_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- movie reviews from users
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL, -- reviewer
    movie_id BIGINT NOT NULL, -- movie being reviewed
    rating INT NOT NULL, -- 1-5 stars
    comment TEXT, -- optional review text
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL, -- if edited
    upvotes INT DEFAULT 0, -- counter cache
    downvotes INT DEFAULT 0, -- counter cache
    helpful_tags VARCHAR(255), -- comma-separated tags like 'funny,insightful'
    status VARCHAR(20) DEFAULT 'APPROVED', -- or PENDING, REJECTED
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci; -- todo: add index on movie_id

-- Create review_votes table if it doesn't exist
CREATE TABLE IF NOT EXISTS review_votes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_upvote BOOLEAN NOT NULL,
    voted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES reviews(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY unique_vote (review_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create user_blocks table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_blocks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    blocked_user_id BIGINT NOT NULL,
    blocked_by_id BIGINT NOT NULL,
    reason VARCHAR(255),
    blocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_admin_block BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (blocked_user_id) REFERENCES users(id),
    FOREIGN KEY (blocked_by_id) REFERENCES users(id),
    UNIQUE KEY unique_block (blocked_user_id, blocked_by_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Create payments table if it doesn't exist
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    payment_intent_id VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    receipt_url VARCHAR(255),
    pdf_receipt_path VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL DEFAULT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
