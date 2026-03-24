package com.cleanarch.application.consumer.usecase;

import com.cleanarch.application.consumer.dto.EventMessage;
import com.cleanarch.application.consumer.port.ConsumeEventUseCasePort;
import com.cleanarch.application.consumer.port.ProcessedEventRepositoryPort;
import com.cleanarch.domain.model.EventType;
import org.springframework.dao.DataIntegrityViolationException;

public class ConsumeEventUseCase implements ConsumeEventUseCasePort {

    private final ProcessedEventRepositoryPort processedEventRepository;

    public ConsumeEventUseCase(ProcessedEventRepositoryPort processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    public void execute(EventMessage message) {

        try {
            handleEvent(message);
            processedEventRepository.insert(message.id());

        } catch (DataIntegrityViolationException e) {
            return;
        }
    }

    private void handleEvent(EventMessage message)
    {
        if(message.eventType() == EventType.USER_CREATED)
        {
            sendWelcomeEmail(message);
        }

        if(message.eventType() == EventType.PASSWORD_CHANGED)
        {
            sendSecurityAlert(message);
        }
    }
}
