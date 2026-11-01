
package com.sahil.notification.service;

import com.sahil.notification.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationWebSocketService(
            SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendNotification(Notification notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/" +
                        notification.getRecipientEmail().toLowerCase(),
                notification
        );
    }
}
