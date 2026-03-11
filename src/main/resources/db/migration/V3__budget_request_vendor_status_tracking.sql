ALTER TABLE budget_request_vendors
    ADD COLUMN IF NOT EXISTS viewed_at TIMESTAMP,
    ADD COLUMN IF NOT EXISTS declined_at TIMESTAMP;
