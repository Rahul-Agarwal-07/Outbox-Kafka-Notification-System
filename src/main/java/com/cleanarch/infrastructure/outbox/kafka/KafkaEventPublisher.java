package com.cleanarch.infrastructure.outbox.kafka;

import com.cleanarch.application.outbox.port.EventPublisherPort;
import com.cleanarch.domain.model.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventPublisher implements EventPublisherPort {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(OutboxEvent event) {

        String topic = resolveTopic(event);

        try
        {
            kafkaTemplate.send(
                    topic,
                    event.getAggregateId().toString(),
                    event.getPayload()
            ).get();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String resolveTopic(OutboxEvent event)
    {
        return event.getAggregateType();
    }
}
