-- =============================================================================
-- 1. INFRAESTRUCTURA: EVENTOS OUTBOX (Garantía de Consistencia Eventual)
-- =============================================================================
CREATE TABLE IF NOT EXISTS outbox_events (
                                             id UUID PRIMARY KEY,
                                             aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    topic VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Índices de alta eficiencia para el Polling/Debezium Worker
CREATE INDEX IF NOT EXISTS idx_outbox_events_status ON outbox_events(status);
CREATE INDEX IF NOT EXISTS idx_outbox_events_created_at ON outbox_events(created_at);

-- =============================================================================
-- 2. SANIDAD: MAESTRO UNIFICADO DE ALÉRGENOS E INTOLERANCIAS
-- =============================================================================
CREATE TABLE IF NOT EXISTS allergens (
                                         id BIGSERIAL PRIMARY KEY,
                                         code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    restriction_type VARCHAR(50) NOT NULL DEFAULT 'OFFICIAL_ALLERGEN'
    );

-- Inserción directa y atómica de los 14 alérgenos regulados por la UE
INSERT INTO allergens (code, name, description, restriction_type) VALUES
                                                                      ('GLUTEN', 'Cereales con gluten', 'Trigo, centeno, cebada, avena, espelta, kamut o sus variedades híbridas y productos derivados.', 'OFFICIAL_ALLERGEN'),
                                                                      ('CRUSTACEOS', 'Crustáceos y derivados', 'Cangrejos, langostinos, gambas, cigalas, etc., y productos a base de crustáceos.', 'OFFICIAL_ALLERGEN'),
                                                                      ('HUEVOS', 'Huevos y derivados', 'Huevos y productos a base de huevo (mayonesa, repostería, pasta al huevo).', 'OFFICIAL_ALLERGEN'),
                                                                      ('PESCADO', 'Pescado y derivados', 'Pescado y productos a base de pescado, excepto gelatina de pescado utilizada como soporte de vitaminas.', 'OFFICIAL_ALLERGEN'),
                                                                      ('CACAHUETES', 'Cacahuetes y derivados', 'Cacahuetes, aceite de cacahuete, manteca y productos a base de cacahuetes.', 'OFFICIAL_ALLERGEN'),
                                                                      ('SOJA', 'Soja y derivados', 'Granos de soja, aceites refinados de soja, salsas de soja y productos a base de soja.', 'OFFICIAL_ALLERGEN'),
                                                                      ('LACTEOS', 'Lácteos y derivados', 'Leche de vaca, cabra, oveja, lactosa, queso, mantequilla, yogur y trazas lácteas.', 'OFFICIAL_ALLERGEN'),
                                                                      ('FRUTOS_CASCARA', 'Frutos de cáscara', 'Almendras, avellanas, nueces, anacardos, pacanas, castañas de Pará, pistachos y derivados.', 'OFFICIAL_ALLERGEN'),
                                                                      ('APIO', 'Apio y derivados', 'Hojas, tallos, semillas de apio, sal de apio y productos derivados (caldos, sopas).', 'OFFICIAL_ALLERGEN'),
                                                                      ('MOSTAZA', 'Mostaza y derivados', 'Mostaza en grano, en polvo, salsas, aliños y productos derivados.', 'OFFICIAL_ALLERGEN'),
                                                                      ('SESAMO', 'Sésamo y derivados', 'Semillas de sésamo, harina de sésamo, aceite de sésamo y pastas (tahini).', 'OFFICIAL_ALLERGEN'),
                                                                      ('SULFITOS', 'Dióxido de azufre y sulfitos', 'Conservantes E220-E228 en concentraciones superiores a 10 mg/kg o 10 mg/litro (vino, vinagre).', 'OFFICIAL_ALLERGEN'),
                                                                      ('ALTRAMUCES', 'Altramuces y derivados', 'Altramuces, harinas de altramuz y productos de panadería o pastas que los incluyan.', 'OFFICIAL_ALLERGEN'),
                                                                      ('MOLUSCOS', 'Moluscos y derivados', 'Mejillones, almejas, calamares, pulpo, caracoles de tierra, etc., y productos derivados.', 'OFFICIAL_ALLERGEN')
    ON CONFLICT (code) DO UPDATE SET
    name = EXCLUDED.name,
                              description = EXCLUDED.description,
                              restriction_type = EXCLUDED.restriction_type;

-- =============================================================================
-- 3. LOGÍSTICA BASE: MATERIAS PRIMAS (Entidad RawMaterial)
-- =============================================================================
CREATE TABLE IF NOT EXISTS raw_materials (
                                             id BIGSERIAL PRIMARY KEY,
                                             name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00
    );

-- Relación de alérgenos/intolerancias con materias primas (EVOLUCIONADA CON PRESENCIA)
CREATE TABLE IF NOT EXISTS raw_material_allergens (
                                                      raw_material_id BIGINT NOT NULL REFERENCES raw_materials(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    presence_type VARCHAR(50) NOT NULL DEFAULT 'CONTAINS', -- ◄ Calificador de Traza/Contenido
    PRIMARY KEY (raw_material_id, allergen_id)
    );

-- =============================================================================
-- 4. PRODUCCIÓN: RECETAS E INGREDIENTES (Entidades Recipe y RecipeIngredient)
-- =============================================================================
CREATE TABLE IF NOT EXISTS recipes (
                                       id BIGSERIAL PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    preparation_steps TEXT,
    base_cost DECIMAL(10,2) DEFAULT 0.00,
    total_yield DOUBLE PRECISION,
    yield_unit VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS recipe_ingredients (
                                                  id BIGSERIAL PRIMARY KEY,
                                                  recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    raw_material_id BIGINT REFERENCES raw_materials(id) ON DELETE SET NULL,
    sub_recipe_id BIGINT REFERENCES recipes(id) ON DELETE SET NULL,
    quantity DECIMAL(10,3) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    CONSTRAINT chk_ingredient_source CHECK (
(raw_material_id IS NOT NULL AND sub_recipe_id IS NULL) OR
(raw_material_id IS NULL AND sub_recipe_id IS NOT NULL)
    )
    );

-- Relación explícita de alérgenos/intolerancias consolidados en Recetas (EVOLUCIONADA CON PRESENCIA)
CREATE TABLE IF NOT EXISTS recipe_allergens (
                                                recipe_id BIGINT NOT NULL REFERENCES recipes(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    presence_type VARCHAR(50) NOT NULL DEFAULT 'CONTAINS', -- ◄ Calificador de Traza/Contenido
    PRIMARY KEY (recipe_id, allergen_id)
    );

-- =============================================================================
-- 5. FICHA TÉCNICA: PLATOS Y ESCANDALLOS (Entidades Dish y DishPreparation)
-- =============================================================================
CREATE TABLE IF NOT EXISTS dishes (
                                      id BIGSERIAL PRIMARY KEY,
                                      technical_name VARCHAR(255) NOT NULL,
    base_cost NUMERIC(10, 2) DEFAULT 0.00,
    diners_per_plate INTEGER,
    service_type VARCHAR(50)
    );

CREATE TABLE IF NOT EXISTS dish_preparations (
                                                 id BIGSERIAL PRIMARY KEY,
                                                 dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    raw_material_id BIGINT REFERENCES raw_materials(id) ON DELETE SET NULL,
    recipe_id BIGINT REFERENCES recipes(id) ON DELETE SET NULL,
    quantity_required DOUBLE PRECISION NOT NULL,
    CONSTRAINT chk_prep_source CHECK (
(raw_material_id IS NOT NULL AND recipe_id IS NULL) OR
(raw_material_id IS NULL AND recipe_id IS NOT NULL)
    )
    );

-- Relación explícita de alérgenos/intolerancias consolidados en Platos (EVOLUCIONADA CON PRESENCIA)
CREATE TABLE IF NOT EXISTS dish_allergens (
                                              dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    allergen_id BIGINT NOT NULL REFERENCES allergens(id) ON DELETE CASCADE,
    presence_type VARCHAR(50) NOT NULL DEFAULT 'CONTAINS', -- ◄ Calificador de Traza/Contenido
    PRIMARY KEY (dish_id, allergen_id)
    );

-- =============================================================================
-- 6. COMERCIAL: PLANTILLAS ESTRUCTURALES DE MENÚ (MenuTemplate y MenuCategory)
-- =============================================================================
CREATE TABLE IF NOT EXISTS menu_templates (
                                              id BIGSERIAL PRIMARY KEY,
                                              name VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS menu_categories (
                                               id BIGSERIAL PRIMARY KEY,
                                               menu_template_id BIGINT NOT NULL REFERENCES menu_templates(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    sequence_order INTEGER,
    selectable_count INTEGER
    );

-- Tablas de unión Muchos a Muchos (ManyToMany) con Platos (Dishes)
CREATE TABLE IF NOT EXISTS menu_category_fixed_dishes (
                                                          menu_category_id BIGINT NOT NULL REFERENCES menu_categories(id) ON DELETE CASCADE,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    PRIMARY KEY (menu_category_id, dish_id)
    );

CREATE TABLE IF NOT EXISTS menu_category_optional_dishes (
                                                             menu_category_id BIGINT NOT NULL REFERENCES menu_categories(id) ON DELETE CASCADE,
    dish_id BIGINT NOT NULL REFERENCES dishes(id) ON DELETE CASCADE,
    PRIMARY KEY (menu_category_id, dish_id)
    );