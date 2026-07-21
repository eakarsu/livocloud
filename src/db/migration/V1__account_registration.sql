CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    user_password VARCHAR(255),
    email VARCHAR(254) NOT NULL UNIQUE,
    account_state INTEGER NOT NULL,
    account_type VARCHAR(32) NOT NULL DEFAULT 'developer',
    company_name VARCHAR(160),
    address_country VARCHAR(80),
    billing_address VARCHAR(500),
    tax_number VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_account_state_check CHECK (account_state IN (0, 1, 2, 3, 11, 31, 32, 311))
);

CREATE TABLE pending_requests (
    req_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    email VARCHAR(254) NOT NULL,
    token CHAR(64) NOT NULL UNIQUE,
    status INTEGER NOT NULL DEFAULT 1,
    token_expire_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    consumed_at TIMESTAMP,
    CONSTRAINT pending_request_status_check CHECK (status IN (0, 1, 2))
);

CREATE INDEX pending_requests_email_status_idx
    ON pending_requests (email, status, token_expire_date);

CREATE TABLE account_audit (
    audit_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
    event_type VARCHAR(80) NOT NULL,
    occurred_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX account_audit_user_time_idx
    ON account_audit (user_id, occurred_at);

CREATE TABLE registration_attempts (
    attempt_id BIGSERIAL PRIMARY KEY,
    email_digest CHAR(64) NOT NULL,
    client_digest CHAR(64) NOT NULL,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX registration_attempts_email_time_idx
    ON registration_attempts (email_digest, attempted_at);

CREATE INDEX registration_attempts_client_time_idx
    ON registration_attempts (client_digest, attempted_at);
