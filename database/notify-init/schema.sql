CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    application_number VARCHAR(50) NOT NULL,
    recipient_identifier VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_notifications_app_number ON notifications(application_number);

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