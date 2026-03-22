package com.cleanarch.infrastructure.outbox.persistence.repository;
import com.cleanarch.application.outbox.port.OutboxRepositoryPort;
import com.cleanarch.domain.model.OutboxEvent;
import com.cleanarch.domain.model.OutboxStatus;
import com.cleanarch.infrastructure.outbox.persistence.entity.OutboxEventEntity;
import com.cleanarch.infrastructure.outbox.persistence.mapper.OutboxMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY = 5000;
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<OutboxEvent> fetchPendingEvents(int batchSize) {

        List<OutboxEventEntity> entities = entityManager
                .createNativeQuery("""
                      SELECT * FROM outbox_events
                      WHERE status IN('PENDING', 'RETRYABLE')
                      AND (nextRetryAt IS NULL OR nextRetryAt <= CURRENT_TIMESTAMP)
                      ORDER BY createdAt
                      LIMIT :batchSize
                      FOR UPDATE SKIP LOCKED
                    """, OutboxEventEntity.class)
                .setParameter("batchSize", batchSize)
                .getResultList();

        return entities.stream()
                .map(OutboxMapper::toDomain)
                .toList();

    }

    @Override
    @Transactional
    public void markAsProcessing(List<UUID> ids) {

        entityManager
                .createQuery("""
                        UPDATE OutboxEventEntity e
                        SET e.status = 'PROCESSING'
                        WHERE e.id IN :ids
                    """
                )
                .setParameter("ids", ids)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void markAsPublished(UUID eventId) {
        entityManager
                .createQuery("""
                        UPDATE OutboxEventEntity e
                        SET e.status = 'PUBLISHED', e.processedAt = CURRENT_TIMESTAMP
                        WHERE e.id = :eventId
                    """
                )
                .setParameter("eventId", eventId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void markAsFailed(UUID eventId, String error) {
        OutboxEventEntity event = entityManager.find(OutboxEventEntity.class, eventId);

        int retryCount = event.getRetryCount();

        if(retryCount + 1 >= MAX_RETRIES)
            event.setStatus(OutboxStatus.DEAD.name());
        else
        {
            event.setStatus(OutboxStatus.RETRYABLE.name());
            event.setNextRetryAt(calculateNextRetryTime(retryCount + 1));
        }

        event.setErrorMessage(error);
        event.setRetryCount(retryCount + 1);
    }

    private Instant calculateNextRetryTime(int retryCount)
    {
        long delay = (long) (BASE_DELAY * Math.pow(2, retryCount));
        return Instant.now().plusMillis(delay);
    }
}
