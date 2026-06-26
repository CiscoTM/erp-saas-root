-- =============================================================================
-- 1. INFRAESTRUCTURA E IDEMPOTENCIA
-- =============================================================================
CREATE TABLE IF NOT EXISTS processed_events (
                                                event_id UUID PRIMARY KEY,
                                                processed_at TIMESTAMP NOT NULL,
                                                event_type VARCHAR(255)
    );

-- =============================================================================
-- 2. CATÁLOGO OPERATIVO DE PLATOS (Espejo optimizado de ms-kitchen)
-- =============================================================================
CREATE TABLE IF NOT EXISTS dish_operation (
                                              id UUID PRIMARY KEY,
                                              name VARCHAR(255),
    kitchen_dish_id BIGINT NOT NULL UNIQUE,
    base_cost DECIMAL(10,2),
    allergens VARCHAR(255),
    last_update TIMESTAMP
    );

-- Índice para búsquedas rápidas durante la sincronización de eventos de cocina
CREATE INDEX IF NOT EXISTS idx_dish_op_kitchen_id ON dish_operation(kitchen_dish_id);

-- =============================================================================
-- 3. GESTIÓN COMERCIAL: TARIFAS, TEMPORADAS Y MENÚS
-- =============================================================================
CREATE TABLE IF NOT EXISTS season (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(255),
    start_date DATE,
    end_date DATE,
    multiplier DECIMAL(10,2)
    );

CREATE TABLE IF NOT EXISTS tariff (
                                      id UUID PRIMARY KEY,
                                      name VARCHAR(255),
    description VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS comercial_menu (
                                              id UUID PRIMARY KEY,
                                              name VARCHAR(255) NOT NULL,
    description TEXT,
    menu_template_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
    );

-- Matriz de Precios por combinación de Menú, Temporada y Tarifa
CREATE TABLE IF NOT EXISTS menu_prices (
                                           id UUID PRIMARY KEY,
                                           commercial_menu_id UUID NOT NULL REFERENCES comercial_menu(id) ON DELETE CASCADE,
    season_id UUID NOT NULL REFERENCES season(id),
    tariff_id UUID NOT NULL REFERENCES tariff(id),
    price_per_pax DECIMAL(10,2) NOT NULL,
    UNIQUE(commercial_menu_id, season_id, tariff_id)
    );

-- Platos considerados Extras fuera del menú cerrado
CREATE TABLE IF NOT EXISTS extra_dish (
                                          id UUID PRIMARY KEY,
                                          dish_id UUID NOT NULL REFERENCES dish_operation(id) ON DELETE CASCADE,
    commercial_name VARCHAR(255),
    fixed_price DECIMAL(10,2)
    );

-- Tabla intermedia para mapear códigos de venta con recetas físicas de cocina
CREATE TABLE IF NOT EXISTS commercial_menu_recipes (
                                                       id BIGSERIAL PRIMARY KEY,
                                                       commercial_menu_code VARCHAR(255) NOT NULL, -- Código alfanumérico enviado por ms-sales
    dish_id UUID NOT NULL REFERENCES dish_operation(id) ON DELETE CASCADE,
    default_quantity INTEGER NOT NULL DEFAULT 1
    );

-- Índice para acelerar la explosión del menú al recibir el evento de reserva confirmed
CREATE INDEX IF NOT EXISTS idx_commercial_menu_code ON commercial_menu_recipes(commercial_menu_code);

-- =============================================================================
-- 4. CONTROL OPERATIVO: HOJAS DE FUNCIÓN (Órdenes de Servicio)
-- =============================================================================
CREATE TABLE IF NOT EXISTS function_sheets (
                                               id UUID PRIMARY KEY,
                                               booking_id BIGINT NOT NULL,
                                               event_name VARCHAR(255) NOT NULL,
    event_date TIMESTAMP NOT NULL,
    room_name VARCHAR(255),
    guests_count INTEGER,
    commercial_menu_id UUID REFERENCES comercial_menu(id), -- Clave foránea integrada de forma nativa
    agreed_menu_price DECIMAL(10,2),
    profitability_status VARCHAR(50),
    status VARCHAR(255) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Desglose y detalles de platos/servicios asignados a la hoja de función
CREATE TABLE IF NOT EXISTS function_sheet_details (
                                                      id UUID PRIMARY KEY,
                                                      function_sheet_id UUID NOT NULL REFERENCES function_sheets(id) ON DELETE CASCADE,
    dish_id UUID NOT NULL REFERENCES dish_operation(id),
    custom_name VARCHAR(255) NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    final_price DECIMAL(10,2) NOT NULL,
    unit_cost_at_event DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL
    );