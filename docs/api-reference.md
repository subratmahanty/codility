# Heartbeat Clinical Management System - API Reference

## Overview

The Heartbeat API provides REST endpoints for managing clinical operations including patient registration, treatments, prescriptions, lab tests, and notifications. All endpoints require JWT authentication and implement role-based access control.

**Base URL**: `http://localhost:8080/api`

**Authentication**: Bearer token in Authorization header

**Content Type**: `application/json` for most endpoints, `multipart/form-data` for file uploads

## Authentication

### Login
```http
POST /api/auth/login
```

**Request Body:**
```json
{
  "username": "dr_sharma",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "username": "dr_sharma",
    "firstName": "Dr. Rajesh",
    "lastName": "Sharma",
    "role": "DOCTOR",
    "email": "dr.sharma@heartbeat.com"
  },
  "expiresAt": "2024-01-16T14:30:00Z"
}
```

### Register User (Admin only)
```http
POST /api/auth/register
```

**Roles Required**: `ADMIN`

**Request Body:**
```json
{
  "username": "new_user",
  "email": "user@heartbeat.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "NURSE",
  "phone": "+91-9876543210"
}
```

### Refresh Token
```http
POST /api/auth/refresh
```

**Request Body:**
```json
{
  "refreshToken": "refresh_token_here"
}
```

## Patient Management

### Register Patient
```http
POST /api/patients
```

**Roles Required**: `ADMIN`, `HELPDESK`, `NURSE`

**Request Body:**
```json
{
  "firstName": "Ramesh",
  "lastName": "Gupta",
  "dateOfBirth": "1985-06-15",
  "gender": "MALE",
  "phone": "+91-9123456780",
  "email": "ramesh.gupta@email.com",
  "address": "123, MG Road, Sector 15",
  "city": "Delhi",
  "state": "Delhi",
  "pincode": "110001",
  "emergencyContactName": "Sunita Gupta",
  "emergencyContactPhone": "+91-9123456781",
  "bloodGroup": "B+",
  "allergies": "None",
  "medicalHistory": "Hypertension since 2020",
  "identifiers": [
    {
      "type": "AADHAR",
      "value": "123456789012"
    }
  ]
}
```

**Response:**
```json
{
  "id": "650e8400-e29b-41d4-a716-446655440000",
  "firstName": "Ramesh",
  "lastName": "Gupta",
  "dateOfBirth": "1985-06-15",
  "gender": "MALE",
  "phone": "+91-9123456780",
  "email": "ramesh.gupta@email.com",
  "address": "123, MG Road, Sector 15",
  "city": "Delhi",
  "state": "Delhi",
  "pincode": "110001",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "identifiers": [
    {
      "type": "AADHAR",
      "value": "123456789012",
      "isVerified": false
    }
  ]
}
```

### Get Patient
```http
GET /api/patients/{id}
```

**Roles Required**: All staff roles

**Response:**
```json
{
  "id": "650e8400-e29b-41d4-a716-446655440000",
  "firstName": "Ramesh",
  "lastName": "Gupta",
  "dateOfBirth": "1985-06-15",
  "gender": "MALE",
  "phone": "+91-9123456780",
  "email": "ramesh.gupta@email.com",
  "address": "123, MG Road, Sector 15",
  "city": "Delhi",
  "state": "Delhi",
  "pincode": "110001",
  "emergencyContactName": "Sunita Gupta",
  "emergencyContactPhone": "+91-9123456781",
  "bloodGroup": "B+",
  "allergies": "None",
  "medicalHistory": "Hypertension since 2020",
  "photoUrl": "https://storage.com/patient-photos/uuid.jpg",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00Z",
  "identifiers": [
    {
      "type": "AADHAR",
      "value": "123456789012",
      "isVerified": true
    }
  ]
}
```

### Update Patient
```http
PUT /api/patients/{id}
```

**Roles Required**: `ADMIN`, `HELPDESK` (basic fields), `NURSE` (vitals), `DOCTOR` (medical notes)

**Request Body:** Same as register patient (partial updates allowed)

### Search Patients
```http
GET /api/patients
```

**Query Parameters:**
- `query` - Search term (name, phone, email)
- `uid` - Search by identifier value
- `identifierType` - Filter by identifier type (PAN, AADHAR, VOTER_ID)
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)
- `sort` - Sort field (default: createdAt)
- `direction` - Sort direction (ASC, DESC)

**Response:**
```json
{
  "content": [
    {
      "id": "650e8400-e29b-41d4-a716-446655440000",
      "firstName": "Ramesh",
      "lastName": "Gupta",
      "phone": "+91-9123456780",
      "email": "ramesh.gupta@email.com",
      "dateOfBirth": "1985-06-15",
      "gender": "MALE",
      "city": "Delhi",
      "createdAt": "2024-01-15T10:30:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### Search by UID
```http
GET /api/patients/search?uid=123456789012
```

**Response:** Patient object if found

### Upload Patient Photo
```http
POST /api/patients/{id}/photo
```

**Content Type**: `multipart/form-data`

**Form Data:**
- `photo` - Image file (JPEG, PNG)

**Response:**
```json
{
  "photoUrl": "https://storage.com/patient-photos/uuid.jpg",
  "fileId": "file-uuid"
}
```

### Generate Patient ID Card
```http
GET /api/patients/{id}/id-card
```

**Query Parameters:**
- `format` - Response format (pdf, html)

**Response:** PDF file or HTML for printing

### Add Patient Identifier
```http
POST /api/patients/{id}/identifiers
```

**Roles Required**: `ADMIN`, `HELPDESK`

**Request Body:**
```json
{
  "type": "PAN",
  "value": "ABCDE1234F"
}
```

## Vitals Management

### Record Patient Vitals
```http
POST /api/patients/{id}/vitals
```

**Roles Required**: `NURSE`, `DOCTOR`, `ADMIN`

**Request Body:**
```json
{
  "heightCm": 175.0,
  "weightKg": 78.5,
  "temperatureF": 98.6,
  "bloodPressureSystolic": 120,
  "bloodPressureDiastolic": 80,
  "heartRateBpm": 72,
  "respiratoryRate": 16,
  "oxygenSaturation": 98.5,
  "bloodSugar": 95.0,
  "notes": "Patient stable",
  "recordedDate": "2024-01-15"
}
```

### Get Patient Vitals
```http
GET /api/patients/{id}/vitals
```

**Query Parameters:**
- `from` - Start date (YYYY-MM-DD)
- `to` - End date (YYYY-MM-DD)
- `page` - Page number
- `size` - Page size

**Response:**
```json
{
  "content": [
    {
      "id": "vitals-uuid",
      "heightCm": 175.0,
      "weightKg": 78.5,
      "temperatureF": 98.6,
      "bloodPressureSystolic": 120,
      "bloodPressureDiastolic": 80,
      "heartRateBpm": 72,
      "respiratoryRate": 16,
      "oxygenSaturation": 98.5,
      "bloodSugar": 95.0,
      "notes": "Patient stable",
      "recordedDate": "2024-01-15",
      "recordedBy": {
        "id": "user-uuid",
        "firstName": "Priya",
        "lastName": "Verma",
        "role": "NURSE"
      },
      "createdAt": "2024-01-15T14:30:00Z"
    }
  ]
}
```

## Treatment Management

### Create Treatment
```http
POST /api/treatments
```

**Roles Required**: `DOCTOR`, `ADMIN`

**Request Body:**
```json
{
  "patientId": "650e8400-e29b-41d4-a716-446655440000",
  "diagnosis": "Hypertension - follow up",
  "symptoms": "Mild headache, elevated BP",
  "examinationNotes": "BP: 150/95, otherwise stable",
  "followUpDate": "2024-02-15",
  "prescriptionItems": [
    {
      "medicineId": "750e8400-e29b-41d4-a716-446655440003",
      "dosage": "1 tablet",
      "frequency": "Once daily",
      "duration": "30 days",
      "instructions": "Take in the morning with food",
      "quantity": 30
    }
  ],
  "labTestRequests": [
    {
      "labTestId": "850e8400-e29b-41d4-a716-446655440002",
      "notes": "Baseline lipid profile",
      "isUrgent": false
    }
  ]
}
```

**Response:**
```json
{
  "id": "a50e8400-e29b-41d4-a716-446655440000",
  "patient": {
    "id": "650e8400-e29b-41d4-a716-446655440000",
    "firstName": "Ramesh",
    "lastName": "Gupta"
  },
  "doctor": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "firstName": "Dr. Rajesh",
    "lastName": "Sharma"
  },
  "diagnosis": "Hypertension - follow up",
  "symptoms": "Mild headache, elevated BP",
  "examinationNotes": "BP: 150/95, otherwise stable",
  "status": "ACTIVE",
  "followUpDate": "2024-02-15",
  "treatmentDate": "2024-01-15",
  "createdAt": "2024-01-15T14:30:00Z",
  "prescriptionItems": [...],
  "labTestRequests": [...]
}
```

### Get Treatment
```http
GET /api/treatments/{id}
```

**Roles Required**: `DOCTOR`, `NURSE`, `ADMIN`

### Get Patient Treatments
```http
GET /api/patients/{id}/treatments
```

**Query Parameters:**
- `status` - Filter by status (ACTIVE, COMPLETED, CANCELLED)
- `from` - Start date
- `to` - End date
- `page` - Page number
- `size` - Page size

### Update Treatment Status
```http
PUT /api/treatments/{id}/status
```

**Roles Required**: `DOCTOR`, `ADMIN`

**Request Body:**
```json
{
  "status": "COMPLETED",
  "notes": "Treatment completed successfully"
}
```

## Medicine Management

### Create Medicine
```http
POST /api/medicine
```

**Roles Required**: `PHARMA_ADMIN`, `ADMIN`

**Request Body:**
```json
{
  "name": "Paracetamol",
  "genericName": "Acetaminophen",
  "manufacturer": "Generic Pharma",
  "dosageForm": "Tablet",
  "strength": "500",
  "unit": "mg",
  "description": "Pain relief and fever reducer"
}
```

### Update Medicine
```http
PUT /api/medicine/{id}
```

**Roles Required**: `PHARMA_ADMIN`, `ADMIN`

### Get Medicines
```http
GET /api/medicine
```

**Query Parameters:**
- `query` - Search term
- `dosageForm` - Filter by dosage form
- `isActive` - Filter by active status
- `page` - Page number
- `size` - Page size

**Response:**
```json
{
  "content": [
    {
      "id": "750e8400-e29b-41d4-a716-446655440000",
      "name": "Paracetamol",
      "genericName": "Acetaminophen",
      "manufacturer": "Generic Pharma",
      "dosageForm": "Tablet",
      "strength": "500",
      "unit": "mg",
      "description": "Pain relief and fever reducer",
      "isActive": true,
      "createdAt": "2024-01-10T10:00:00Z"
    }
  ]
}
```

## Lab Test Management

### Create Lab Test
```http
POST /api/lab-tests
```

**Roles Required**: `PHARMA_ADMIN`, `ADMIN`

**Request Body:**
```json
{
  "testName": "Complete Blood Count",
  "testCode": "CBC",
  "category": "Hematology",
  "normalRangeMin": null,
  "normalRangeMax": null,
  "unit": null,
  "sampleType": "Blood",
  "preparationNotes": "No special preparation required",
  "cost": 250.00
}
```

### Get Lab Tests
```http
GET /api/lab-tests
```

**Query Parameters:**
- `query` - Search term
- `category` - Filter by category
- `sampleType` - Filter by sample type
- `isActive` - Filter by active status

### Submit Lab Result
```http
POST /api/lab-test-requests/{requestId}/result
```

**Roles Required**: `LAB_ADMIN`, `ADMIN`

**Request Body:**
```json
{
  "resultValue": "110",
  "resultText": "Fasting Blood Sugar: 110 mg/dL",
  "isNormal": false,
  "resultDate": "2024-01-23",
  "notes": "Slightly elevated, adjust medication"
}
```

### Get Lab Test Results
```http
GET /api/lab-test-requests/{requestId}/result
```

## File Attachments

### Upload Attachment
```http
POST /api/patients/{id}/attachments
```

**Content Type**: `multipart/form-data`

**Roles Required**: `NURSE`, `LAB_ADMIN`, `DOCTOR`, `ADMIN`

**Form Data:**
- `file` - File to upload
- `fileType` - Type (PHOTO, REPORT, PRESCRIPTION, LAB_RESULT, OTHER)
- `description` - File description
- `treatmentId` - Optional treatment ID
- `labResultId` - Optional lab result ID

**Response:**
```json
{
  "id": "file-uuid",
  "filename": "generated-filename.jpg",
  "originalFilename": "patient-report.jpg",
  "fileType": "REPORT",
  "fileSize": 1024000,
  "mimeType": "image/jpeg",
  "publicUrl": "https://storage.com/files/uuid.jpg",
  "description": "X-ray report",
  "uploadedBy": {
    "id": "user-uuid",
    "firstName": "Priya",
    "lastName": "Verma"
  },
  "createdAt": "2024-01-15T14:30:00Z"
}
```

### Get Attachment
```http
GET /api/attachments/{id}
```

**Response:** File download or metadata based on Accept header

### Get Patient Attachments
```http
GET /api/patients/{id}/attachments
```

**Query Parameters:**
- `fileType` - Filter by file type
- `treatmentId` - Filter by treatment
- `page` - Page number
- `size` - Page size

## Notifications

### Schedule Notification
```http
POST /api/notifications/schedule
```

**Roles Required**: `ADMIN`, `DOCTOR`, `NURSE`

**Request Body:**
```json
{
  "patientId": "650e8400-e29b-41d4-a716-446655440000",
  "templateId": "c50e8400-e29b-41d4-a716-446655440000",
  "recipient": "+91-9123456780",
  "channel": "SMS",
  "scheduledAt": "2024-02-14T10:00:00Z",
  "customMessage": "Custom message if not using template"
}
```

### Get Notifications
```http
GET /api/notifications
```

**Query Parameters:**
- `patientId` - Filter by patient
- `status` - Filter by status
- `channel` - Filter by channel
- `from` - Start date
- `to` - End date

### Create Notification Template
```http
POST /api/notifications/templates
```

**Roles Required**: `ADMIN`

**Request Body:**
```json
{
  "name": "Appointment Reminder",
  "subject": "Appointment Reminder",
  "messageTemplate": "Hello {{patientName}}, reminder for appointment on {{appointmentDate}}",
  "channel": "SMS"
}
```

## Dashboard & Analytics

### Get Dashboard Summary
```http
GET /api/dashboard/summary
```

**Query Parameters:**
- `from` - Start date (default: last month)
- `to` - End date (default: today)

**Response:**
```json
{
  "dateRange": {
    "from": "2024-01-01",
    "to": "2024-01-31"
  },
  "metrics": {
    "newPatients": 125,
    "totalVisits": 450,
    "prescriptionsIssued": 320,
    "labTestsRequested": 180,
    "labTestsCompleted": 165,
    "pendingFollowUps": 45
  },
  "topDiagnoses": [
    {
      "diagnosis": "Hypertension",
      "count": 89
    },
    {
      "diagnosis": "Type 2 Diabetes",
      "count": 67
    }
  ],
  "monthlyTrends": [
    {
      "month": "2024-01",
      "patients": 125,
      "visits": 450,
      "prescriptions": 320
    }
  ]
}
```

### Get Patient Statistics
```http
GET /api/dashboard/patients/stats
```

**Roles Required**: `ADMIN`, `DOCTOR`, `SUPERVISOR`

### Get Treatment Statistics
```http
GET /api/dashboard/treatments/stats
```

## Practice Templates

### Create Practice Template
```http
POST /api/practice-templates
```

**Roles Required**: `SUPERVISOR`, `ADMIN`

**Request Body:**
```json
{
  "name": "Hypertension Management",
  "diagnosis": "Essential Hypertension",
  "symptoms": "High blood pressure, headache, dizziness",
  "recommendedTests": ["850e8400-e29b-41d4-a716-446655440002"],
  "recommendedMedicines": ["750e8400-e29b-41d4-a716-446655440003"],
  "notes": "Monitor BP regularly, lifestyle modifications"
}
```

### Get Practice Templates
```http
GET /api/practice-templates
```

**Query Parameters:**
- `query` - Search term
- `diagnosis` - Filter by diagnosis
- `isActive` - Filter by active status

## Error Responses

All endpoints return standard HTTP status codes with JSON error responses:

```json
{
  "timestamp": "2024-01-15T14:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/patients",
  "details": [
    {
      "field": "firstName",
      "message": "First name is required"
    }
  ]
}
```

### Common Status Codes
- `200` - Success
- `201` - Created
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (missing/invalid token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found
- `409` - Conflict (duplicate data)
- `500` - Internal Server Error

## Rate Limiting

API endpoints are rate limited to prevent abuse:

- **Authentication endpoints**: 5 requests per minute per IP
- **File upload endpoints**: 10 requests per minute per user
- **Other endpoints**: 1000 requests per minute per user

Rate limit headers are included in responses:
- `X-RateLimit-Limit`: Request limit per window
- `X-RateLimit-Remaining`: Remaining requests in current window
- `X-RateLimit-Reset`: Window reset time (Unix timestamp)

## Webhooks (Future Enhancement)

The system supports webhooks for real-time notifications:

```http
POST /api/webhooks
```

**Event Types:**
- `patient.created`
- `treatment.created`
- `lab.result.ready`
- `notification.sent`