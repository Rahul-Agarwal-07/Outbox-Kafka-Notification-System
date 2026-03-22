package com.cleanarch.application.notification.port;

import java.util.UUID;

public interface NotificationRepositoryPort {

    boolean tryInsert(NotificationCommand command);
    void markAsFailed(UUID eventId, String error);
    void markAsSent(UUID eventId);

}
