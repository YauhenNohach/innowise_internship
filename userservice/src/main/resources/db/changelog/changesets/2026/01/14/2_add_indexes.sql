-- Liquibase rollback: DROP INDEX idx_users_name; DROP INDEX idx_users_surname; DROP INDEX idx_users_name_surname; DROP INDEX idx_users_active; DROP INDEX idx_payment_cards_number; DROP INDEX idx_payment_cards_holder; DROP INDEX idx_payment_cards_active;

-- changeset ynohach:add_additional_indexes
CREATE INDEX IF NOT EXISTS idx_users_name ON users(name);
CREATE INDEX IF NOT EXISTS idx_users_surname ON users(surname);
CREATE INDEX IF NOT EXISTS idx_users_name_surname ON users(name, surname);
CREATE INDEX idx_users_active ON users(active);

CREATE INDEX IF NOT EXISTS idx_payment_cards_number ON payment_cards(number);
CREATE INDEX IF NOT EXISTS idx_payment_cards_holder ON payment_cards(holder);
CREATE INDEX IF NOT EXISTS idx_payment_cards_active ON payment_cards(active);