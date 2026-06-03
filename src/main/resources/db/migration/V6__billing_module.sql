-- =============================================
-- V6: BILLING MODULE
-- Invoices, invoice items, payments
-- =============================================

-- Invoice types
CREATE TYPE invoice_type AS ENUM ('SALES', 'PURCHASE', 'CREDIT_NOTE', 'DEBIT_NOTE');

-- Invoice status
CREATE TYPE invoice_status AS ENUM ('DRAFT', 'CONFIRMED', 'SENT', 'PARTIAL_PAID', 'PAID', 'OVERDUE', 'CANCELLED');

-- Invoice table
CREATE TABLE invoice (
    id BIGSERIAL PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    invoice_type invoice_type NOT NULL,
    status invoice_status DEFAULT 'DRAFT',

    -- Party references
    party_id BIGINT NOT NULL REFERENCES party(id),
    billing_address_id BIGINT REFERENCES address(id),
    shipping_address_id BIGINT REFERENCES address(id),

    -- Financial details
    subtotal DECIMAL(15,2) NOT NULL DEFAULT 0,
    total_tax DECIMAL(15,2) NOT NULL DEFAULT 0,
    tax_breakup JSONB, -- Store CGST/SGST/IGST breakdown
    discount_amount DECIMAL(15,2) DEFAULT 0,
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    shipping_charges DECIMAL(15,2) DEFAULT 0,
    other_charges DECIMAL(15,2) DEFAULT 0,
    round_off DECIMAL(10,2) DEFAULT 0,
    total_amount DECIMAL(15,2) NOT NULL,

    -- Payment tracking
    paid_amount DECIMAL(15,2) DEFAULT 0,
    balance_amount DECIMAL(15,2) NOT NULL,

    -- Dates
    invoice_date DATE NOT NULL,
    due_date DATE,
    payment_terms_days INT DEFAULT 30,

    -- References
    sales_order_id BIGINT,
    purchase_order_id BIGINT,
    delivery_challan_id BIGINT,

    -- Additional
    remarks TEXT,
    terms_and_conditions TEXT,
    shipping_marks TEXT,

    -- Audit
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancelled_reason TEXT
);

-- Invoice items table
CREATE TABLE invoice_item (
    id BIGSERIAL PRIMARY KEY,
    invoice_id BIGINT NOT NULL REFERENCES invoice(id) ON DELETE CASCADE,
    item_number INT NOT NULL,
    product_id BIGINT NOT NULL REFERENCES product(id),

    -- Product details snapshot (in case product changes later)
    product_name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    hsn_code VARCHAR(50),

    -- Quantity and pricing
    quantity DECIMAL(15,3) NOT NULL,
    unit_id BIGINT REFERENCES unit(id),
    unit_price DECIMAL(10,2) NOT NULL,

    -- Discounts
    discount_percentage DECIMAL(5,2) DEFAULT 0,
    discount_amount DECIMAL(10,2) DEFAULT 0,

    -- Tax
    tax_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    tax_amount DECIMAL(10,2) NOT NULL,
    cgst_rate DECIMAL(5,2) DEFAULT 0,
    cgst_amount DECIMAL(10,2) DEFAULT 0,
    sgst_rate DECIMAL(5,2) DEFAULT 0,
    sgst_amount DECIMAL(10,2) DEFAULT 0,
    igst_rate DECIMAL(5,2) DEFAULT 0,
    igst_amount DECIMAL(10,2) DEFAULT 0,

    -- Totals
    subtotal DECIMAL(15,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,

    -- Additional
    remarks TEXT
);

-- Payment modes
CREATE TYPE payment_mode AS ENUM ('CASH', 'CHEQUE', 'BANK_TRANSFER', 'UPI', 'CARD', 'CREDIT', 'DEBIT', 'OTHER');

-- Payment status
CREATE TYPE payment_status AS ENUM ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED');

-- Payments table
CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    payment_number VARCHAR(50) UNIQUE NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- References
    invoice_id BIGINT NOT NULL REFERENCES invoice(id),
    party_id BIGINT NOT NULL REFERENCES party(id),

    -- Payment details
    amount DECIMAL(15,2) NOT NULL,
    payment_mode payment_mode NOT NULL,
    payment_status payment_status DEFAULT 'PENDING',

    -- Bank/Cheque details
    bank_name VARCHAR(255),
    account_number VARCHAR(50),
    ifsc_code VARCHAR(20),
    cheque_number VARCHAR(50),
    cheque_date DATE,
    transaction_id VARCHAR(100),
    reference_number VARCHAR(100),

    -- Additional
    remarks TEXT,
    received_by VARCHAR(100),

    -- Audit
    created_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Payment allocation (for partial payments across invoices)
CREATE TABLE payment_allocation (
    id BIGSERIAL PRIMARY KEY,
    payment_id BIGINT NOT NULL REFERENCES payment(id),
    invoice_id BIGINT NOT NULL REFERENCES invoice(id),
    allocated_amount DECIMAL(15,2) NOT NULL,
    allocated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_invoice_party ON invoice(party_id);
CREATE INDEX idx_invoice_status ON invoice(status);
CREATE INDEX idx_invoice_date ON invoice(invoice_date);
CREATE INDEX idx_invoice_number ON invoice(invoice_number);
CREATE INDEX idx_invoice_item_invoice ON invoice_item(invoice_id);
CREATE INDEX idx_invoice_item_product ON invoice_item(product_id);
CREATE INDEX idx_payment_invoice ON payment(invoice_id);
CREATE INDEX idx_payment_party ON payment(party_id);
CREATE INDEX idx_payment_date ON payment(payment_date);