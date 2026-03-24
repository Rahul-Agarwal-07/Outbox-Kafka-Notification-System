package com.cleanarch.infrastructure.notification.persistence.repository;

import com.cleanarch.application.notification.port.NotificationRepositoryPort;
import com.cleanarch.domain.exception.NotificationEntityDoesNotExists;
import com.cleanarch.domain.model.Notification;
import com.cleanarch.domain.model.NotificationStatus;
import com.cleanarch.infrastructure.notification.persistence.entity.NotificationEntity;
import com.cleanarch.infrastructure.notification.persistence.mapper.NotificationMapper;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

public class NotificationRepositoryAdapter implements NotificationRepositoryPort {

    private final EntityManager entityManager;
    private final JpaNotificationRepository jpaNotificationRepository;

    public NotificationRepositoryAdapter(EntityManager entityManager, JpaNotificationRepository jpaNotificationRepository) {
        this.entityManager = entityManager;
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public Optional<Notification> findById(UUID eventId) {

        return jpaNotificationRepository.findById(eventId)
                .map(NotificationMapper::toDomain);

    }

    @Override
    @Transactional
    public boolean tryInsert(Notification notification) {

        try {
            NotificationEntity entity = NotificationMapper.toEntity(notification);
            jpaNotificationRepository.save(entity);

            return true;

        } catch(DataIntegrityViolationException e)
        {
            return false;
        }
    }

    @Override
    @Transactional
    public void markAsFailed(UUID eventId, String error) {

        entityManager.createQuery(
             """
                    UPDATE NotificationEntity n
                    SET n.status = :status,
                        n.errorMessage = :error,
                        n.updatedAt = CURRENT_TIMESTAMP
                    WHERE n.eventId = :eventId AND n.status != 'SENT'
                """
        )
                .setParameter("status", NotificationStatus.FAILED.name())
                .setParameter("error", error)
                .setParameter("eventId", eventId)
                .executeUpdate();
    }

    @Override
    @Transactional
    public void markAsSent(UUID eventId) {
        entityManager.createQuery(
                     """
                            UPDATE NotificationEntity n
                            SET n.status = :status,                           
                                n.updatedAt = CURRENT_TIMESTAMP
                            WHERE n.eventId = :eventId AND n.status != 'SENT'
                        """
                )
                .setParameter("status", NotificationStatus.SENT.name())
                .setParameter("eventId", eventId)
                .executeUpdate();
    }
}
