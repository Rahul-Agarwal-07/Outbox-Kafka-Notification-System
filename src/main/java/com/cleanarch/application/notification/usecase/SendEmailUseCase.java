package com.cleanarch.application.notification.usecase;

import com.cleanarch.application.consumer.dto.EventMessage;
import com.cleanarch.application.notification.port.EmailSenderPort;
import com.cleanarch.application.notification.port.NotificationCommand;
import com.cleanarch.application.notification.port.NotificationRepositoryPort;
import com.cleanarch.application.notification.port.SendEmailUseCasePort;
import com.cleanarch.domain.model.NotificationType;

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
                new NotificationCommand(
                        event.id(),
                        type,
                        event.payload()
                )
        );

        if(!reserved) return;

        try {
            send(type, event);
            notificationRepository.markAsSent(event.id());
        }
        catch (Exception e)
        {
            notificationRepository.markAsFailed(event.id(), e.getMessage());
            throw e;
        }

    }

    private void send(NotificationType type, EventMessage event) {

        switch (type)
        {
            case WELCOME_EMAIL -> emailSender.sendWelcomeEmail(event.payload());
            case SECURITY_ALERT -> emailSender.sendSecurityAlert(event.payload());
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
