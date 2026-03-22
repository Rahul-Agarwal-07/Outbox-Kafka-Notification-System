package com.cleanarch.domain.model;

import java.util.UUID;

public class Notification {

    private UUID eventId;
    private String recipient;
    private NotificationStatus status;
    private NotificationType type;

}
