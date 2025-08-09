-- Heartbeat Clinical Management System
-- Sample Data for Testing

-- Insert sample users
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, phone, is_active) VALUES
-- Admin user
('550e8400-e29b-41d4-a716-446655440000', 'admin', 'admin@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'System', 'Administrator', 'ADMIN', '+91-9876543210', true),

-- Doctor
('550e8400-e29b-41d4-a716-446655440001', 'dr_sharma', 'dr.sharma@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Dr. Rajesh', 'Sharma', 'DOCTOR', '+91-9876543211', true),

-- Nurse
('550e8400-e29b-41d4-a716-446655440002', 'nurse_priya', 'priya@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Priya', 'Verma', 'NURSE', '+91-9876543212', true),

-- Pharmacy Admin
('550e8400-e29b-41d4-a716-446655440003', 'pharma_admin', 'pharma@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Ravi', 'Kumar', 'PHARMA_ADMIN', '+91-9876543213', true),

-- Lab Admin
('550e8400-e29b-41d4-a716-446655440004', 'lab_admin', 'lab@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Sunita', 'Patel', 'LAB_ADMIN', '+91-9876543214', true),

-- Helpdesk
('550e8400-e29b-41d4-a716-446655440005', 'helpdesk', 'helpdesk@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Amit', 'Singh', 'HELPDESK', '+91-9876543215', true),

-- Supervisor
('550e8400-e29b-41d4-a716-446655440006', 'supervisor', 'supervisor@heartbeat.com', '$2a$10$8k7gT3LYQz5eBJhZJ6c7D.XR9GYgW3d2FzL6J9C4K8e1H7Q5N2W3M', 'Dr. Meera', 'Joshi', 'SUPERVISOR', '+91-9876543216', true);

-- Insert sample patients
INSERT INTO patients (id, first_name, last_name, date_of_birth, gender, phone, email, address, city, state, pincode, emergency_contact_name, emergency_contact_phone, blood_group, allergies, medical_history, created_by) VALUES
('650e8400-e29b-41d4-a716-446655440000', 'Ramesh', 'Gupta', '1985-06-15', 'MALE', '+91-9123456780', 'ramesh.gupta@email.com', '123, MG Road, Sector 15', 'Delhi', 'Delhi', '110001', 'Sunita Gupta', '+91-9123456781', 'B+', 'None', 'Hypertension since 2020', '550e8400-e29b-41d4-a716-446655440005'),

('650e8400-e29b-41d4-a716-446655440001', 'Kavita', 'Sharma', '1992-03-22', 'FEMALE', '+91-9123456782', 'kavita.sharma@email.com', '456, Park Street, Block A', 'Mumbai', 'Maharashtra', '400001', 'Raj Sharma', '+91-9123456783', 'A+', 'Penicillin allergy', 'Diabetes Type 2', '550e8400-e29b-41d4-a716-446655440005'),

('650e8400-e29b-41d4-a716-446655440002', 'Arjun', 'Patel', '1978-11-08', 'MALE', '+91-9123456784', 'arjun.patel@email.com', '789, Gandhi Nagar, Phase 2', 'Bangalore', 'Karnataka', '560001', 'Meena Patel', '+91-9123456785', 'O+', 'Dust allergy', 'Asthma', '550e8400-e29b-41d4-a716-446655440005');

-- Insert patient identifiers
INSERT INTO patient_identifiers (patient_id, identifier_type, identifier_value, is_verified, verified_by) VALUES
('650e8400-e29b-41d4-a716-446655440000', 'AADHAR', '123456789012', true, '550e8400-e29b-41d4-a716-446655440005'),
('650e8400-e29b-41d4-a716-446655440000', 'PAN', 'ABCDE1234F', true, '550e8400-e29b-41d4-a716-446655440005'),
('650e8400-e29b-41d4-a716-446655440001', 'AADHAR', '234567890123', true, '550e8400-e29b-41d4-a716-446655440005'),
('650e8400-e29b-41d4-a716-446655440002', 'VOTER_ID', 'DEL1234567890', true, '550e8400-e29b-41d4-a716-446655440005');

-- Insert sample medicines
INSERT INTO medicines (id, name, generic_name, manufacturer, dosage_form, strength, unit, description, created_by) VALUES
('750e8400-e29b-41d4-a716-446655440000', 'Paracetamol', 'Acetaminophen', 'Generic Pharma', 'Tablet', '500', 'mg', 'Pain relief and fever reducer', '550e8400-e29b-41d4-a716-446655440003'),
('750e8400-e29b-41d4-a716-446655440001', 'Amoxicillin', 'Amoxicillin', 'Beta Pharma', 'Capsule', '250', 'mg', 'Antibiotic for bacterial infections', '550e8400-e29b-41d4-a716-446655440003'),
('750e8400-e29b-41d4-a716-446655440002', 'Metformin', 'Metformin HCl', 'Diabetes Care', 'Tablet', '500', 'mg', 'Type 2 diabetes medication', '550e8400-e29b-41d4-a716-446655440003'),
('750e8400-e29b-41d4-a716-446655440003', 'Lisinopril', 'Lisinopril', 'CardioMed', 'Tablet', '10', 'mg', 'ACE inhibitor for hypertension', '550e8400-e29b-41d4-a716-446655440003'),
('750e8400-e29b-41d4-a716-446655440004', 'Salbutamol', 'Salbutamol Sulfate', 'RespiCare', 'Inhaler', '100', 'mcg', 'Bronchodilator for asthma', '550e8400-e29b-41d4-a716-446655440003');

-- Insert sample lab tests
INSERT INTO lab_tests (id, test_name, test_code, category, normal_range_min, normal_range_max, unit, sample_type, preparation_notes, cost, created_by) VALUES
('850e8400-e29b-41d4-a716-446655440000', 'Complete Blood Count', 'CBC', 'Hematology', NULL, NULL, NULL, 'Blood', 'No special preparation required', 250.00, '550e8400-e29b-41d4-a716-446655440004'),
('850e8400-e29b-41d4-a716-446655440001', 'Fasting Blood Sugar', 'FBS', 'Biochemistry', 70.0, 100.0, 'mg/dL', 'Blood', 'Fast for 8-12 hours', 150.00, '550e8400-e29b-41d4-a716-446655440004'),
('850e8400-e29b-41d4-a716-446655440002', 'Lipid Profile', 'LIPID', 'Biochemistry', NULL, NULL, 'mg/dL', 'Blood', 'Fast for 12 hours', 400.00, '550e8400-e29b-41d4-a716-446655440004'),
('850e8400-e29b-41d4-a716-446655440003', 'Liver Function Test', 'LFT', 'Biochemistry', NULL, NULL, 'U/L', 'Blood', 'No special preparation', 350.00, '550e8400-e29b-41d4-a716-446655440004'),
('850e8400-e29b-41d4-a716-446655440004', 'Urine Analysis', 'URINE', 'Pathology', NULL, NULL, NULL, 'Urine', 'Mid-stream sample', 100.00, '550e8400-e29b-41d4-a716-446655440004');

-- Insert practice templates
INSERT INTO practice_templates (id, name, diagnosis, symptoms, recommended_tests, recommended_medicines, notes, created_by) VALUES
('950e8400-e29b-41d4-a716-446655440000', 'Hypertension Management', 'Essential Hypertension', 'High blood pressure, headache, dizziness', 
 ARRAY['850e8400-e29b-41d4-a716-446655440002'], 
 ARRAY['750e8400-e29b-41d4-a716-446655440003'], 
 'Monitor BP regularly, lifestyle modifications', '550e8400-e29b-41d4-a716-446655440006'),

('950e8400-e29b-41d4-a716-446655440001', 'Type 2 Diabetes Care', 'Type 2 Diabetes Mellitus', 'Increased thirst, frequent urination, fatigue', 
 ARRAY['850e8400-e29b-41d4-a716-446655440001'], 
 ARRAY['750e8400-e29b-41d4-a716-446655440002'], 
 'Diet control, regular exercise, medication compliance', '550e8400-e29b-41d4-a716-446655440006'),

('950e8400-e29b-41d4-a716-446655440002', 'Respiratory Infection', 'Upper Respiratory Tract Infection', 'Cough, fever, sore throat, runny nose', 
 ARRAY['850e8400-e29b-41d4-a716-446655440000'], 
 ARRAY['750e8400-e29b-41d4-a716-446655440000', '750e8400-e29b-41d4-a716-446655440001'], 
 'Rest, adequate fluid intake, symptomatic treatment', '550e8400-e29b-41d4-a716-446655440006');

-- Insert sample treatments
INSERT INTO treatments (id, patient_id, doctor_id, diagnosis, symptoms, examination_notes, status, follow_up_date, treatment_date) VALUES
('a50e8400-e29b-41d4-a716-446655440000', '650e8400-e29b-41d4-a716-446655440000', '550e8400-e29b-41d4-a716-446655440001', 
 'Hypertension - follow up', 'Mild headache, BP elevated', 'BP: 150/95, otherwise stable', 'ACTIVE', '2024-02-15', '2024-01-15'),

('a50e8400-e29b-41d4-a716-446655440001', '650e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 
 'Diabetes Type 2 - routine check', 'Feeling tired, increased thirst', 'Blood sugar slightly elevated, weight stable', 'COMPLETED', '2024-03-22', '2024-01-22'),

('a50e8400-e29b-41d4-a716-446655440002', '650e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 
 'Asthma exacerbation', 'Wheezing, shortness of breath', 'Mild wheeze on auscultation, peak flow reduced', 'ACTIVE', '2024-02-08', '2024-01-25');

-- Insert prescription items
INSERT INTO prescription_items (treatment_id, medicine_id, dosage, frequency, duration, instructions, quantity) VALUES
('a50e8400-e29b-41d4-a716-446655440000', '750e8400-e29b-41d4-a716-446655440003', '1 tablet', 'Once daily', '30 days', 'Take in the morning with food', 30),
('a50e8400-e29b-41d4-a716-446655440001', '750e8400-e29b-41d4-a716-446655440002', '1 tablet', 'Twice daily', '30 days', 'Take with meals', 60),
('a50e8400-e29b-41d4-a716-446655440002', '750e8400-e29b-41d4-a716-446655440004', '2 puffs', 'As needed', '30 days', 'Use during breathing difficulty', 1);

-- Insert lab test requests
INSERT INTO lab_test_requests (id, treatment_id, lab_test_id, requested_date, notes, is_urgent) VALUES
('b50e8400-e29b-41d4-a716-446655440000', 'a50e8400-e29b-41d4-a716-446655440000', '850e8400-e29b-41d4-a716-446655440002', '2024-01-15', 'Baseline lipid profile', false),
('b50e8400-e29b-41d4-a716-446655440001', 'a50e8400-e29b-41d4-a716-446655440001', '850e8400-e29b-41d4-a716-446655440001', '2024-01-22', 'Monitor blood sugar levels', false),
('b50e8400-e29b-41d4-a716-446655440002', 'a50e8400-e29b-41d4-a716-446655440002', '850e8400-e29b-41d4-a716-446655440000', '2024-01-25', 'Check for infection', true);

-- Insert lab test results
INSERT INTO lab_test_results (request_id, result_value, result_text, is_normal, result_date, performed_by, reviewed_by, notes) VALUES
('b50e8400-e29b-41d4-a716-446655440001', '110', 'Fasting Blood Sugar: 110 mg/dL', false, '2024-01-23', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 'Slightly elevated, adjust medication');

-- Insert patient vitals
INSERT INTO patient_vitals (patient_id, height_cm, weight_kg, temperature_f, blood_pressure_systolic, blood_pressure_diastolic, heart_rate_bpm, respiratory_rate, oxygen_saturation, recorded_date, recorded_by, notes) VALUES
('650e8400-e29b-41d4-a716-446655440000', 175.0, 78.5, 98.6, 150, 95, 72, 16, 98.5, '2024-01-15', '550e8400-e29b-41d4-a716-446655440002', 'Patient stable'),
('650e8400-e29b-41d4-a716-446655440001', 162.0, 65.2, 98.4, 125, 80, 68, 14, 99.0, '2024-01-22', '550e8400-e29b-41d4-a716-446655440002', 'Normal vitals'),
('650e8400-e29b-41d4-a716-446655440002', 170.0, 72.0, 99.2, 130, 85, 75, 18, 96.0, '2024-01-25', '550e8400-e29b-41d4-a716-446655440002', 'Mild respiratory distress');

-- Insert notification templates
INSERT INTO notification_templates (id, name, subject, message_template, channel, created_by) VALUES
('c50e8400-e29b-41d4-a716-446655440000', 'Appointment Reminder', 'Appointment Reminder', 'Hello {{patientName}}, this is a reminder for your appointment on {{appointmentDate}} at {{appointmentTime}}. Please arrive 15 minutes early.', 'SMS', '550e8400-e29b-41d4-a716-446655440000'),
('c50e8400-e29b-41d4-a716-446655440001', 'Lab Result Ready', 'Lab Results Available', 'Dear {{patientName}}, your lab results are ready. Please visit the clinic or check your patient portal to view the results.', 'EMAIL', '550e8400-e29b-41d4-a716-446655440000'),
('c50e8400-e29b-41d4-a716-446655440002', 'Follow-up Reminder', 'Follow-up Required', 'Hello {{patientName}}, it is time for your follow-up appointment. Please contact us to schedule your next visit.', 'SMS', '550e8400-e29b-41d4-a716-446655440000');

-- Insert sample notifications
INSERT INTO notifications (patient_id, treatment_id, template_id, recipient, channel, subject, message, scheduled_at, status) VALUES
('650e8400-e29b-41d4-a716-446655440000', 'a50e8400-e29b-41d4-a716-446655440000', 'c50e8400-e29b-41d4-a716-446655440002', '+91-9123456780', 'SMS', 'Follow-up Required', 'Hello Ramesh Gupta, it is time for your follow-up appointment. Please contact us to schedule your next visit.', '2024-02-14 10:00:00', 'SCHEDULED'),
('650e8400-e29b-41d4-a716-446655440001', 'a50e8400-e29b-41d4-a716-446655440001', 'c50e8400-e29b-41d4-a716-446655440001', 'kavita.sharma@email.com', 'EMAIL', 'Lab Results Available', 'Dear Kavita Sharma, your lab results are ready. Please visit the clinic or check your patient portal to view the results.', '2024-01-24 09:00:00', 'SENT');