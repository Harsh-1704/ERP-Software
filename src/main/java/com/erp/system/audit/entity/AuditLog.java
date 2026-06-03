package com.erp.system.audit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String entityType; // 'USER', 'PARTY', 'PRODUCT', 'ORDER', etc.

    private Long entityId;
    private String entityName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    private Long userId;
    private String username;
    private String userIp;

    @Column(columnDefinition = "TEXT")
    private String userAgent;

    @Column(columnDefinition = "jsonb")
    private String oldValues;

    @Column(columnDefinition = "jsonb")
    private String newValues;

    @Column(columnDefinition = "text[]")
    private String[] changedFields;

    private String sessionId;
    private String requestUri;
    private String requestMethod;

    @Column(nullable = false)
    private LocalDateTime actionTimestamp = LocalDateTime.now();
}
