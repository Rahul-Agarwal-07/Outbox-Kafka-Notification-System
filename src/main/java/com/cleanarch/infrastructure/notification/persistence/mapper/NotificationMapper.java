package com.cleanarch.infrastructure.notification.persistence.mapper;

import com.cleanarch.domain.model.Notification;
import com.cleanarch.infrastructure.notification.persistence.entity.NotificationEntity;

import java.time.Instant;

public class NotificationMapper {

    public static NotificationEntity toEntity(Notification notification)
    {
        return new NotificationEntity(
            notification.getEventId(),
            notification.getType().name(),
            notification.getStatus().name(),
            notification.getRecipient(),
            Instant.now(),
            null,
            null
        );
    }
}
