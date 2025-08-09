-- Heartbeat Clinical Management System
-- Initial Database Schema Migration

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

-- User sessions
CREATE TABLE user_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address INET,
    user_agent TEXT
);

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

-- Patient identifiers
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

-- Medicine catalog
CREATE TABLE medicines (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    generic_name VARCHAR(255),
    manufacturer VARCHAR(255),
    dosage_form VARCHAR(100),
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
    sample_type VARCHAR(50),
    preparation_notes TEXT,
    cost DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Treatments
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

-- Prescription items
CREATE TABLE prescription_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    treatment_id UUID NOT NULL REFERENCES treatments(id) ON DELETE CASCADE,
    medicine_id UUID NOT NULL REFERENCES medicines(id),
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    quantity INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Lab test requests
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

-- File attachments
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
    storage_path TEXT NOT NULL,
    public_url TEXT,
    description TEXT,
    uploaded_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

-- Notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    patient_id UUID REFERENCES patients(id),
    treatment_id UUID REFERENCES treatments(id),
    template_id UUID REFERENCES notification_templates(id),
    recipient VARCHAR(255) NOT NULL,
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

-- Practice templates
CREATE TABLE practice_templates (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    diagnosis VARCHAR(255) NOT NULL,
    symptoms TEXT,
    recommended_tests UUID[] DEFAULT '{}',
    recommended_medicines UUID[] DEFAULT '{}',
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id)
);

-- Create indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_patients_name ON patients(first_name, last_name);
CREATE INDEX idx_patients_phone ON patients(phone);
CREATE INDEX idx_patient_identifiers_type_value ON patient_identifiers(identifier_type, identifier_value);
CREATE INDEX idx_treatments_patient_id ON treatments(patient_id);
CREATE INDEX idx_treatments_doctor_id ON treatments(doctor_id);
CREATE INDEX idx_treatments_date ON treatments(treatment_date);
CREATE INDEX idx_prescription_items_treatment_id ON prescription_items(treatment_id);
CREATE INDEX idx_lab_test_requests_treatment_id ON lab_test_requests(treatment_id);
CREATE INDEX idx_patient_vitals_patient_id ON patient_vitals(patient_id);
CREATE INDEX idx_file_attachments_patient_id ON file_attachments(patient_id);
CREATE INDEX idx_notifications_patient_id ON notifications(patient_id);
CREATE INDEX idx_notifications_scheduled_at ON notifications(scheduled_at);