-- Create the Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY,
    application_number VARCHAR(50) NOT NULL UNIQUE,
    home_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    -- Flattened Borrower Info
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    ssn VARCHAR(20) NOT NULL,
    annual_income NUMERIC(15, 2) NOT NULL,
    employer_name VARCHAR(255),

    -- Loan Details
    loan_amount NUMERIC(15, 2) NOT NULL,
    loan_purpose TEXT
);

--ALTER TABLE applications
--ADD COLUMN IF NOT EXISTS credit_score INT,
--ADD COLUMN IF NOT EXISTS credit_risk_tier VARCHAR(20),
--ADD COLUMN IF NOT EXISTS credit_checked_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_loan_app_number ON applications(application_number);

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