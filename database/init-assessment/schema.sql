-- 1. Create Assessments Table
CREATE TABLE IF NOT EXISTS assessments (
    id UUID PRIMARY KEY,
    application_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    decision VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Indexes for Performance
CREATE INDEX IF NOT EXISTS idx_assessments_app_id ON assessments(application_id);

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

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE assessment_embeddings (
    id UUID PRIMARY KEY,
    content TEXT,
    metadata JSONB,
    embedding vector(768)
);