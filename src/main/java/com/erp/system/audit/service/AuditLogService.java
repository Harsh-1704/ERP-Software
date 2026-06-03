package com.erp.system.audit.service;

import com.erp.system.audit.entity.AuditAction;
import com.erp.system.audit.entity.AuditLog;
import com.erp.system.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog logAction(AuditLog auditLog) {
        auditLog.setActionTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getEntityAuditTrail(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    public List<AuditLog> getUserActivity(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    public List<AuditLog> getRecentActivity() {
        return auditLogRepository.findTop100ByOrderByActionTimestampDesc();
    }

    public List<AuditLog> getActivityByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate);
    }
}
