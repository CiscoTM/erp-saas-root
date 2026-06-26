CREATE TABLE IF NOT EXISTS operational_parameters (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      tenant_id VARCHAR(50) NOT NULL UNIQUE,      -- Soporte multi-tenant
    overhead_percentage NUMERIC(5,2) NOT NULL,  -- Ej: 0.25 (25%)
    risk_margin_threshold NUMERIC(5,2) NOT NULL,
    optimal_margin_threshold NUMERIC(5,2) NOT NULL
    );