package com.cleanarch.domain.model;

import java.util.UUID;

public class Notification {

    private UUID eventId;
    private String recipient;
    private NotificationStatus status;
    private NotificationType type;

    public Notification(UUID eventId, String recipient, NotificationStatus status, NotificationType type) {
        this.eventId = eventId;
        this.recipient = recipient;
        this.status = status;
        this.type = type;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getRecipient() {
        return recipient;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public NotificationType getType() {
        return type;
    }
}
