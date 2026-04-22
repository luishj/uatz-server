ALTER TABLE budget_requests
    ADD COLUMN IF NOT EXISTS source_channel VARCHAR(30);

ALTER TABLE budget_requests
    ADD COLUMN IF NOT EXISTS source_message TEXT;
