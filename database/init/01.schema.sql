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
    description TEXT,
    embedding vector(768)
);

-- Optional: indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_home_status ON homes(status);
CREATE INDEX IF NOT EXISTS idx_home_price ON homes(price);
CREATE INDEX IF NOT EXISTS idx_home_city ON homes(city);
CREATE INDEX IF NOT EXISTS idx_home_zip ON homes(zip_code);
CREATE INDEX IF NOT EXISTS idx_homes_price_beds ON homes(price, beds);

-- 3. Create an HNSW index for high-performance semantic search
-- This allows the database to perform similarity searches significantly faster than a flat scan.
CREATE INDEX IF NOT EXISTS idx_homes_embedding
ON homes USING hnsw (embedding vector_cosine_ops);
