-- Liquibase rollback: drop table payment_cards; drop table users;

-- changeset ynohach:initial_schema
CREATE TABLE IF NOT EXISTS users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(255),
                       surname VARCHAR(255),
                       birth_date DATE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP WITHOUT TIME ZONE,
                       updated_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS payment_cards (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               number VARCHAR(20) NOT NULL unique,
                               holder VARCHAR(255) NOT NULL,
                               expiration_date VARCHAR(5) NOT NULL,
                               active BOOLEAN DEFAULT TRUE,
                               created_at TIMESTAMP WITHOUT TIME ZONE,
                               updated_at TIMESTAMP WITHOUT TIME ZONE,
                               CONSTRAINT fk_payment_cards_user FOREIGN KEY (user_id) REFERENCES users (id)
);


CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_payment_cards_user_id ON payment_cards(user_id);