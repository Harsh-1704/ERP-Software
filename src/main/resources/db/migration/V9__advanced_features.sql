-- =============================================
-- V9: ADVANCED FEATURES
-- Audit logs, notifications, reporting
-- =============================================

-- Entity types for audit
CREATE TYPE audit_action AS ENUM ('CREATE', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'EXPORT', 'IMPORT', 'APPROVE', 'REJECT');

-- Audit log table
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL, -- 'USER', 'PARTY', 'PRODUCT', 'ORDER', etc.
    entity_id BIGINT,
    entity_name VARCHAR(255), -- Human readable name

    action audit_action NOT NULL,

    -- User info
    user_id BIGINT REFERENCES users(id),
    username VARCHAR(100),
    user_ip VARCHAR(50),
    user_agent TEXT,

    -- Changes (JSON format for flexibility)
    old_values JSONB,
    new_values JSONB,
    changed_fields TEXT[], -- Array of field names that changed

    -- Additional context
    session_id VARCHAR(100),
    request_uri VARCHAR(255),
    request_method VARCHAR(20),

    -- Timestamp
    action_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification types
CREATE TYPE notification_type AS ENUM (
    'INFO',
    'SUCCESS',
    'WARNING',
    'ERROR',
    'ORDER_UPDATE',
    'PAYMENT_REMINDER',
    'STOCK_ALERT',
    'APPROVAL_REQUEST',
    'SYSTEM_ALERT'
);

-- Notification delivery channels
CREATE TYPE notification_channel AS ENUM ('IN_APP', 'EMAIL', 'SMS', 'PUSH');

-- Notification status
CREATE TYPE notification_status AS ENUM ('PENDING', 'SENT', 'DELIVERED', 'READ', 'FAILED');

-- Notifications table
CREATE TABLE notification (
    id BIGSERIAL PRIMARY KEY,
    notification_type notification_type NOT NULL,

    -- Recipient
    user_id BIGINT REFERENCES users(id),
    party_id BIGINT REFERENCES party(id),

    -- Content
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(500),
    action_text VARCHAR(100),

    -- Delivery
    channel notification_channel DEFAULT 'IN_APP',
    status notification_status DEFAULT 'PENDING',

    -- Scheduling
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,

    -- Retry
    retry_count INT DEFAULT 0,
    last_retry_at TIMESTAMP,
    error_message TEXT,

    -- Metadata
    metadata JSONB,

    -- Audit
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Notification preferences per user
CREATE TABLE notification_preference (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    notification_type notification_type NOT NULL,
    channel notification_channel NOT NULL,
    is_enabled BOOLEAN DEFAULT TRUE,
    UNIQUE(user_id, notification_type, channel)
);

-- Stock alert thresholds
CREATE TABLE stock_alert (
    id BIGSERIAL PRIMARY KEY,
    stock_id BIGINT NOT NULL REFERENCES stock(id),
    alert_type VARCHAR(50) NOT NULL, -- 'LOW_STOCK', 'OUT_OF_STOCK', 'OVERSTOCK'
    threshold_value DECIMAL(15,3),
    current_value DECIMAL(15,3),
    is_active BOOLEAN DEFAULT TRUE,
    triggered_at TIMESTAMP,
    acknowledged_at TIMESTAMP,
    acknowledged_by VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Report definitions (for dynamic reporting)
CREATE TYPE report_type AS ENUM ('SALES', 'PURCHASE', 'INVENTORY', 'FINANCIAL', 'CUSTOMER', 'VENDOR', 'CUSTOM');
CREATE TYPE report_format AS ENUM ('PDF', 'EXCEL', 'CSV', 'HTML');
CREATE TYPE report_frequency AS ENUM ('ONCE', 'DAILY', 'WEEKLY', 'MONTHLY', 'QUARTERLY', 'YEARLY');

CREATE TABLE report_definition (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    report_type report_type NOT NULL,
    description TEXT,

    -- Query configuration
    query_template TEXT NOT NULL,
    parameters JSONB, -- Expected parameters with types

    -- Output
    columns JSONB NOT NULL, -- Column definitions
    default_format report_format DEFAULT 'EXCEL',

    -- Scheduling
    default_frequency report_frequency DEFAULT 'ONCE',
    is_scheduled BOOLEAN DEFAULT FALSE,
    schedule_cron VARCHAR(100),

    -- Access
    created_by BIGINT REFERENCES users(id),
    is_public BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Generated reports
CREATE TABLE generated_report (
    id BIGSERIAL PRIMARY KEY,
    report_definition_id BIGINT REFERENCES report_definition(id),
    report_name VARCHAR(255) NOT NULL,
    generated_by BIGINT REFERENCES users(id),

    -- Parameters used
    parameters JSONB,

    -- Output
    format report_format NOT NULL,
    file_path VARCHAR(500),
    file_size_bytes BIGINT,

    -- Metadata
    record_count INT,
    date_from DATE,
    date_to DATE,

    -- Status
    status VARCHAR(50) DEFAULT 'GENERATING', -- GENERATING, COMPLETED, FAILED
    error_message TEXT,

    -- Timestamps
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    downloaded_at TIMESTAMP
);

-- Dashboard widgets configuration
CREATE TABLE dashboard_widget (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    widget_type VARCHAR(100) NOT NULL,
    widget_name VARCHAR(255) NOT NULL,

    -- Configuration
    position_x INT DEFAULT 0,
    position_y INT DEFAULT 0,
    width INT DEFAULT 4,
    height INT DEFAULT 2,

    -- Settings
    configuration JSONB,
    refresh_interval_seconds INT DEFAULT 300,

    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- System settings/key-value store
CREATE TABLE system_setting (
    id BIGSERIAL PRIMARY KEY,
    setting_key VARCHAR(100) UNIQUE NOT NULL,
    setting_value TEXT,
    value_type VARCHAR(50) DEFAULT 'STRING', -- STRING, NUMBER, BOOLEAN, JSON
    description TEXT,
    category VARCHAR(100),
    is_editable BOOLEAN DEFAULT TRUE,
    updated_by VARCHAR(100),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_audit_log_entity ON audit_log(entity_type, entity_id);
CREATE INDEX idx_audit_log_user ON audit_log(user_id);
CREATE INDEX idx_audit_log_timestamp ON audit_log(action_timestamp);
CREATE INDEX idx_notification_user ON notification(user_id);
CREATE INDEX idx_notification_status ON notification(status);
CREATE INDEX idx_stock_alert_active ON stock_alert(is_active);
CREATE INDEX idx_generated_report_user ON generated_report(generated_by);

-- Insert default system settings
INSERT INTO system_setting (setting_key, setting_value, value_type, description, category) VALUES
('company.name', 'ERP Company', 'STRING', 'Company name for invoices', 'GENERAL'),
('company.gstin', '', 'STRING', 'Company GST number', 'GENERAL'),
('company.pan', '', 'STRING', 'Company PAN number', 'GENERAL'),
('company.address', '', 'STRING', 'Company registered address', 'GENERAL'),
('invoice.prefix', 'INV', 'STRING', 'Invoice number prefix', 'BILLING'),
('invoice.starting_number', '1000', 'NUMBER', 'Starting invoice number', 'BILLING'),
('order.prefix', 'ORD', 'STRING', 'Order number prefix', 'ORDER'),
('payment.grace_period_days', '7', 'NUMBER', 'Grace period for payments', 'BILLING'),
('stock.low_stock_threshold', '10', 'NUMBER', 'Default low stock threshold', 'INVENTORY'),
('notification.email_enabled', 'false', 'BOOLEAN', 'Enable email notifications', 'NOTIFICATION'),
('notification.sms_enabled', 'false', 'BOOLEAN', 'Enable SMS notifications', 'NOTIFICATION');