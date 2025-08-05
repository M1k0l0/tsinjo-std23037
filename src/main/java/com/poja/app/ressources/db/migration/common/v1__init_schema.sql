CREATE TABLE donor (
                       id BIGSERIAL PRIMARY KEY,
                       email TEXT NOT NULL,
                       full_name TEXT NOT NULL
);

CREATE TABLE beneficiary (
                             id BIGSERIAL PRIMARY KEY,
                             email TEXT NOT NULL,
                             full_name TEXT NOT NULL
);

CREATE TYPE payment_state AS ENUM ('VERIFYING','SUCCEEDED','FAILED');

CREATE TABLE payment (
                         id BIGSERIAL PRIMARY KEY,
                         reference TEXT,
                         amount BIGINT NOT NULL,
                         method TEXT,
                         created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
                         state payment_state NOT NULL DEFAULT 'VERIFYING'
);

CREATE TABLE donation (
                          id BIGSERIAL PRIMARY KEY,
                          donor_id BIGINT REFERENCES donor(id),
                          payment_id BIGINT REFERENCES payment(id),
                          note TEXT,
                          created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE TABLE help (
                      id BIGSERIAL PRIMARY KEY,
                      beneficiary_id BIGINT REFERENCES beneficiary(id),
                      payment_id BIGINT REFERENCES payment(id),
                      description TEXT,
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT now()
);

CREATE INDEX idx_payment_state ON payment(state);
