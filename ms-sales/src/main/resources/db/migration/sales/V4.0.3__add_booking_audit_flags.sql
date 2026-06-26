-- Bandera de auditoría para ventas aprobadas a pérdidas (Bypass)
ALTER TABLE bookings
    ADD COLUMN is_deficit_margin BOOLEAN DEFAULT FALSE,
    ADD COLUMN force_override BOOLEAN DEFAULT FALSE;