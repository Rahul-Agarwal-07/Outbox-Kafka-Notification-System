package com.cleanarch.domain.exception;

public class InvalidEventException extends DomainException {
    public InvalidEventException() { super("Invalid Event Payload");}
    public InvalidEventException(String message) {
        super(message);
    }
}
