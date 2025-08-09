-- Heartbeat Clinical Management System
-- Core Database Schema (PostgreSQL)

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create custom types
CREATE TYPE user_role AS ENUM ('ADMIN', 'DOCTOR', 'PHARMA_ADMIN', 'LAB_ADMIN', 'NURSE', 'HELPDESK', 'SUPERVISOR');
CREATE TYPE identifier_type AS ENUM ('PAN', 'AADHAR', 'VOTER_ID');
CREATE TYPE gender_type AS ENUM ('MALE', 'FEMALE', 'OTHER');
CREATE TYPE notification_status AS ENUM ('SCHEDULED', 'SENT', 'FAILED', 'CANCELLED');
CREATE TYPE notification_channel AS ENUM ('SMS', 'EMAIL', 'WHATSAPP');
CREATE TYPE treatment_status AS ENUM ('ACTIVE', 'COMPLETED', 'CANCELLED');
CREATE TYPE file_type AS ENUM ('PHOTO', 'REPORT', 'PRESCRIPTION', 'LAB_RESULT', 'OTHER');

-- =============================================
-- USERS AND AUTHENTICATION
-- =============================================

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role user_role NOT NULL,
    phone VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    last_login TIMESTAMP
);

-- User sessions (for JWT token management)
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT
);

-- =============================================
-- PATIENTS AND IDENTIFIERS
-- =============================================

-- Patients table
CREATE TABLE patients (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender gender_type NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    blood_group VARCHAR(5),
    allergies TEXT,
    medical_history TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    photo_url TEXT
);

-- Patient identifiers (PAN, Aadhar, Voter ID)
CREATE TABLE patient_identifiers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id) ON DELETE CASCADE,
    identifier_type identifier_type NOT NULL,
    identifier_value VARCHAR(50) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    verified_at TIMESTAMP,
    verified_by UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(identifier_type, identifier_value)
);

-- =============================================
-- MEDICAL DATA
-- =============================================

-- Medicine catalog
CREATE TABLE medicines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    manufacturer VARCHAR(255),
    dosage_form VARCHAR(100), -- tablet, syrup, injection, etc.
    strength VARCHAR(50),
    unit VARCHAR(20),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Lab test catalog
CREATE TABLE lab_tests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    test_name VARCHAR(255) NOT NULL,
    test_code VARCHAR(50) UNIQUE,
    category VARCHAR(100),
    normal_range_min DECIMAL(10,3),
    normal_range_max DECIMAL(10,3),
    unit VARCHAR(20),
    sample_type VARCHAR(50), -- blood, urine, etc.
    preparation_notes TEXT,
    cost DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Practice templates (disease/symptom → recommended tests/meds)
CREATE TABLE practice_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    symptoms TEXT,
    recommended_tests UUID[] DEFAULT '{}', -- Array of lab_test IDs
    recommended_medicines UUID[] DEFAULT '{}', -- Array of medicine IDs
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- =============================================
-- TREATMENTS AND PRESCRIPTIONS
-- =============================================

-- Treatments (main consultation record)
CREATE TABLE treatments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    doctor_id UUID NOT NULL REFERENCES users(id),
    diagnosis VARCHAR(500) NOT NULL,
    symptoms TEXT,
    examination_notes TEXT,
    status treatment_status DEFAULT 'ACTIVE',
    follow_up_date DATE,
    treatment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Prescription items (medicines prescribed in a treatment)
CREATE TABLE prescription_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    treatment_id UUID NOT NULL REFERENCES treatments(id) ON DELETE CASCADE,
    medicine_id UUID NOT NULL REFERENCES medicines(id),
    dosage VARCHAR(100) NOT NULL, -- "1 tablet"
    frequency VARCHAR(100) NOT NULL, -- "twice daily"
    duration VARCHAR(100) NOT NULL, -- "for 7 days"
    instructions TEXT,
    quantity INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Lab test requests (tests ordered in a treatment)
CREATE TABLE lab_test_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    treatment_id UUID NOT NULL REFERENCES treatments(id) ON DELETE CASCADE,
    lab_test_id UUID NOT NULL REFERENCES lab_tests(id),
    requested_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT,
    is_urgent BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Lab test results
CREATE TABLE lab_test_results (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    request_id UUID NOT NULL REFERENCES lab_test_requests(id) ON DELETE CASCADE,
    result_value VARCHAR(255),
    result_text TEXT,
    is_normal BOOLEAN,
    result_date DATE NOT NULL DEFAULT CURRENT_DATE,
    performed_by UUID REFERENCES users(id),
    reviewed_by UUID REFERENCES users(id),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- VITALS AND MEASUREMENTS
-- =============================================

-- Patient vitals
CREATE TABLE patient_vitals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID NOT NULL REFERENCES patients(id),
    height_cm DECIMAL(5,2),
    weight_kg DECIMAL(5,2),
    temperature_f DECIMAL(4,1),
    blood_pressure_systolic INTEGER,
    blood_pressure_diastolic INTEGER,
    heart_rate_bpm INTEGER,
    respiratory_rate INTEGER,
    oxygen_saturation DECIMAL(5,2),
    blood_sugar DECIMAL(6,2),
    notes TEXT,
    recorded_date DATE NOT NULL DEFAULT CURRENT_DATE,
    recorded_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- FILE ATTACHMENTS
-- =============================================

-- File attachments (photos, reports, etc.)
CREATE TABLE file_attachments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id),
    treatment_id UUID REFERENCES treatments(id),
    lab_result_id UUID REFERENCES lab_test_results(id),
    filename VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    file_type file_type NOT NULL,
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    storage_path TEXT NOT NULL, -- S3/MinIO path
    public_url TEXT,
    description TEXT,
    uploaded_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- NOTIFICATIONS
-- =============================================

-- Notification templates
CREATE TABLE notification_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message_template TEXT NOT NULL,
    channel notification_channel NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Scheduled notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id),
    treatment_id UUID REFERENCES treatments(id),
    template_id UUID REFERENCES notification_templates(id),
    recipient VARCHAR(255) NOT NULL, -- phone/email
    channel notification_channel NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    scheduled_at TIMESTAMP NOT NULL,
    sent_at TIMESTAMP,
    status notification_status DEFAULT 'SCHEDULED',
    retry_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- AUDIT AND LOGGING
-- =============================================

-- Audit log for tracking data changes
CREATE TABLE audit_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    table_name VARCHAR(100) NOT NULL,
    record_id UUID NOT NULL,
    action VARCHAR(20) NOT NULL, -- INSERT, UPDATE, DELETE
    old_values JSONB,
    new_values JSONB,
    changed_by UUID REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT
);

-- =============================================
-- INDEXES FOR PERFORMANCE
-- =============================================

-- User indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);

-- Patient indexes
CREATE INDEX idx_patients_name ON patients(first_name, last_name);
CREATE INDEX idx_patients_phone ON patients(phone);
CREATE INDEX idx_patients_created_at ON patients(created_at);
CREATE INDEX idx_patient_identifiers_type_value ON patient_identifiers(identifier_type, identifier_value);
CREATE INDEX idx_patient_identifiers_patient_id ON patient_identifiers(patient_id);

-- Treatment indexes
CREATE INDEX idx_treatments_patient_id ON treatments(patient_id);
CREATE INDEX idx_treatments_doctor_id ON treatments(doctor_id);
CREATE INDEX idx_treatments_date ON treatments(treatment_date);
CREATE INDEX idx_treatments_status ON treatments(status);

-- Prescription indexes
CREATE INDEX idx_prescription_items_treatment_id ON prescription_items(treatment_id);
CREATE INDEX idx_prescription_items_medicine_id ON prescription_items(medicine_id);

-- Lab test indexes
CREATE INDEX idx_lab_test_requests_treatment_id ON lab_test_requests(treatment_id);
CREATE INDEX idx_lab_test_requests_lab_test_id ON lab_test_requests(lab_test_id);
CREATE INDEX idx_lab_test_results_request_id ON lab_test_results(request_id);

-- Vitals indexes
CREATE INDEX idx_patient_vitals_patient_id ON patient_vitals(patient_id);
CREATE INDEX idx_patient_vitals_date ON patient_vitals(recorded_date);

-- File attachment indexes
CREATE INDEX idx_file_attachments_patient_id ON file_attachments(patient_id);
CREATE INDEX idx_file_attachments_treatment_id ON file_attachments(treatment_id);
CREATE INDEX idx_file_attachments_type ON file_attachments(file_type);

-- Notification indexes
CREATE INDEX idx_notifications_patient_id ON notifications(patient_id);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);
CREATE INDEX idx_notifications_status ON notifications(status);

-- Audit log indexes
CREATE INDEX idx_audit_log_table_record ON audit_log(table_name, record_id);
CREATE INDEX idx_audit_log_changed_at ON audit_log(changed_at);
CREATE INDEX idx_audit_log_changed_by ON audit_log(changed_by);

-- =============================================
-- TRIGGERS FOR AUDIT LOGGING
-- =============================================

-- Function to handle audit logging
CREATE OR REPLACE FUNCTION audit_trigger_function()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'DELETE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, changed_by)
        VALUES (TG_TABLE_NAME, OLD.id, TG_OP, to_jsonb(OLD), 
                COALESCE(current_setting('app.current_user_id', true)::UUID, NULL));
        RETURN OLD;
    ELSIF TG_OP = 'UPDATE' THEN
        INSERT INTO audit_log (table_name, record_id, action, old_values, new_values, changed_by)
        VALUES (TG_TABLE_NAME, NEW.id, TG_OP, to_jsonb(OLD), to_jsonb(NEW),
                COALESCE(current_setting('app.current_user_id', true)::UUID, NULL));
        RETURN NEW;
    ELSIF TG_OP = 'INSERT' THEN
        INSERT INTO audit_log (table_name, record_id, action, new_values, changed_by)
        VALUES (TG_TABLE_NAME, NEW.id, TG_OP, to_jsonb(NEW),
                COALESCE(current_setting('app.current_user_id', true)::UUID, NULL));
        RETURN NEW;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Create audit triggers for key tables
CREATE TRIGGER audit_patients_trigger
    AFTER INSERT OR UPDATE OR DELETE ON patients
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_treatments_trigger
    AFTER INSERT OR UPDATE OR DELETE ON treatments
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();

CREATE TRIGGER audit_users_trigger
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION audit_trigger_function();