-- 1. Table for Credit Results
CREATE TABLE IF NOT EXISTS credit_reports (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    application_number VARCHAR(50) NOT NULL,
    ssn_hash VARCHAR(255) NOT NULL,
    credit_score INTEGER NOT NULL,
    risk_tier VARCHAR(20) NOT NULL,
    checked_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- 4. Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_credit_app_id ON credit_reports(application_id);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id   VARCHAR(255) NOT NULL,
    type           VARCHAR(255) NOT NULL,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for performance (optional, but good for large volumes)
CREATE INDEX IF NOT EXISTS idx_outbox_aggregate_id ON outbox(created_at);