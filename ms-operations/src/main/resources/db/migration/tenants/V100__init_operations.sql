CREATE TABLE IF NOT EXISTS function_sheets (
                                               id BIGSERIAL PRIMARY KEY,
                                               booking_id BIGINT NOT NULL,
                                               event_name VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    room_name VARCHAR(255),
    guests_count INTEGER,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Tabla técnica para control de eventos ya procesados (Idempotencia Técnica)
CREATE TABLE IF NOT EXISTS processed_events (
                                                event_id UUID PRIMARY KEY,
                                                processed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);