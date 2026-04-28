package es.tk3.operations.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    @Column(name = "event_id")
    private UUID eventId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Column(name = "event_type")
    private String eventType;

    public ProcessedEvent(UUID eventId, LocalDateTime processedAt, String eventType) {
        this.eventId = eventId;
        this.processedAt = processedAt;
        this.eventType = eventType;
    }

    public ProcessedEvent() {
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID eventId;
        private LocalDateTime processedAt;
        private String eventType;

        public Builder eventId(UUID eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder processedAt(LocalDateTime processedAt) {
            this.processedAt = processedAt;
            return this;
        }

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public ProcessedEvent build() {
            return new ProcessedEvent(eventId, processedAt, eventType);
        }
    }
}
