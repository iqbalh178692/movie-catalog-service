CREATE TABLE movies (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    language VARCHAR(50),
    genre VARCHAR(100),
    duration_minutes INT,
    release_date DATE,
    active BOOLEAN
);

CREATE TABLE theatres (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    city VARCHAR(100)
);

CREATE TABLE shows (
    id UUID PRIMARY KEY,
    movie_id UUID,
    theatre_id UUID,
    show_time TIMESTAMP,
    price DECIMAL(10,2)
);
