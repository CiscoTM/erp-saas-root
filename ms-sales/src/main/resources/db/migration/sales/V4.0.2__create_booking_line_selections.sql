CREATE TABLE booking_line_selections (
                                         id SERIAL PRIMARY KEY,
                                         booking_line_id BIGINT NOT NULL,
                                         dish_id VARCHAR(255),
                                         quantity INTEGER,
                                         CONSTRAINT fk_booking_line FOREIGN KEY (booking_line_id) REFERENCES booking_lines(id)
);