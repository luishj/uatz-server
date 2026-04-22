CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS statuses (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(40) NOT NULL,
    code VARCHAR(40) NOT NULL,
    name VARCHAR(80) NOT NULL,
    UNIQUE (type, code)
);

INSERT INTO roles (code, name)
SELECT 'ADMIN', 'Administrador'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'ADMIN');

INSERT INTO roles (code, name)
SELECT 'OPERATOR', 'Operador'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'OPERATOR');

INSERT INTO roles (code, name)
SELECT 'VENDOR', 'Fornecedor'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE code = 'VENDOR');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST', 'OPEN', 'Aberto'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST' AND code = 'OPEN');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST', 'SENT_TO_VENDORS', 'Enviado aos fornecedores'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST' AND code = 'SENT_TO_VENDORS');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST', 'WAITING_QUOTES', 'Aguardando cotacoes'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST' AND code = 'WAITING_QUOTES');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST', 'CLOSED', 'Fechado'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST' AND code = 'CLOSED');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST_VENDOR', 'SENT', 'Enviado'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST_VENDOR' AND code = 'SENT');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST_VENDOR', 'VIEWED', 'Visualizado'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST_VENDOR' AND code = 'VIEWED');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST_VENDOR', 'RESPONDED', 'Respondeu'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST_VENDOR' AND code = 'RESPONDED');

INSERT INTO statuses (type, code, name)
SELECT 'BUDGET_REQUEST_VENDOR', 'DECLINED', 'Recusado'
WHERE NOT EXISTS (SELECT 1 FROM statuses WHERE type = 'BUDGET_REQUEST_VENDOR' AND code = 'DECLINED');

ALTER TABLE users ADD COLUMN IF NOT EXISTS role_id BIGINT;

UPDATE users u
SET role_id = r.id
FROM roles r
WHERE u.role_id IS NULL
  AND u.role = r.code;

ALTER TABLE users
    ALTER COLUMN role_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT fk_users_role_id FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users DROP COLUMN role;

ALTER TABLE budget_requests ADD COLUMN IF NOT EXISTS status_id BIGINT;

UPDATE budget_requests br
SET status_id = s.id
FROM statuses s
WHERE br.status_id IS NULL
  AND s.type = 'BUDGET_REQUEST'
  AND br.status = s.code;

ALTER TABLE budget_requests
    ALTER COLUMN status_id SET NOT NULL;

ALTER TABLE budget_requests
    ADD CONSTRAINT fk_budget_requests_status_id FOREIGN KEY (status_id) REFERENCES statuses (id);

ALTER TABLE budget_requests DROP COLUMN status;

ALTER TABLE budget_request_vendors ADD COLUMN IF NOT EXISTS status_id BIGINT;

UPDATE budget_request_vendors brv
SET status_id = s.id
FROM statuses s
WHERE brv.status_id IS NULL
  AND s.type = 'BUDGET_REQUEST_VENDOR'
  AND brv.status = s.code;

ALTER TABLE budget_request_vendors
    ALTER COLUMN status_id SET NOT NULL;

ALTER TABLE budget_request_vendors
    ADD CONSTRAINT fk_budget_request_vendors_status_id FOREIGN KEY (status_id) REFERENCES statuses (id);

ALTER TABLE budget_request_vendors DROP COLUMN status;
