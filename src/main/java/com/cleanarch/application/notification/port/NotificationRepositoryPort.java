package com.cleanarch.application.notification.port;

import com.cleanarch.domain.model.Notification;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepositoryPort {

    Optional<Notification> findById(UUID eventId);
    boolean tryInsert(Notification notification);
    void markAsFailed(UUID eventId, String error);
    void markAsSent(UUID eventId);

}
