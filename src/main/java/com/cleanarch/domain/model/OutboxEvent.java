package com.cleanarch.domain.model;

import com.cleanarch.domain.exception.InvalidOutboxStateException;

import java.time.Instant;
import java.util.UUID;

public class OutboxEvent {

    private UUID id;
    private UUID aggregateId;
    private String aggregateType;
    private EventType eventType;
    private Integer eventVersion;

    private String payload;
    private OutboxStatus status;
    private Integer retryCount;
    private String errorMessage;

    private Instant nextRetryAt;
    private Instant createdAt;
    private Instant processedAt;

    public OutboxEvent(
            UUID id,
            UUID aggregateId,
            String aggregateType,
            EventType eventType,
            Integer eventVersion,
            String payload,
            OutboxStatus status,
            Integer retryCount,
            String errorMessage,
            Instant nextRetryAt,
            Instant createdAt,
            Instant processedAt
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.eventVersion = eventVersion;
        this.payload = payload;
        this.status = status;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }

    // Getters

    public UUID getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public Integer getEventVersion() {
        return eventVersion;
    }

    public String getPayload() {
        return payload;
    }

    public OutboxStatus getStatus() {
        return status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public Instant getNextRetryAt() {
        return nextRetryAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public String getErrorMessage() { return errorMessage; }

    // Setters
    public void markProcessing() {

        if(this.status != OutboxStatus.PENDING)
            throw new InvalidOutboxStateException("Only Pending Events can be processed.");

        this.status = OutboxStatus.PROCESSING;
    }

    public void markPublished() {
        if(this.status != OutboxStatus.PROCESSING)
            throw new InvalidOutboxStateException("Only Processing Events can be published");

        this.status = OutboxStatus.PUBLISHED;
        this.processedAt = Instant.now();
    }

    public void markFailed(String error) {

        if(this.status != OutboxStatus.PROCESSING)
            throw new InvalidOutboxStateException("Only Processing Events can fail");

        this.status = OutboxStatus.DEAD;
        this.retryCount = (retryCount == null) ? 1 : retryCount + 1;
        this.errorMessage = error;

    }
}
