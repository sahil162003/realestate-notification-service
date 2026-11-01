
package com.sahil.notification.controller;

import com.sahil.notification.entity.Notification;
import com.sahil.notification.repository.NotificationRepository;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final CacheManager cacheManager;
    private final NotificationRepository notificationRepository;

    public NotificationController(
            NotificationRepository notificationRepository,
            CacheManager cacheManager) {
        this.notificationRepository = notificationRepository;
        this.cacheManager = cacheManager;
    }

    @GetMapping
    public List<Notification> getMyNotifications(
            Authentication authentication) {
        String email = authentication.getName();

        return notificationRepository
                .findByRecipientEmailIgnoreCaseOrderByCreatedAtDesc(email);
    }

    @GetMapping("/unread-count")
    public long getMyUnreadCount(Authentication authentication) {
        String email = authentication.getName();

        return getUnreadCountForEmail(email);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByEmail(
            @PathVariable String email,
            Authentication authentication) {

        if (!authentication.getName().equalsIgnoreCase(email)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(
                notificationRepository
                        .findByRecipientEmailIgnoreCaseOrderByCreatedAtDesc(email));
    }

    @GetMapping("/user/{email}/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable String email,
            Authentication authentication) {

        if (!authentication.getName().equalsIgnoreCase(email)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(getUnreadCountForEmail(email));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        return notificationRepository.findById(id)
                .filter(notification ->
                        notification.getRecipientEmail() != null
                        && notification.getRecipientEmail()
                                .equalsIgnoreCase(authentication.getName()))
                .map(notification -> {
                    notification.setRead(true);
                    Notification updated =
                            notificationRepository.save(notification);

                    evictUnreadCount(updated.getRecipientEmail());

                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private long getUnreadCountForEmail(String email) {
        return notificationRepository
                .countByRecipientEmailIgnoreCaseAndReadFalse(email);
    }

    private void evictUnreadCount(String email) {
        Cache cache = cacheManager.getCache("unreadCounts");

        if (cache != null && email != null) {
            cache.evict(email.toLowerCase());
        }
    }
}
