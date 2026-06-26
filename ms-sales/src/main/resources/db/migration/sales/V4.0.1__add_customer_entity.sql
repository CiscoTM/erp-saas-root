CREATE TABLE IF NOT EXISTS customers (
                                         id UUID PRIMARY KEY,
                                         tax_id VARCHAR(50) NOT NULL, -- NIF / CIF / DNI
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    customer_type VARCHAR(50) DEFAULT 'INDIVIDUAL' -- INDIVIDUAL o COMPANY
    );

ALTER TABLE bookings ADD COLUMN customer_id UUID REFERENCES customers(id);
ALTER TABLE bookings DROP COLUMN customer_name;