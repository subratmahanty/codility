# Heartbeat Clinical Management System

A comprehensive role-based clinical system built with Spring Boot, React, and PostgreSQL for patient registration, treatments, medicines, lab management, notifications, file attachments, and dashboards.

## Architecture Overview

### System Design Principles
- **Role-Based Access Control (RBAC)**: 7 distinct roles with specific permissions
- **Patient Identity Management**: Unique patient identification using PAN/Aadhar/Voter ID
- **Secure File Storage**: Object storage (S3/MinIO) with metadata in database
- **JWT Security**: Token-based authentication with role verification
- **Scalable Architecture**: Modular monolith ready for microservice transition

### Technology Stack

**Backend:**
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Spring Security + JWT
- Flyway (Database migrations)
- MapStruct (DTO mapping)
- S3/MinIO (File storage)

**Frontend:**
- React 18
- React Router
- Axios
- React Hook Form
- Recharts (Dashboard)
- Role-based routing

**Infrastructure:**
- PostgreSQL Database
- S3/MinIO Object Storage
- SMTP/Twilio (Notifications)

## Role-Based Permissions

| Role | Permissions |
|------|------------|
| **Admin** | Full system administration, user management, view/edit all data |
| **Doctor** | View/edit treatments, create prescriptions, patient history |
| **PharmaAdmin** | Manage medicines & lab test definitions |
| **LabAdmin** | Manage lab test records, enter results |
| **Nurse** | Patient details, vitals, reports, workflow assistance |
| **Helpdesk** | Patient registration, search, scheduling |
| **Supervisor** | Create practice templates, view treatments |

## Project Structure

```
heartbeat/
├── backend/                    # Spring Boot backend
│   ├── heartbeat-api/         # REST Controllers
│   ├── heartbeat-service/     # Business Logic
│   ├── heartbeat-repo/        # JPA Entities & Repositories
│   ├── heartbeat-auth/        # Security & JWT
│   ├── heartbeat-notify/      # Notification Service
│   └── heartbeat-common/      # Shared DTOs & Utilities
├── frontend/                  # React frontend
│   ├── src/
│   │   ├── api/              # API clients
│   │   ├── auth/             # Authentication
│   │   ├── components/       # React components
│   │   ├── routes/           # Route definitions
│   │   └── utils/            # Utilities
├── database/                  # SQL scripts
│   ├── migrations/           # Flyway migrations
│   └── ddl/                  # Database schema
└── docs/                     # Documentation
```

## Key Features

### Patient Management
- **Registration**: Comprehensive patient registration with photo capture
- **Unique Identity**: PAN/Aadhar/Voter ID enforcement
- **Medical History**: Complete treatment and prescription history
- **File Attachments**: Photos, reports, and medical documents

### Treatment & Prescription
- **Electronic Prescriptions**: Digital prescription creation and management
- **Treatment History**: Complete medical record tracking
- **Practice Templates**: Standardized treatment protocols

### Laboratory Management
- **Test Definitions**: Configurable lab test catalog
- **Result Management**: Digital result entry and tracking
- **Report Generation**: Automated lab report generation

### Notifications
- **Appointment Reminders**: SMS/Email/WhatsApp notifications
- **Follow-up Alerts**: Automated patient follow-up reminders
- **Staff Notifications**: Real-time alerts for healthcare staff

### Dashboard & Analytics
- **Patient Metrics**: Registration and visit statistics
- **Treatment Analytics**: Prescription and diagnosis trends
- **Lab Statistics**: Test completion and pending metrics
- **Role-based Views**: Customized dashboards per user role

## Security Features
- JWT-based authentication
- Role-based access control
- Encrypted sensitive data
- Audit logging
- Secure file storage with signed URLs
- HTTPS enforcement

## Getting Started

### Prerequisites
- Java 17+
- Node.js 16+
- PostgreSQL 13+
- Maven 3.6+

### Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

### Database Setup
```bash
# Create database
createdb heartbeat

# Run migrations
cd backend
mvn flyway:migrate
```

## API Documentation

The REST API follows RESTful conventions with JWT authentication and role-based authorization.

**Base URL**: `http://localhost:8080/api`

**Authentication**: Bearer token in Authorization header

### Key Endpoints
- `POST /auth/login` - User authentication
- `GET /patients` - Patient search and listing
- `POST /patients` - Patient registration
- `POST /treatments` - Treatment creation
- `GET /dashboard/summary` - Dashboard metrics

See `docs/api-reference.md` for complete API documentation.

## Development Roadmap

### Phase 1: Core System (4-6 weeks)
- [ ] Database schema and migrations
- [ ] Authentication and authorization
- [ ] Patient registration and management
- [ ] Basic treatment recording

### Phase 2: Advanced Features (3-4 weeks)
- [ ] File upload and management
- [ ] Notification system
- [ ] Dashboard and reporting
- [ ] Prescription printing

### Phase 3: Enhancement (2-3 weeks)
- [ ] Advanced search and filtering
- [ ] Audit logging
- [ ] Performance optimization
- [ ] Mobile responsiveness

## Contributing

Please read our contributing guidelines and code of conduct before submitting pull requests.

## License

This project is licensed under the MIT License - see the LICENSE file for details.