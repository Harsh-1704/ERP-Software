CREATE TABLE roles (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       username VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role_id BIGINT REFERENCES roles(id),
                       active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);