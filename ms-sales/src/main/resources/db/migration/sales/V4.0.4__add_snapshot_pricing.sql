-- DDL: Añadimos las columnas como nullable para no romper la compatibilidad inmediatamente
ALTER TABLE booking_lines
    ADD COLUMN snapshot_overhead_percentage NUMERIC(5,2),
ADD COLUMN snapshot_risk_margin_threshold NUMERIC(5,2),
ADD COLUMN snapshot_optimal_margin_threshold NUMERIC(5,2);

-- DML: Poblamos las reservas históricas con los parámetros por defecto de la empresa
UPDATE booking_lines
SET snapshot_overhead_percentage = 0.15,
    snapshot_risk_margin_threshold = 0.10,
    snapshot_optimal_margin_threshold = 0.25
WHERE snapshot_overhead_percentage IS NULL;