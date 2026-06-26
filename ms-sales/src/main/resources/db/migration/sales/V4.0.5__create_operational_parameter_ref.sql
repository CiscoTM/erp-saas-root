-- Tabla de réplica local para parámetros operativos en ms-sales
CREATE TABLE IF NOT EXISTS operational_parameter_ref (
                                                         tenant_id VARCHAR(50) PRIMARY KEY,
    overhead_percentage NUMERIC(12, 4) NOT NULL,
    risk_margin_threshold NUMERIC(12, 4) NOT NULL,
    optimal_margin_threshold NUMERIC(12, 4) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Comentarios para documentación técnica
COMMENT ON TABLE operational_parameter_ref IS 'Réplica local de parámetros operativos sincronizada vía Kafka para evitar llamadas síncronas';
COMMENT ON COLUMN operational_parameter_ref.tenant_id IS 'Identificador del inquilino (Tenant ID)';