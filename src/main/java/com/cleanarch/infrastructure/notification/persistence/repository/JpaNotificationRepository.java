package com.cleanarch.infrastructure.notification.persistence.repository;

import com.cleanarch.infrastructure.notification.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<NotificationEntity, UUID> {

}
