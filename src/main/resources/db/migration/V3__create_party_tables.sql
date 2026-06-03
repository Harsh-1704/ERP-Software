CREATE TABLE party (
                       id BIGSERIAL PRIMARY KEY,
                       party_name VARCHAR(255) NOT NULL,
                       legal_name VARCHAR(255),
                       gst_number VARCHAR(50),
                       pan_number VARCHAR(50),
                       status VARCHAR(50),
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE party_type (
                            id BIGSERIAL PRIMARY KEY,
                            type_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE party_role (
                            id BIGSERIAL PRIMARY KEY,
                            party_id BIGINT REFERENCES party(id),
                            party_type_id BIGINT REFERENCES party_type(id)
);

CREATE TABLE address (
                         id BIGSERIAL PRIMARY KEY,
                         party_id BIGINT REFERENCES party(id),
                         address_type VARCHAR(50),
                         street VARCHAR(255),
                         city VARCHAR(100),
                         state VARCHAR(100),
                         country VARCHAR(100),
                         pincode VARCHAR(20)
);

CREATE TABLE contact (
                         id BIGSERIAL PRIMARY KEY,
                         party_id BIGINT REFERENCES party(id),
                         contact_name VARCHAR(255),
                         email VARCHAR(255),
                         phone VARCHAR(20),
                         designation VARCHAR(100)
);