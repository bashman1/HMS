-- Add consultation and referral columns to patient_visits table
-- Migration V5

-- Add consultation columns
ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS diagnosis TEXT;

ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS treatment_plan TEXT;

ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS prescription TEXT;

-- Add referral columns
ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS referred_department_id BIGINT;

ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS referred_department_name VARCHAR(100);

ALTER TABLE patient.patient_visits
ADD COLUMN IF NOT EXISTS referral_reason TEXT;
