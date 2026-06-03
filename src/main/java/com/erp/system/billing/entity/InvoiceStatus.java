package com.erp.system.billing.entity;

public enum InvoiceStatus {
    DRAFT,
    CONFIRMED,
    SENT,
    PARTIAL_PAID,
    PAID,
    OVERDUE,
    CANCELLED
}