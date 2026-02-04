-- 1. Enable pgvector if you plan to do AI-based risk similarity later
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Create the Credit Reports Table
CREATE TABLE IF NOT EXISTS credit_reports (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL, -- The link to the Application Service
    application_number VARCHAR(50) NOT NULL,

    -- Borrower Identity (Minimal)
    ssn_hash VARCHAR(255) NOT NULL, -- Store a hash for lookup, encrypt the actual SSN

    -- Financial Data
    credit_score INT NOT NULL CHECK (credit_score >= 300 AND credit_score <= 850),
    risk_tier VARCHAR(20) NOT NULL, -- PRIME, SUBPRIME, etc.

    -- The "Raw" Data
    -- We store the full response from the Credit Bureau (Experian/Equifax)
    -- here so we don't lose any detail for future audits.
    raw_bureau_data JSONB,

    -- AI/Vector Analysis (Optional placeholder)
    -- This stores an embedding of the borrower's financial behavior
    risk_embedding vector(1536),

    checked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Create a table to track Credit Bureau API hits (for billing/auditing)
CREATE TABLE IF NOT EXISTS bureau_invocation_logs (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    bureau_name VARCHAR(50) NOT NULL, -- 'EXPERIAN', 'EQUIFAX', 'MOCK'
    request_timestamp TIMESTAMP NOT NULL,
    response_status_code INT,
    latency_ms INT
);

-- 4. Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_credit_app_id ON credit_reports(application_id);
CREATE INDEX IF NOT EXISTS idx_credit_ssn_hash ON credit_reports(ssn_hash);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for performance (optional, but good for large volumes)
CREATE INDEX IF NOT EXISTS idx_outbox_created_at ON outbox (created_at);