CREATE TABLE token_refresh(
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token TEXT NOT NULL,
    expiration_date TIMESTAMPTZ NOT NULL,
    user_id BIGINT NOT NULL,

    CONSTRAINT fk_token_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);