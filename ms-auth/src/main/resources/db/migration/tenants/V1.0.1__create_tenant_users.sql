-- /home/dev50/proyectos/erp-saas-root/ms-auth/src/main/resources/db/migration/tenants/V1.0.1__create_tenant_users.sql
-- Ejecutar en cada DB de inquilino
CREATE TABLE IF NOT EXISTS tenant_users (
                                            id BIGSERIAL PRIMARY KEY,
                                            username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Opcional: Índice para acelerar el login (Idempotente)
CREATE INDEX IF NOT EXISTS idx_tenant_users_username ON tenant_users(username);

CREATE TABLE IF NOT EXISTS outbox_events (
                                             id UUID PRIMARY KEY,
                                             aggregate_id VARCHAR(255) NOT NULL,
    aggregate_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_events(status);