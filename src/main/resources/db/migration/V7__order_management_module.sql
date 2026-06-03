-- =============================================
-- V7: ORDER MANAGEMENT MODULE
-- Sales orders, purchase orders, order lifecycle
-- =============================================

-- Order types
CREATE TYPE order_type AS ENUM ('SALES', 'PURCHASE');

-- Order status
CREATE TYPE order_status AS ENUM (
    'DRAFT',
    'PENDING_APPROVAL',
    'APPROVED',
    'CONFIRMED',
    'PROCESSING',
    'PARTIALLY_FULFILLED',
    'FULFILLED',
    'SHIPPED',
    'DELIVERED',
    'CANCELLED',
    'RETURNED'
);

-- Order table (base for both sales and purchase orders)
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    order_type order_type NOT NULL,
    status order_status DEFAULT 'DRAFT',

    -- Party references
    party_id BIGINT NOT NULL REFERENCES party(id),
    billing_address_id BIGINT REFERENCES address(id),
    shipping_address_id BIGINT REFERENCES address(id),

    -- Contact references
    contact_person_id BIGINT REFERENCES contact(id),
    sales_person_id BIGINT REFERENCES users(id),

    -- Financial details
    subtotal DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_tax DECIMAL(15,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    shipping_charges DECIMAL(15,2) DEFAULT 0,
    other_charges DECIMAL(15,2) DEFAULT 0,
    round_off DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(15,2) NOT NULL,

    -- Dates
    order_date DATE NOT NULL,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    payment_terms_days INT DEFAULT 30,

    -- Shipping
    shipping_method VARCHAR(100),
    tracking_number VARCHAR(100),
    courier_name VARCHAR(255),

    -- Additional
    remarks TEXT,
    terms_and_conditions TEXT,
    internal_notes TEXT,

    -- Approval
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    approval_remarks TEXT,

    -- Audit
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancelled_reason TEXT
);

-- Order items table
CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    item_number INT NOT NULL,
    product_id BIGINT NOT NULL REFERENCES product(id),

    -- Product details snapshot
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    hsn_code VARCHAR(50),

    -- Quantity
    ordered_quantity DECIMAL(15,3) NOT NULL,
    confirmed_quantity DECIMAL(15,3),
    shipped_quantity DECIMAL(15,3),
    delivered_quantity DECIMAL(15,3),
    returned_quantity DECIMAL(15,3),
    cancelled_quantity DECIMAL(15,3),

    -- Unit and pricing
    unit_id BIGINT REFERENCES unit(id),
    unit_price DECIMAL(10,2) NOT NULL,

    -- Discounts
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,

    -- Tax
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(10,2) NOT NULL,

    -- Totals
    subtotal DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,

    -- Delivery
    expected_delivery_date DATE,

    -- Additional
    remarks TEXT
);

-- Order status history for tracking
CREATE TABLE order_status_history (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    from_status order_status,
    to_status order_status NOT NULL,
    changed_by VARCHAR(100),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT
);

-- Delivery challan/dispatch
CREATE TABLE delivery_challan (
    id BIGSERIAL PRIMARY KEY,
    challan_number VARCHAR(50) UNIQUE NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(id),
    invoice_id BIGINT REFERENCES invoice(id),
    dispatch_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    vehicle_number VARCHAR(20),
    transporter_name VARCHAR(255),
    driver_name VARCHAR(255),
    driver_phone VARCHAR(20),
    lr_number VARCHAR(50), -- Lorry Receipt Number
    status VARCHAR(50) DEFAULT 'DISPATCHED', -- DISPATCHED, IN_TRANSIT, DELIVERED, RETURNED
    delivered_at TIMESTAMP,
    remarks TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Delivery challan items
CREATE TABLE delivery_challan_item (
    id BIGSERIAL PRIMARY KEY,
    challan_id BIGINT NOT NULL REFERENCES delivery_challan(id),
    order_item_id BIGINT NOT NULL REFERENCES order_item(id),
    product_id BIGINT NOT NULL REFERENCES product(id),
    dispatched_quantity DECIMAL(15,3) NOT NULL,
    delivered_quantity DECIMAL(15,3),
    damaged_quantity DECIMAL(15,3) DEFAULT 0,
    remarks TEXT
);

-- Create indexes
CREATE INDEX idx_orders_party ON orders(party_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_product ON order_item(product_id);
CREATE INDEX idx_order_status_history ON order_status_history(order_id);
CREATE INDEX idx_delivery_challan_order ON delivery_challan(order_id);