CREATE TABLE IF NOT EXISTS movies (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    language VARCHAR(50),
    genre VARCHAR(100),
    duration_minutes INT,
    release_date DATE,
    active BOOLEAN
);

CREATE TABLE IF NOT EXISTS theatres (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    city VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS shows (
    id UUID PRIMARY KEY,
    movie_id UUID,
    screen_id UUID,
    show_time TIMESTAMP,
    price DECIMAL(10,2)
);

CREATE TABLE IF NOT EXISTS screens (
    id UUID PRIMARY KEY,
    theatre_id UUID,
    name VARCHAR(100),
    total_seats INT
);

CREATE TABLE IF NOT EXISTS screen_seats (
    id UUID PRIMARY KEY,
    screen_id UUID,
    seat_number VARCHAR(10),
    seat_type VARCHAR(50),
    row_number INT
);

