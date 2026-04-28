CREATE TABLE outbox (
                        id UUID PRIMARY KEY,
                        aggregate_id VARCHAR(255) NOT NULL,
                        type VARCHAR(100) NOT NULL,
                        payload TEXT NOT NULL,
                        status VARCHAR(20) DEFAULT 'PENDING',
                        tenant_id VARCHAR(255) NOT NULL, -- Columna necesaria para el Relayer
                        trace_id VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_status ON outbox(status);