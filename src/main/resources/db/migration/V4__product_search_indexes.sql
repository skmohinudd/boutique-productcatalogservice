CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_products_active_created_at
    ON products (created_at DESC)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_category_created_at
    ON products (lower(category), created_at DESC)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_name_trgm
    ON products USING gin (lower(name) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_products_description_trgm
    ON products USING gin (lower(description) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_products_sku_trgm
    ON products USING gin (lower(sku) gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_products_category_trgm
    ON products USING gin (lower(category) gin_trgm_ops);
