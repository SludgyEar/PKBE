CREATE TABLE blacklisted_tokens(
    id VARCHAR(40) PRIMARY KEY,
    expiration_date TIMESTAMPTZ NOT NULL
);