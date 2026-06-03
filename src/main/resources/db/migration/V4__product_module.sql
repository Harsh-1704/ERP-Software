-- Product Module Tables
CREATE TABLE product_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id INT
);

CREATE TABLE unit (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    symbol VARCHAR(10)
);

CREATE TABLE product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    category_id INT REFERENCES product_category(id),
    unit_id INT REFERENCES unit(id),
    sku VARCHAR(100) UNIQUE,
    barcode VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product_price (
    id BIGSERIAL PRIMARY KEY,
    product_id INT REFERENCES product(id),
    price DECIMAL(10,2) NOT NULL,
    effective_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
