-- Añadir precio base a la plantilla
ALTER TABLE menu_templates
    ADD COLUMN base_price NUMERIC(10, 2) NOT NULL DEFAULT 0.00;

-- Añadir el suplemento (extra_price) a la tabla intermedia
ALTER TABLE menu_category_optional_dishes
    ADD COLUMN extra_price NUMERIC(10, 2) NOT NULL DEFAULT 0.00;