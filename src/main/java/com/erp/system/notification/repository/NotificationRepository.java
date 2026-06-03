package com.erp.system.notification.repository;

import com.erp.system.notification.entity.Notification;
import com.erp.system.notification.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserId(Long userId);
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByPartyId(Long partyId);
}
