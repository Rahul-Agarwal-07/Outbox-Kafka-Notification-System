package com.cleanarch.application.notification.usecase;

import com.cleanarch.application.consumer.dto.EventMessage;
import com.cleanarch.application.notification.port.EmailSenderPort;
import com.cleanarch.application.notification.port.NotificationRepositoryPort;
import com.cleanarch.application.notification.port.SendEmailUseCasePort;
import com.cleanarch.domain.model.Notification;
import com.cleanarch.domain.model.NotificationStatus;
import com.cleanarch.domain.model.NotificationType;
import org.springframework.core.NestedExceptionUtils;

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

        Notification newNotification = new Notification(
                event.id(),
                event.payload(),
                NotificationStatus.PENDING,
                type
        );

        boolean inserted = notificationRepository.tryInsert(newNotification);

        Notification notification = inserted ?
                newNotification :
                notificationRepository.findById(event.id()).orElseThrow();


        NotificationStatus status = notification.getStatus();

        if(status == NotificationStatus.SENT)
        {
            // already processed and workflow is completed
            return;
        }

        if(status == NotificationStatus.PENDING || status == NotificationStatus.FAILED)
        {
            try {
                send(type, event);
                notificationRepository.markAsSent(event.id());
            }
            catch (Exception e)
            {
                String error = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
                notificationRepository.markAsFailed(event.id(), error);
                throw e;
            }
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
