-- =============================================
-- V5: INVENTORY MODULE
-- Warehouse management, stock tracking, movements
-- =============================================

-- Warehouse table
CREATE TABLE warehouse (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(100) DEFAULT 'India',
    pincode VARCHAR(20),
    contact_person VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    capacity_units INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock/Inventory table - tracks current stock per product per warehouse
CREATE TABLE stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    quantity_on_hand DECIMAL(15,3) DEFAULT 0,
    quantity_reserved DECIMAL(15,3) DEFAULT 0,
    quantity_available DECIMAL(15,3) DEFAULT 0,
    reorder_level DECIMAL(15,3) DEFAULT 0,
    last_stock_check TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, warehouse_id)
);

-- Stock movement types: IN, OUT, TRANSFER_IN, TRANSFER_OUT, ADJUSTMENT, RETURN
CREATE TYPE stock_movement_type AS ENUM (
    'PURCHASE_IN',
    'SALES_OUT',
    'TRANSFER_IN',
    'TRANSFER_OUT',
    'ADJUSTMENT_IN',
    'ADJUSTMENT_OUT',
    'RETURN_IN',
    'RETURN_OUT',
    'MANUFACTURING_IN',
    'MANUFACTURING_OUT'
);

-- Stock movement table - tracks all stock transactions
CREATE TABLE stock_movement (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    movement_type stock_movement_type NOT NULL,
    quantity DECIMAL(15,3) NOT NULL,
    unit_price DECIMAL(10,2),
    total_value DECIMAL(15,2),
    reference_type VARCHAR(50), -- 'PURCHASE_ORDER', 'SALES_ORDER', 'TRANSFER', etc.
    reference_id BIGINT,
    from_warehouse_id BIGINT REFERENCES warehouse(id),
    to_warehouse_id BIGINT REFERENCES warehouse(id),
    remarks TEXT,
    movement_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock adjustment table - for manual corrections
CREATE TABLE stock_adjustment (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    adjustment_number VARCHAR(50) UNIQUE NOT NULL,
    adjustment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(255),
    remarks TEXT,
    status VARCHAR(50) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock adjustment items
CREATE TABLE stock_adjustment_item (
    id BIGSERIAL PRIMARY KEY,
    adjustment_id BIGINT NOT NULL REFERENCES stock_adjustment(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    current_quantity DECIMAL(15,3) NOT NULL,
    adjusted_quantity DECIMAL(15,3) NOT NULL,
    difference_quantity DECIMAL(15,3) NOT NULL,
    reason VARCHAR(255),
    remarks TEXT
);

-- Stock transfer between warehouses
CREATE TABLE stock_transfer (
    id BIGSERIAL PRIMARY KEY,
    transfer_number VARCHAR(50) UNIQUE NOT NULL,
    from_warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    to_warehouse_id BIGINT NOT NULL REFERENCES warehouse(id),
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expected_arrival_date TIMESTAMP,
    actual_arrival_date TIMESTAMP,
    status VARCHAR(50) DEFAULT 'IN_TRANSIT', -- INITIATED, IN_TRANSIT, RECEIVED, CANCELLED
    remarks TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stock transfer items
CREATE TABLE stock_transfer_item (
    id BIGSERIAL PRIMARY KEY,
    transfer_id BIGINT NOT NULL REFERENCES stock_transfer(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    quantity DECIMAL(15,3) NOT NULL,
    quantity_received DECIMAL(15,3),
    remarks TEXT
);

-- Create indexes for performance
CREATE INDEX idx_stock_product ON stock(product_id);
CREATE INDEX idx_stock_warehouse ON stock(warehouse_id);
CREATE INDEX idx_stock_movement_product ON stock_movement(product_id);
CREATE INDEX idx_stock_movement_warehouse ON stock_movement(warehouse_id);
CREATE INDEX idx_stock_movement_date ON stock_movement(movement_date);
CREATE INDEX idx_stock_movement_reference ON stock_movement(reference_type, reference_id);