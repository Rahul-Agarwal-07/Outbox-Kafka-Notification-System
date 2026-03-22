package com.cleanarch.application.notification.port;

import com.cleanarch.domain.model.Notification;

import java.util.UUID;

public interface NotificationRepositoryPort {

    boolean tryInsert(Notification notification);
    void markAsFailed(UUID eventId, String error);
    void markAsSent(UUID eventId);

}
