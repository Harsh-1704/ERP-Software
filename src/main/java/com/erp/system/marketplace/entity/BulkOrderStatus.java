package com.erp.system.marketplace.entity;

public enum BulkOrderStatus {
    PENDING_CONFIRMATION,
    CONFIRMED,
    PAYMENT_PENDING,
    PAYMENT_RECEIVED,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    PARTIALLY_DELIVERED,
    CANCELLED,
    DISPUTED
}
