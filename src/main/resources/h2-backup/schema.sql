-- Create component_types table if it doesn't exist
CREATE TABLE IF NOT EXISTS component_types (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create master_data table if it doesn't exist
CREATE TABLE IF NOT EXISTS master_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    master_data_id INT NOT NULL,
    data_value VARCHAR(100) NOT NULL,
    component_type_id INT NOT NULL,
    FOREIGN KEY (component_type_id) REFERENCES component_types(id),
    UNIQUE KEY unique_master_data (component_type_id, master_data_id)
);

-- Create roles table if it doesn't exist
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Create users table if it doesn't exist
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Create movies table if it doesn't exist
CREATE TABLE IF NOT EXISTS movies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    release_year INT,
    genre VARCHAR(100),
    poster_image_url VARCHAR(255)
);

-- Create theaters table if it doesn't exist
CREATE TABLE IF NOT EXISTS theaters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    capacity INT NOT NULL
);

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
);

-- Create reservations table if it doesn't exist
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    showtime_id BIGINT NOT NULL,
    reservation_time TIMESTAMP NOT NULL,
    status_id INT NOT NULL DEFAULT 1,
    total_price DECIMAL(10, 2) NOT NULL,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (showtime_id) REFERENCES showtimes(id)
    -- No foreign key constraint for status_id to allow flexibility
);

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
);

-- Create reviews table if it doesn't exist
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    upvotes INT DEFAULT 0,
    downvotes INT DEFAULT 0,
    helpful_tags VARCHAR(255),
    status VARCHAR(20) DEFAULT 'APPROVED',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

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
);

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
);

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
    updated_at TIMESTAMP,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);
