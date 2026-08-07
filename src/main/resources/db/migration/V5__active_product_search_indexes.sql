-- Final load-test optimization:
-- Most catalogue reads only expose ACTIVE products. Partial indexes keep
-- inactive catalogue rows out of the hot search indexes.

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX IF NOT EXISTS idx_products_active_name_trgm
    ON products USING gin (lower(name) gin_trgm_ops)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_description_trgm
    ON products USING gin (lower(description) gin_trgm_ops)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_sku_trgm
    ON products USING gin (lower(sku) gin_trgm_ops)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_category_trgm
    ON products USING gin (lower(category) gin_trgm_ops)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_category_created_at_v2
    ON products (lower(category), created_at DESC)
    WHERE status = 'ACTIVE';

CREATE INDEX IF NOT EXISTS idx_products_active_created_at_v2
    ON products (created_at DESC)
    WHERE status = 'ACTIVE';
