# Generic Workflow Platform

A comprehensive workflow platform built with Flowable (BPMN + DMN engine), Spring Boot, and React for creating, managing, and executing customizable business processes.

## Architecture Overview

### Backend (Spring Boot + Flowable)
- **Workflow Engine**: Flowable BPMN, DMN, and Form engines
- **REST APIs**: Complete API layer for workflow management
- **Security**: JWT-based authentication with Spring Security
- **Multi-tenancy**: Support for multi-tenant deployments
- **Database**: Process definitions, instances, and history

### Frontend (React)
- **Admin Panel**: BPMN/DMN model management
- **Form Builder**: Dynamic form creation and editing
- **Process Monitor**: Real-time process monitoring
- **Task Management**: User task interface
- **Form Renderer**: Dynamic form rendering

## Key Features

### 🔄 Workflow Management
- Dynamic BPMN/DMN deployment without restarts
- Process versioning and lifecycle management
- Real-time process monitoring and analytics
- Task assignment and user task management

### 📋 Form Management
- JSON-based form definitions
- Drag-and-drop form builder
- Dynamic form rendering
- Form validation and data binding

### 🔐 Security & Access Control
- JWT authentication
- Role-based access control (RBAC)
- API security with Spring Security
- Multi-tenant isolation

### 🚀 Integration & Deployment
- RESTful API for external system integration
- Docker containerization
- CI/CD pipeline support
- Horizontal scaling capabilities

## Quick Start

### Prerequisites
- Java 17+
- Node.js 18+
- Docker (optional)
- PostgreSQL/MySQL (or H2 for development)

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

### Docker Setup
```bash
docker-compose up -d
```

## Project Structure

```
├── backend/                 # Spring Boot application
│   ├── src/main/java/      # Java source code
│   ├── src/main/resources/ # Configuration files
│   └── pom.xml             # Maven dependencies
├── frontend/               # React application
│   ├── src/                # React source code
│   ├── public/             # Static assets
│   └── package.json        # NPM dependencies
├── docker/                 # Docker configuration
├── docs/                   # Documentation
└── examples/               # Sample BPMN/DMN files
```

## API Endpoints

### Process Management
- `POST /api/processes/deploy` - Deploy BPMN/DMN models
- `GET /api/processes` - List process definitions
- `POST /api/processes/{id}/start` - Start process instance
- `GET /api/processes/{id}/instances` - Get process instances

### Task Management
- `GET /api/tasks` - Get user tasks
- `POST /api/tasks/{id}/complete` - Complete task
- `GET /api/tasks/{id}/form` - Get task form

### Form Management
- `POST /api/forms` - Create/update form definition
- `GET /api/forms/{id}` - Get form definition
- `POST /api/forms/{id}/render` - Render form

## Technology Stack

- **Backend**: Spring Boot 3, Flowable 7, Spring Security, Spring Data JPA
- **Frontend**: React 18, TypeScript, Material-UI, React Query
- **Database**: PostgreSQL (configurable)
- **Build Tools**: Maven, npm/yarn
- **Containerization**: Docker, Docker Compose

## License

MIT License