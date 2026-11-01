
		package com.sahil.notification.entity;

		import jakarta.persistence.Column;
		import jakarta.persistence.Entity;
		import jakarta.persistence.GeneratedValue;
		import jakarta.persistence.GenerationType;
		import jakarta.persistence.Id;
		import jakarta.persistence.Table;

		import java.time.LocalDateTime;

		@Entity
		@Table(name = "notifications")
		public class Notification {

		    @Id
		    @GeneratedValue(strategy = GenerationType.IDENTITY)
		    private Long id;

		    @Column(nullable = false)
		    private String recipientEmail;

		    @Column(nullable = false)
		    private String eventType;

		    @Column(nullable = false, length = 2000)
		    private String message;

		    @Column(nullable = false)
		    private boolean read = false;

		    @Column(nullable = false)
		    private LocalDateTime createdAt = LocalDateTime.now();

		    public Notification() {
		    }

		    public Long getId() {
		        return id;
		    }

		    public String getRecipientEmail() {
		        return recipientEmail;
		    }

		    public void setRecipientEmail(String recipientEmail) {
		        this.recipientEmail = recipientEmail;
		    }

		    public String getEventType() {
		        return eventType;
		    }

		    public void setEventType(String eventType) {
		        this.eventType = eventType;
		    }

		    public String getMessage() {
		        return message;
		    }

		    public void setMessage(String message) {
		        this.message = message;
		    }

		    public boolean isRead() {
		        return read;
		    }

		    public void setRead(boolean read) {
		        this.read = read;
		    }

		    public LocalDateTime getCreatedAt() {
		        return createdAt;
		    }
		}
