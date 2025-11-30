-- Customer Service Database Schema
-- Customer inherits from Person (all Person fields are included in customers table)

CREATE TABLE IF NOT EXISTS persons (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    identification VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(500),
    phone VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customer table inherits all Person fields (name, gender, identification, address, phone)
CREATE TABLE IF NOT EXISTS customers (
    id BIGSERIAL PRIMARY KEY,
    -- Person fields (inherited)
    name VARCHAR(255) NOT NULL,
    gender VARCHAR(50) NOT NULL,
    identification VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(500),
    phone VARCHAR(50),
    -- Customer specific fields
    password VARCHAR(255) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_customers_identification ON customers(identification);
CREATE INDEX IF NOT EXISTS idx_persons_identification ON persons(identification);

