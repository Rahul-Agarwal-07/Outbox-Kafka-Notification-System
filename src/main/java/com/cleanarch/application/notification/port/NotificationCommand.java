package com.cleanarch.application.notification.port;

import com.cleanarch.domain.model.NotificationType;

import java.util.UUID;

public record NotificationCommand(
        UUID eventId,
        NotificationType type,
        String recipient
) {
}
