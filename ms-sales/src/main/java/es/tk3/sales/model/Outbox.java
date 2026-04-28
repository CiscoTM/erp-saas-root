package es.tk3.sales.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class Outbox {

    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(name = "tenant_id", nullable = false) // Añadido
    private String tenantId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "trace_id")
    private String traceId;

    public Outbox() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters y Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTenantId() { return tenantId; } // Añadido
    public void setTenantId(String tenantId) { this.tenantId = tenantId; } // Añadido

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
}