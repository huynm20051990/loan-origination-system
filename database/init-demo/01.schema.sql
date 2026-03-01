-- schema.sql: Creates the homes table

-- Enable the pgvector extension to allow vector data types and operators
CREATE EXTENSION IF NOT EXISTS vector;

-- Drop the table first if you want to start fresh (optional)
-- DROP TABLE IF EXISTS homes;

CREATE TABLE IF NOT EXISTS homes (
    id UUID PRIMARY KEY,
    price NUMERIC(38,2) NOT NULL,
    beds INT,
    baths DOUBLE PRECISION,
    sqft INT,
    image_url VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state_code CHAR(2) NOT NULL,
    zip_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE home_embeddings (
    id UUID PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding vector(768)
);

CREATE INDEX IF NOT EXISTS home_embeddings_embedding_idx
ON home_embeddings
USING hnsw (embedding vector_cosine_ops);


-- Optional: indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_home_status ON homes(status);
CREATE INDEX IF NOT EXISTS idx_home_price ON homes(price);
CREATE INDEX IF NOT EXISTS idx_home_city ON homes(city);
CREATE INDEX IF NOT EXISTS idx_home_zip ON homes(zip_code);
CREATE INDEX IF NOT EXISTS idx_homes_price_beds ON homes(price, beds);

-- Create the Applications Table
CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY,
    application_number VARCHAR(50) NOT NULL UNIQUE,
    home_id UUID NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    -- Flattened Personal & Identity Info
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    date_of_birth DATE NOT NULL, -- Added to match new stepper
    ssn VARCHAR(20) NOT NULL,

    -- Loan Details
    loan_amount NUMERIC(15, 2) NOT NULL,
    loan_purpose TEXT,

    -- Optional: Ensure status stays within expected values
    CONSTRAINT chk_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'SUBMITTED'))
);

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