ALTER TABLE vendor_quotes
    ADD CONSTRAINT uq_vendor_quotes_request_vendor UNIQUE (request_id, vendor_id);
