package com.cleanarch.application.notification.port;

public interface EmailSenderPort {

    void sendWelcomeEmail(String recipient);
    void sendSecurityAlert(String recipient);

}
