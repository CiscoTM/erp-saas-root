-- Tabla de usuarios maestros
CREATE TABLE IF NOT EXISTS users (
                                     id SERIAL PRIMARY KEY,
                                     username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    role VARCHAR(255) NOT NULL
    );

-- ⚠️ ESTA ES LA TABLA QUE FALTA
CREATE TABLE IF NOT EXISTS tenants (
                                       id VARCHAR(255) PRIMARY KEY,      -- Ejemplo: 'bar_paco'
    db_url VARCHAR(255) NOT NULL,     -- Ejemplo: 'jdbc:postgresql://localhost:5433/db_bar_paco'
    db_username VARCHAR(255) NOT NULL,
    db_password VARCHAR(255) NOT NULL
    );

-- Insertar usuario inicial
INSERT INTO users (username, password, email, role)
VALUES ('tk3dev', '$2a$12$0QIr8RaCx//fSNA5ir6.VuKDlYZ1dYsSQWqEQhTw8nTYGr1nQ.qhC', 'admin@central.com', 'SUPERADMIN')
    ON CONFLICT (username) DO NOTHING;