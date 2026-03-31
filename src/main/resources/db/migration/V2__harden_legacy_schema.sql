CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(255) NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    user_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    source_account_id BIGINT NOT NULL,
    target_account_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    title VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

DO $$
BEGIN
    IF to_regclass('public."transaction"') IS NOT NULL
       AND to_regclass('public.transactions') IS NULL THEN
        ALTER TABLE public."transaction" RENAME TO transactions;
    END IF;
END $$;

ALTER TABLE accounts
    ADD COLUMN IF NOT EXISTS account_number VARCHAR(255),
    ADD COLUMN IF NOT EXISTS balance DECIMAL(19, 2),
    ADD COLUMN IF NOT EXISTS currency VARCHAR(10),
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS source_account_id BIGINT,
    ADD COLUMN IF NOT EXISTS target_account_id BIGINT,
    ADD COLUMN IF NOT EXISTS amount DECIMAL(19, 2),
    ADD COLUMN IF NOT EXISTS title VARCHAR(255),
    ADD COLUMN IF NOT EXISTS timestamp TIMESTAMP;

UPDATE transactions
SET timestamp = CURRENT_TIMESTAMP
WHERE timestamp IS NULL;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM accounts WHERE user_id IS NULL) THEN
        DELETE FROM accounts WHERE user_id IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM transactions WHERE source_account_id IS NULL OR target_account_id IS NULL) THEN
        DELETE FROM transactions WHERE source_account_id IS NULL OR target_account_id IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM transactions WHERE amount IS NULL) THEN
        DELETE FROM transactions WHERE amount IS NULL;
    END IF;

    IF EXISTS (SELECT 1 FROM transactions WHERE title IS NULL) THEN
        DELETE FROM transactions WHERE title IS NULL;
    END IF;
END $$;

ALTER TABLE accounts ALTER COLUMN account_number SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN balance SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN currency SET NOT NULL;
ALTER TABLE accounts ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE transactions ALTER COLUMN source_account_id SET NOT NULL;
ALTER TABLE transactions ALTER COLUMN target_account_id SET NOT NULL;
ALTER TABLE transactions ALTER COLUMN amount SET NOT NULL;
ALTER TABLE transactions ALTER COLUMN title SET NOT NULL;
ALTER TABLE transactions ALTER COLUMN timestamp SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'uk_accounts_account_number'
    ) THEN
        ALTER TABLE accounts
            ADD CONSTRAINT uk_accounts_account_number UNIQUE (account_number);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_accounts_user'
    ) THEN
        ALTER TABLE accounts
            ADD CONSTRAINT fk_accounts_user
            FOREIGN KEY (user_id) REFERENCES users(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_transactions_source_account'
    ) THEN
        ALTER TABLE transactions
            ADD CONSTRAINT fk_transactions_source_account
            FOREIGN KEY (source_account_id) REFERENCES accounts(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_transactions_target_account'
    ) THEN
        ALTER TABLE transactions
            ADD CONSTRAINT fk_transactions_target_account
            FOREIGN KEY (target_account_id) REFERENCES accounts(id);
    END IF;
END $$;
