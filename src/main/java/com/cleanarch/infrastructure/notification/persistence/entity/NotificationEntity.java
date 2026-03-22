package com.cleanarch.infrastructure.notification.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.aspectj.weaver.ast.Not;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "error_message")
    private String errorMessage;

    protected NotificationEntity() {}

    public NotificationEntity(UUID id, String type, String status, String recipient, Instant createdAt, Instant updatedAt, String error) {
        this.eventId = id;
        this.type = type;
        this.status = status;
        this.recipient = recipient;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.errorMessage = error;
    }

    public void setId(UUID id) {
        this.eventId = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setError(String error) {
        this.errorMessage = error;
    }
}
