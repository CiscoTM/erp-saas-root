ALTER TABLE raw_materials
    ADD COLUMN purchase_unit VARCHAR(50) NOT NULL DEFAULT 'UNIDAD',
    ADD COLUMN purchase_price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    ADD COLUMN conversion_factor NUMERIC(10, 4) NOT NULL DEFAULT 1.0000,
    ADD COLUMN kitchen_unit VARCHAR(50) NOT NULL DEFAULT 'UNIDAD',
    ADD COLUMN cost_per_kitchen_unit NUMERIC(10, 4) NOT NULL DEFAULT 0.00;

    ALTER TABLE raw_materials DROP COLUMN price;

-- Comentario para el equipo de desarrollo:
-- cost_per_kitchen_unit = purchase_price / conversion_factor