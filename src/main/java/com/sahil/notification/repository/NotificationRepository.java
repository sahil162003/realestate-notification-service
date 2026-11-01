
package com.sahil.notification.repository;

import com.sahil.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientEmailIgnoreCaseOrderByCreatedAtDesc(
            String recipientEmail);

    long countByRecipientEmailIgnoreCaseAndReadFalse(
            String recipientEmail);
}
