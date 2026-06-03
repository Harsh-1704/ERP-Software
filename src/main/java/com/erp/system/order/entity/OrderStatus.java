package com.erp.system.order.entity;

public enum OrderStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    CONFIRMED,
    PROCESSING,
    PARTIALLY_FULFILLED,
    FULFILLED,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    RETURNED
}
