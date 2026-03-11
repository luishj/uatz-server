CREATE TABLE IF NOT EXISTS budget_request_vendors (
    id BIGSERIAL PRIMARY KEY,
    request_id BIGINT NOT NULL REFERENCES budget_requests (id) ON DELETE CASCADE,
    vendor_id BIGINT NOT NULL REFERENCES vendors (id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL,
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,
    UNIQUE (request_id, vendor_id)
);
