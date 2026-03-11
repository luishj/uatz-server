CREATE TABLE IF NOT EXISTS vendor_quote_items (
    id BIGSERIAL PRIMARY KEY,
    vendor_quote_id BIGINT NOT NULL REFERENCES vendor_quotes (id) ON DELETE CASCADE,
    budget_item_id BIGINT NOT NULL REFERENCES budget_items (id),
    product_name VARCHAR(150) NOT NULL,
    quantity NUMERIC(12, 2) NOT NULL,
    unit VARCHAR(30),
    unit_price NUMERIC(12, 2) NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL
);
