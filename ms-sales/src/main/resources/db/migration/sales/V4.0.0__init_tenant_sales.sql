-- 1. Tabla de Espacios Físicos (Rooms)
CREATE TABLE IF NOT EXISTS rooms (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       capacity INTEGER NOT NULL,
                       price_per_hour NUMERIC(12, 2) NOT NULL,
                       active BOOLEAN DEFAULT TRUE NOT NULL
);

-- 2. Catálogo Maestro de Tipos de Eventos (NUEVO/RESTAURADO)
CREATE TABLE IF NOT EXISTS event_types (
                             id BIGSERIAL PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             description TEXT
);

-- Inserción del catálogo por defecto
INSERT INTO event_types (name, description) VALUES
                                                ('Boda', 'Celebración nupcial completa'),
                                                ('Comunión', 'Celebración de primera comunión'),
                                                ('Bautizo', 'Celebración de bautizo'),
                                                ('Evento Corporativo', 'Reuniones, cenas de empresa o convenciones'),
                                                ('Fiesta Privada', 'Cumpleaños, aniversarios y fiestas particulares');

-- 3. Tabla de Cabecera de Reservas (Bookings) - MODIFICADA
CREATE TABLE IF NOT EXISTS bookings (
                          id BIGSERIAL PRIMARY KEY,
                          room_id BIGINT NULL,
                          event_type_id BIGINT NULL,
                          event_name VARCHAR(255) NULL,
                          customer_name VARCHAR(255) NULL,
                          start_date TIMESTAMP NULL,
                          end_date TIMESTAMP NULL,
                          total_price NUMERIC(12, 2) NULL,
                          expected_guests INTEGER NULL,
                          status VARCHAR(50) NULL,
                          CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms (id) ON DELETE SET NULL,
                          CONSTRAINT fk_bookings_event_type FOREIGN KEY (event_type_id) REFERENCES event_types (id) ON DELETE SET NULL
);

-- 4. Tabla de Líneas de Detalle / Carrito de Compra (Booking Lines)
CREATE TABLE IF NOT EXISTS booking_lines (
                               id BIGSERIAL PRIMARY KEY,
                               booking_id BIGINT NOT NULL,
                               commercial_menu_id VARCHAR(255) NULL,
                               pax_count INTEGER NULL,
                               agreed_price_per_pax NUMERIC(12, 2) NULL,
                               CONSTRAINT fk_lines_booking FOREIGN KEY (booking_id) REFERENCES bookings (id) ON DELETE CASCADE
);

-- 5. Tabla del Motor Transaccional Outbox (erp-common)
CREATE TABLE IF NOT EXISTS outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_id VARCHAR(255) NOT NULL,
                               aggregate_type VARCHAR(255) NOT NULL,
                               topic VARCHAR(255) NOT NULL,
                               event_type VARCHAR(255) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 6. Índices de rendimiento
CREATE INDEX IF NOT EXISTS idx_bookings_dates ON bookings (start_date, end_date);
CREATE INDEX IF NOT EXISTS idx_bookings_status ON bookings (status);
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_events (status);