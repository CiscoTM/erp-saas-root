-- ms-sales/src/main/resources/db/migration/tenants/V1__init_sales_schema.sql

CREATE TABLE IF NOT EXISTS tenant_users (
                                            id SERIAL PRIMARY KEY,
                                            username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS event_types (
                                           id SERIAL PRIMARY KEY,
                                           name VARCHAR(255),
    description TEXT
    );

CREATE TABLE IF NOT EXISTS rooms (
                                     id SERIAL PRIMARY KEY,
                                     name VARCHAR(255),
    capacity INTEGER,
    price_per_hour DOUBLE PRECISION,
    active BOOLEAN DEFAULT TRUE
    );

CREATE TABLE IF NOT EXISTS bookings (
                                        id SERIAL PRIMARY KEY,
                                        room_id INTEGER REFERENCES rooms(id), -- Añadimos integridad referencial
    event_name VARCHAR(255),
    customer_name VARCHAR(255),
    expected_guests INTEGER,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    total_price DOUBLE PRECISION,
    status VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS events (
                                      id SERIAL PRIMARY KEY,
                                      event_type_id INTEGER REFERENCES event_types(id),
    title VARCHAR(255),
    room_name VARCHAR(255),
    contact_name VARCHAR(255),
    contact_phone VARCHAR(255),
    expected_guests INTEGER,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    status VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );