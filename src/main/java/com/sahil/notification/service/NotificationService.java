
package com.sahil.notification.service;

import com.sahil.notification.entity.Notification;
import com.sahil.notification.repository.NotificationRepository;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;

    public NotificationService(
            NotificationRepository notificationRepository,
            CacheManager cacheManager) {
        this.notificationRepository = notificationRepository;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public Notification saveNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);

        evictUnreadCount(saved.getRecipientEmail());

        return saved;
    }

    private void evictUnreadCount(String email) {
        Cache cache = cacheManager.getCache("unreadCounts");

        if (cache != null && email != null) {
            cache.evict(email.toLowerCase());
        }
    }
}
