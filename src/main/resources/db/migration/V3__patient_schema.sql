-- Patient Management Schema
-- Creates tables for patient registration and OPD visit management

-- Create patient schema if not exists
CREATE SCHEMA IF NOT EXISTS patient;

-- Patients table
CREATE TABLE IF NOT EXISTS patient.patients (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    uhid VARCHAR(20) NOT NULL UNIQUE,
    mr_number VARCHAR(20),
    first_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(10),
    blood_group VARCHAR(20),
    marital_status VARCHAR(20),
    nationality VARCHAR(50),
    religion VARCHAR(50),
    occupation VARCHAR(100),
    phone_primary VARCHAR(20) NOT NULL,
    phone_secondary VARCHAR(20),
    email VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    emergency_contact_relation VARCHAR(50),
    aadhaar_number VARCHAR(12),
    abha_id VARCHAR(20),
    passport_number VARCHAR(20),
    allergies TEXT,
    chronic_conditions TEXT,
    photo_url VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT
);

-- Indexes for patients table
CREATE INDEX IF NOT EXISTS idx_patients_uhid ON patient.patients(uhid);
CREATE INDEX IF NOT EXISTS idx_patients_phone ON patient.patients(phone_primary);
CREATE INDEX IF NOT EXISTS idx_patients_email ON patient.patients(email);
CREATE INDEX IF NOT EXISTS idx_patients_name ON patient.patients(last_name);
CREATE INDEX IF NOT EXISTS idx_patients_uuid ON patient.patients(uuid);
CREATE INDEX IF NOT EXISTS idx_patients_active ON patient.patients(is_active);

-- Departments table
CREATE TABLE IF NOT EXISTS patient.departments (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Insert default departments
INSERT INTO patient.departments (name, code, description) VALUES
('General Medicine', 'GEN-MED', 'General Medicine Department'),
('Cardiology', 'CARD', 'Cardiology Department'),
('Neurology', 'NEURO', 'Neurology Department'),
('Orthopedics', 'ORTHO', 'Orthopedics Department'),
('Pediatrics', 'PEDS', 'Pediatrics Department'),
('Gynecology', 'GYN', 'Gynecology Department'),
('Dermatology', 'DERM', 'Dermatology Department'),
('ENT', 'ENT', 'Ear, Nose and Throat Department'),
('Ophthalmology', 'EYE', 'Ophthalmology Department'),
('Psychiatry', 'PSYCH', 'Psychiatry Department')
ON CONFLICT (code) DO NOTHING;

-- Patient visits table (OPD visits)
CREATE TABLE IF NOT EXISTS patient.patient_visits (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    patient_id BIGINT NOT NULL REFERENCES patient.patients(id),
    department_id BIGINT NOT NULL REFERENCES patient.departments(id),
    visit_number VARCHAR(20) NOT NULL,
    visit_type VARCHAR(20) NOT NULL,
    visit_status VARCHAR(20) NOT NULL DEFAULT 'REGISTERED',
    priority VARCHAR(10) DEFAULT 'NORMAL',
    chief_complaint TEXT,
    vitals JSONB,
    notes TEXT,
    doctor_id BIGINT,
    queue_number INT,
    is_billed BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    checked_in_at TIMESTAMP,
    consultation_started_at TIMESTAMP,
    consultation_completed_at TIMESTAMP
);

-- Indexes for patient_visits table
CREATE INDEX IF NOT EXISTS idx_patient_visits_patient ON patient.patient_visits(patient_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_department ON patient.patient_visits(department_id);
CREATE INDEX IF NOT EXISTS idx_patient_visits_status ON patient.patient_visits(visit_status);
CREATE INDEX IF NOT EXISTS idx_patient_visits_date ON patient.patient_visits(created_at);
CREATE INDEX IF NOT EXISTS idx_patient_visits_queue ON patient.patient_visits(department_id, visit_status, queue_number);

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION patient.update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers for updated_at
DO $$
DECLARE
    tbl TEXT;
BEGIN
    FOR tbl IN
        SELECT table_name FROM information_schema.columns
        WHERE column_name = 'updated_at'
        AND table_schema = 'patient'
    LOOP
        EXECUTE format('DROP TRIGGER IF EXISTS update_%I ON patient.%I', tbl, tbl);
        EXECUTE format('CREATE TRIGGER update_%I BEFORE UPDATE ON patient.%I FOR EACH ROW EXECUTE FUNCTION patient.update_timestamp()', tbl, tbl);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
