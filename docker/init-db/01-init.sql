-- HMS Authentication Service Database Initialization
-- This script runs on first container startup

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE hms_auth TO hms_user;

-- Create schema
CREATE SCHEMA IF NOT EXISTS auth;
GRANT ALL ON SCHEMA auth TO hms_user;

-- Set default schema
ALTER USER hms_user SET search_path TO auth, public;
