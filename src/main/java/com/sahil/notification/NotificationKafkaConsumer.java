
package com.sahil.notification;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import com.sahil.notification.entity.Notification;
import com.sahil.notification.service.NotificationService;
import com.sahil.notification.service.NotificationWebSocketService;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationKafkaConsumer {

    private final NotificationService notificationService;
    private final JsonMapper objectMapper;
    private final NotificationWebSocketService webSocketService;

    public NotificationKafkaConsumer(
            NotificationService notificationService,
            JsonMapper objectMapper,
            NotificationWebSocketService webSocketService) {

        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
        this.webSocketService = webSocketService;
    }

    @KafkaListener(
        topics = "realestate-notifications",
        groupId = "realestate-notification-group"
    )
    public void consume(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);

            String recipientEmail =
                    event.path("recipientEmail").asText("");

            String eventType =
                    event.path("eventType").asText("GENERAL");

            String notificationMessage =
                    event.path("message").asText("");

            if (recipientEmail.isBlank()
                    || notificationMessage.isBlank()) {

                System.out.println(
                    "Skipping event: recipientEmail and message are required."
                );
                return;
            }

            Notification notification = new Notification();
            notification.setRecipientEmail(recipientEmail);
            notification.setEventType(eventType);
            notification.setMessage(notificationMessage);

            // Step 1: Save the notification to PostgreSQL
            Notification saved =
                    notificationService.saveNotification(notification);

            System.out.println(
                "Notification saved successfully. ID: " + saved.getId()
            );

            // Step 2: Publish the saved notification through WebSocket
            webSocketService.sendNotification(saved);

            System.out.println(
                "Notification published through WebSocket for: "
                        + saved.getRecipientEmail()
            );

        } catch (Exception e) {
            System.err.println(
                "Failed to process notification event: " + e.getMessage()
            );
        }
    }
}
