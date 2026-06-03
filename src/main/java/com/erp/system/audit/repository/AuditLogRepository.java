package com.erp.system.audit.repository;

import com.erp.system.audit.entity.AuditAction;
import com.erp.system.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityType(String entityType);
    List<AuditLog> findByEntityId(Long entityId);
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<AuditLog> findByAction(AuditAction action);
    List<AuditLog> findByUserId(Long userId);
    List<AuditLog> findByUsername(String username);

    @Query("SELECT a FROM AuditLog a WHERE a.actionTimestamp BETWEEN :startDate AND :endDate")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<AuditLog> findTop100ByOrderByActionTimestampDesc();
}
