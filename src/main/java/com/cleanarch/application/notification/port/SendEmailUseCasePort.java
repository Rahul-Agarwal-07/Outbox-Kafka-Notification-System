package com.cleanarch.application.notification.port;

import com.cleanarch.application.consumer.dto.EventMessage;

public interface SendEmailUseCasePort {

    void execute(EventMessage event);

}
