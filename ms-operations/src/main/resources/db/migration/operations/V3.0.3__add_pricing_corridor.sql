-- 1. Ampliación de parámetros operativos para el Cost-Plus Pricing
ALTER TABLE operational_parameters
    ADD COLUMN minimum_profit_margin NUMERIC(5,2) NOT NULL DEFAULT 0.20,
    ADD COLUMN optimal_profit_margin NUMERIC(5,2) NOT NULL DEFAULT 0.75;

-- 2. Suelo calculado dinámicamente para el plato
ALTER TABLE dish_operation
    ADD COLUMN price_floor NUMERIC(10,2) DEFAULT 0.00;

-- 3. Techo sugerido y Suelo base precalculado en el menú comercial
ALTER TABLE comercial_menu
    ADD COLUMN base_price_floor NUMERIC(10,2) DEFAULT 0.00,
    ADD COLUMN suggested_official_price NUMERIC(10,2) DEFAULT 0.00;