package com.cleanarch.application.notification.usecase;

import com.cleanarch.application.consumer.dto.EventMessage;
import com.cleanarch.application.notification.port.EmailSenderPort;
import com.cleanarch.application.notification.port.NotificationRepositoryPort;
import com.cleanarch.application.notification.port.SendEmailUseCasePort;
import com.cleanarch.domain.model.Notification;
import com.cleanarch.domain.model.NotificationStatus;
import com.cleanarch.domain.model.NotificationType;
import jakarta.transaction.Transactional;

@Transactional
public class SendEmailUseCase implements SendEmailUseCasePort {

    private final NotificationRepositoryPort notificationRepository;
    private final EmailSenderPort emailSender;

    public SendEmailUseCase(NotificationRepositoryPort notificationRepository, EmailSenderPort emailSender) {
        this.notificationRepository = notificationRepository;
        this.emailSender = emailSender;
    }

    @Override
    public void execute(EventMessage event) {

        NotificationType type = mapEventToNotification(event);

        boolean reserved = notificationRepository.tryInsert(
                new Notification(
                        event.id(),
                        event.payload(),
                        NotificationStatus.PENDING,
                        type
                )
        );

        if(!reserved) return;

        try {
            send(type, event);
            notificationRepository.markAsSent(event.id());
        }
        catch (Exception e)
        {
            notificationRepository.markAsFailed(event.id(), e.getCause().getMessage());
            throw e;
        }

    }

    private void send(NotificationType type, EventMessage event) {

        switch (type)
        {
            case WELCOME_EMAIL -> emailSender.sendWelcomeEmail(event.payload());
            case SECURITY_ALERT -> emailSender.sendSecurityAlert(event.payload());
            default -> throw new IllegalStateException("Unsupported NotificationType");
        }

    }

    private NotificationType mapEventToNotification(EventMessage event) {

        return switch (event.eventType())
        {
            case USER_CREATED -> NotificationType.WELCOME_EMAIL;
            case PASSWORD_CHANGED -> NotificationType.SECURITY_ALERT;
        };

    }
}
