package com.cleanarch.infrastructure.consumer.kafka;

import com.cleanarch.application.consumer.dto.EventMessage;
import com.cleanarch.application.consumer.port.ConsumeEventUseCasePort;
import com.cleanarch.application.consumer.usecase.ConsumeEventUseCase;
import com.cleanarch.domain.exception.InvalidEventException;
import com.cleanarch.domain.model.OutboxEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class UserEventKafkaListener {

    private static final Logger log = LogManager.getLogger(UserEventKafkaListener.class);
    private final ConsumeEventUseCasePort useCase;
    private final ObjectMapper objectMapper;

    public UserEventKafkaListener(ConsumeEventUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "user-events",
            groupId = "notification-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void listen(String message, Acknowledgment ack) throws JsonProcessingException {

        try{
            EventMessage event = objectMapper.readValue(message, EventMessage.class);
            useCase.execute(event);
            ack.acknowledge();
        }catch(InvalidEventException e)
        {
            log.error("Invalid Event : {}", message, e);

            // swallow invalid events
            ack.acknowledge();

        }catch(Exception e)
        {
            throw e;
        }
    }

}
