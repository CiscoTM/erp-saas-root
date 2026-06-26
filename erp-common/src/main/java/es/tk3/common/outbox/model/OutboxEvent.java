package es.tk3.common.outbox.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;
    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;
    @Column(name = "topic", nullable = false)
    private String topic;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "payload", columnDefinition = "TEXT", nullable = false)
    private String payload;
    @Column(name = "status", nullable = false)
    private String status = "PENDING";
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public OutboxEvent() {}

    public OutboxEvent(UUID id, String aggregateId, String aggregateType, String topic, String eventType, String payload, String status) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.topic = topic;
        this.eventType = eventType;
        this.payload = payload;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public void setTopic(String topic) { this.topic = topic; }
    public void setPayload(String payload) { this.payload = payload; }
    public String getStatus() { return status; }

    public String getAggregateId() { return aggregateId; }
    public String getTopic() { return topic; }
    public String getPayload() { return payload; }
    public void setStatus(String status) { this.status = status; }
}
