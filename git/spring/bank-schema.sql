-- Shared schema for all DEMO projects (bankdb)
-- All demo projects from Module 03 onwards use this exact schema.
-- Run once against bankdb, or let Spring Boot apply it via schema.sql on startup.

CREATE TABLE IF NOT EXISTS account (
    id             BIGINT         AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20)    NOT NULL UNIQUE,
    holder_name    VARCHAR(100)   NOT NULL,
    account_type   VARCHAR(20)    NOT NULL,      -- CURRENT, SAVINGS, ISA
    balance        DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    opened_date    DATE           NOT NULL
);

CREATE TABLE IF NOT EXISTS transaction (
    id               BIGINT         AUTO_INCREMENT PRIMARY KEY,
    account_id       BIGINT         NOT NULL,
    amount           DECIMAL(15, 2) NOT NULL,
    transaction_type VARCHAR(10)    NOT NULL,    -- CREDIT, DEBIT
    description      VARCHAR(200),
    transaction_date DATETIME       NOT NULL,
    CONSTRAINT fk_transaction_account FOREIGN KEY (account_id) REFERENCES account (id)
);
