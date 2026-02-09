-- Add missing column to patient_visits table
ALTER TABLE patient.patient_visits ADD COLUMN IF NOT EXISTS is_billed BOOLEAN NOT NULL DEFAULT FALSE;

-- Also add missing columns to patients table if not exists
ALTER TABLE patient.patients ADD COLUMN IF NOT EXISTS mr_number VARCHAR(20);
ALTER TABLE patient.patients ADD COLUMN IF NOT EXISTS abha_id VARCHAR(20);
