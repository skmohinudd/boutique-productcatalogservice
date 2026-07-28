CREATE TABLE products (
    id UUID PRIMARY KEY,
    sku VARCHAR(100) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price NUMERIC(12, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    image_url VARCHAR(1000),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_products_sku UNIQUE (sku),
    CONSTRAINT chk_products_price_non_negative CHECK (price >= 0),
    CONSTRAINT chk_products_currency_length CHECK (CHAR_LENGTH(currency) = 3),
    CONSTRAINT chk_products_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_products_name ON products (name);
CREATE INDEX idx_products_category ON products (category);
CREATE INDEX idx_products_status ON products (status);