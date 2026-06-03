-- =============================================
-- V8: B2B MARKETPLACE MODULE
-- Vendor listings, bulk orders, supplier discovery
-- =============================================

-- Marketplace vendor profile (extends party)
CREATE TABLE marketplace_vendor (
    id BIGSERIAL PRIMARY KEY,
    party_id BIGINT NOT NULL REFERENCES party(id),

    -- Vendor details
    company_name VARCHAR(255) NOT NULL,
    business_type VARCHAR(100), -- MANUFACTURER, TRADER, WHOLESALER, DISTRIBUTOR
    gst_verified BOOLEAN DEFAULT FALSE,
    pan_verified BOOLEAN DEFAULT FALSE,

    -- Ratings and stats
    rating DECIMAL(3,2) DEFAULT 0,
    total_reviews INT DEFAULT 0,
    total_orders INT DEFAULT 0,
    response_rate DECIMAL(5,2) DEFAULT 0,
    response_time_hours INT,

    -- Catalog
    total_products INT DEFAULT 0,
    featured_products INT DEFAULT 0,

    -- Subscription
    subscription_plan VARCHAR(50) DEFAULT 'FREE', -- FREE, BASIC, PREMIUM, ENTERPRISE
    subscription_start_date DATE,
    subscription_end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    is_verified BOOLEAN DEFAULT FALSE,
    is_featured BOOLEAN DEFAULT FALSE,

    -- Contact
    website_url VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Product listing for marketplace (extends product)
CREATE TABLE marketplace_listing (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    vendor_id BIGINT NOT NULL REFERENCES marketplace_vendor(id),

    -- Listing details
    title VARCHAR(255) NOT NULL,
    description TEXT,
    short_description VARCHAR(500),

    -- Pricing (B2B specific)
    base_price DECIMAL(10,2) NOT NULL,
    min_order_quantity DECIMAL(15,3) DEFAULT 1,
    max_order_quantity DECIMAL(15,3),

    -- Bulk pricing tiers (stored as JSON for flexibility)
    bulk_pricing JSONB, -- [{"min_qty": 100, "max_qty": 499, "price": 95.00}, {"min_qty": 500, "price": 90.00}]

    -- Availability
    is_available BOOLEAN DEFAULT TRUE,
    availability_status VARCHAR(50) DEFAULT 'IN_STOCK', -- IN_STOCK, OUT_OF_STOCK, MADE_TO_ORDER
    lead_time_days INT DEFAULT 7,
    moq_negotiable BOOLEAN DEFAULT FALSE,

    -- Shipping
    shipping_available_states JSONB, -- Array of state codes
    free_shipping_threshold DECIMAL(10,2),
    shipping_charges DECIMAL(10,2) DEFAULT 0,

    -- Visibility
    is_featured BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    views_count INT DEFAULT 0,
    inquiries_count INT DEFAULT 0,

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bulk pricing tier (alternative to JSONB storage)
CREATE TABLE bulk_price_tier (
    id BIGSERIAL PRIMARY KEY,
    listing_id BIGINT NOT NULL REFERENCES marketplace_listing(id) ON DELETE CASCADE,
    min_quantity DECIMAL(15,3) NOT NULL,
    max_quantity DECIMAL(15,3),
    unit_price DECIMAL(10,2) NOT NULL,
    discount_percentage DECIMAL(5,2) DEFAULT 0
);

-- Product images for marketplace
CREATE TABLE product_image (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES product(id),
    listing_id BIGINT REFERENCES marketplace_listing(id),
    image_url VARCHAR(500) NOT NULL,
    image_type VARCHAR(50) DEFAULT 'PRIMARY', -- PRIMARY, GALLERY, THUMBNAIL
    display_order INT DEFAULT 0,
    alt_text VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Inquiry/RFQ (Request for Quotation)
CREATE TYPE inquiry_status AS ENUM ('PENDING', 'QUOTED', 'NEGOTIATING', 'ORDERED', 'CANCELLED', 'EXPIRED');

CREATE TABLE product_inquiry (
    id BIGSERIAL PRIMARY KEY,
    inquiry_number VARCHAR(50) UNIQUE NOT NULL,
    listing_id BIGINT NOT NULL REFERENCES marketplace_listing(id),

    -- Inquirer
    inquirer_party_id BIGINT NOT NULL REFERENCES party(id),
    contact_person VARCHAR(255),
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),

    -- Inquiry details
    required_quantity DECIMAL(15,3),
    target_price DECIMAL(10,2),
    delivery_location VARCHAR(255),
    expected_delivery_date DATE,

    -- Status
    status inquiry_status DEFAULT 'PENDING',

    -- Vendor response
    quoted_price DECIMAL(10,2),
    quoted_at TIMESTAMP,
    quote_valid_until DATE,
    vendor_notes TEXT,

    -- Additional
    requirements TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bulk order (marketplace specific)
CREATE TYPE bulk_order_status AS ENUM (
    'PENDING_CONFIRMATION',
    'CONFIRMED',
    'PAYMENT_PENDING',
    'PAYMENT_RECEIVED',
    'PROCESSING',
    'SHIPPED',
    'DELIVERED',
    'PARTIALLY_DELIVERED',
    'CANCELLED',
    'DISPUTED'
);

CREATE TABLE bulk_order (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    listing_id BIGINT NOT NULL REFERENCES marketplace_listing(id),

    -- Buyer
    buyer_party_id BIGINT NOT NULL REFERENCES party(id),
    billing_address_id BIGINT REFERENCES address(id),
    shipping_address_id BIGINT REFERENCES address(id),

    -- Order details
    quantity DECIMAL(15,3) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,

    -- Pricing breakdown
    subtotal DECIMAL(15,2) NOT NULL,
    tax_amount DECIMAL(15,2) NOT NULL,
    shipping_charges DECIMAL(15,2) DEFAULT 0,
    discount_amount DECIMAL(15,2) DEFAULT 0,

    -- Status
    status bulk_order_status DEFAULT 'PENDING_CONFIRMATION',

    -- Payment
    payment_status VARCHAR(50) DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    paid_amount DECIMAL(15,2) DEFAULT 0,

    -- Delivery
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    tracking_number VARCHAR(100),

    -- Additional
    buyer_remarks TEXT,
    vendor_notes TEXT,

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    shipped_at TIMESTAMP,
    delivered_at TIMESTAMP,
    cancelled_at TIMESTAMP
);

-- Vendor reviews and ratings
CREATE TABLE vendor_review (
    id BIGSERIAL PRIMARY KEY,
    vendor_id BIGINT NOT NULL REFERENCES marketplace_vendor(id),
    reviewer_party_id BIGINT NOT NULL REFERENCES party(id),
    order_id BIGINT REFERENCES bulk_order(id),

    -- Ratings (1-5)
    overall_rating INT NOT NULL CHECK (overall_rating BETWEEN 1 AND 5),
    product_quality_rating INT CHECK (product_quality_rating BETWEEN 1 AND 5),
    delivery_rating INT CHECK (delivery_rating BETWEEN 1 AND 5),
    communication_rating INT CHECK (communication_rating BETWEEN 1 AND 5),

    -- Review
    review_text TEXT,
    is_verified_purchase BOOLEAN DEFAULT FALSE,

    -- Vendor response
    vendor_response TEXT,
    vendor_response_at TIMESTAMP,

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_marketplace_vendor_party ON marketplace_vendor(party_id);
CREATE INDEX idx_marketplace_listing_vendor ON marketplace_listing(vendor_id);
CREATE INDEX idx_marketplace_listing_product ON marketplace_listing(product_id);
CREATE INDEX idx_marketplace_listing_active ON marketplace_listing(is_active, is_featured);
CREATE INDEX idx_product_inquiry_listing ON product_inquiry(listing_id);
CREATE INDEX idx_bulk_order_listing ON bulk_order(listing_id);
CREATE INDEX idx_bulk_order_buyer ON bulk_order(buyer_party_id);
CREATE INDEX idx_vendor_review_vendor ON vendor_review(vendor_id);