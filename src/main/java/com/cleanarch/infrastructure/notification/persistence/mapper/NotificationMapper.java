package com.cleanarch.infrastructure.notification.persistence.mapper;

import com.cleanarch.domain.model.Notification;
import com.cleanarch.domain.model.NotificationStatus;
import com.cleanarch.domain.model.NotificationType;
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

    public static Notification toDomain(NotificationEntity entity)
    {
        return new Notification(
                entity.getEventId(),
                entity.getRecipient(),
                NotificationStatus.valueOf(entity.getStatus()),
                NotificationType.valueOf(entity.getType())
        );
    }
}
