package com.cleanarch.domain.exception;

public class NotificationEntityDoesNotExists extends DomainException {
    public NotificationEntityDoesNotExists() { super("Notification Entity does not exists");}
    public NotificationEntityDoesNotExists(String message) {
        super(message);
    }
}
