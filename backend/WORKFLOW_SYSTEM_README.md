# 3-Level Workflow System with Flowable

## Overview

This is a comprehensive 3-level workflow system built with Spring Boot and Flowable that implements a maker-checker approval process across three levels (L1, L2, L3). Each level has makers and checkers who make decisions on workflow items, with escalation logic based on decision matching.

## System Architecture

### Workflow Levels

1. **L1 (Level 1)**: Initial decision level
   - **L1 Makers**: Make initial decisions on 3 items (FM or PM)
   - **L1 Checkers**: Review and validate maker decisions, can override if needed

2. **L2 (Level 2)**: Escalation level
   - **L2 Makers**: Handle escalated cases from L1
   - **L2 Checkers**: Review L2 maker decisions

3. **L3 (Level 3)**: Final decision level
   - **L3 Decision Makers**: Provide final resolution for cases escalated from L2

### Decision Logic

- Each case contains 3 decision items
- Each item requires a decision: **FM** (Financial Model) or **PM** (Portfolio Model)
- Makers submit decisions for all 3 items
- Checkers review and can either:
  - **Approve**: If all decisions match, case is completed
  - **Override**: Checker can change decisions and complete the case
  - **Escalate**: If decisions don't match, case moves to next level

### Escalation Rules

- **L1 → L2**: When L1 checker and maker decisions don't match
- **L2 → L3**: When L2 checker and maker decisions don't match
- **L3**: Final level - L3 decision maker provides ultimate resolution

## Technology Stack

- **Spring Boot 3.2.0** - Application framework
- **Flowable 7.0.1** - BPMN workflow engine with DMN and Forms
- **PostgreSQL** - Production database
- **H2** - Development database
- **Spring Security** - Authentication and authorization
- **JWT** - Token-based authentication
- **Maven** - Build management

## Project Structure

```
backend/
├── src/main/java/com/workflowplatform/
│   ├── model/                 # Entity models
│   │   ├── User.java         # User entity with workflow roles
│   │   ├── WorkflowCase.java # Main workflow case entity
│   │   ├── DecisionItem.java # Individual decision items
│   │   └── WorkflowDecision.java # Decision audit trail
│   ├── repository/           # Data repositories
│   ├── service/              # Business logic
│   │   └── WorkflowService.java # Core workflow operations
│   ├── controller/           # REST API endpoints
│   │   └── WorkflowController.java # Workflow REST API
│   ├── dto/                  # Data transfer objects
│   └── config/               # Configuration classes
├── src/main/resources/
│   ├── processes/            # BPMN process definitions
│   │   └── three-level-workflow.bpmn20.xml
│   ├── dmn/                  # DMN decision tables
│   │   └── decision-validation.dmn
│   ├── forms/                # Flowable form definitions
│   │   ├── maker-decision-form.form
│   │   ├── checker-review-form.form
│   │   └── l3-final-decision-form.form
│   ├── application.yml       # Application configuration
│   └── data.sql             # Sample test data
```

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (for production)

### Development Setup

1. **Clone and navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Run with development profile (H2 database)**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/api/swagger-ui.html
   - H2 Console: http://localhost:8080/api/h2-console

### Production Setup

1. **Setup PostgreSQL database**
   ```sql
   CREATE DATABASE workflow_platform;
   CREATE USER workflow_user WITH PASSWORD 'workflow_password';
   GRANT ALL PRIVILEGES ON DATABASE workflow_platform TO workflow_user;
   ```

2. **Run with production profile**
   ```bash
   mvn spring-boot:run -Dspring.profiles.active=prod
   ```

## API Usage

### Authentication

All API endpoints require authentication. Use the following test users:

| Username | Password | Roles |
|----------|----------|--------|
| admin | password123 | ADMIN, PROCESS_MANAGER |
| l1maker1 | password123 | L1_MAKER, USER |
| l1checker1 | password123 | L1_CHECKER, USER |
| l2maker1 | password123 | L2_MAKER, USER |
| l2checker1 | password123 | L2_CHECKER, USER |
| l3decision1 | password123 | L3_DECISION_MAKER, USER |
| testuser1 | password123 | USER |

### Core API Endpoints

#### 1. Create a Workflow Case
```http
POST /api/workflow/cases
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "caseTitle": "Investment Portfolio Review Q1 2024",
  "description": "Quarterly review of investment portfolio allocations",
  "decisionItems": [
    {
      "itemName": "Equity Allocation Model",
      "description": "Selection of appropriate model for equity allocation"
    },
    {
      "itemName": "Fixed Income Strategy", 
      "description": "Determination of fixed income allocation model"
    },
    {
      "itemName": "Alternative Investment Framework",
      "description": "Model selection for alternative investments"
    }
  ]
}
```

#### 2. Get Available Tasks
```http
GET /api/workflow/tasks/group
Authorization: Bearer {jwt_token}
```

#### 3. Claim a Task
```http
POST /api/workflow/tasks/{taskId}/claim
Authorization: Bearer {jwt_token}
```

#### 4. Submit Maker Decision
```http
POST /api/workflow/decisions/maker
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "taskId": "task_id_here",
  "caseId": 1,
  "decisions": [
    {
      "itemId": 1,
      "decision": "FM",
      "comments": "Financial model is more appropriate for this scenario"
    },
    {
      "itemId": 2,
      "decision": "PM", 
      "comments": "Portfolio model better suits this requirement"
    },
    {
      "itemId": 3,
      "decision": "FM",
      "comments": "Financial model recommended"
    }
  ],
  "comments": "Overall maker assessment completed"
}
```

#### 5. Submit Checker Decision
```http
POST /api/workflow/decisions/checker
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "taskId": "task_id_here",
  "caseId": 1,
  "decisions": [
    {
      "itemId": 1,
      "decision": "FM",
      "comments": "Agree with maker decision",
      "override": false
    },
    {
      "itemId": 2,
      "decision": "FM",
      "comments": "Override to FM for consistency",
      "override": true
    },
    {
      "itemId": 3,
      "decision": "FM", 
      "comments": "Confirmed FM is correct",
      "override": false
    }
  ],
  "comments": "Checker review completed with one override"
}
```

## Workflow Process Flow

### 1. Case Creation
- User creates a case with 3 decision items
- Flowable process instance is started
- Case is assigned to L1 makers

### 2. L1 Processing
- **L1 Maker**: Makes decisions on all 3 items (FM or PM)
- **L1 Checker**: Reviews decisions and can:
  - Approve all (if matching) → Case completed
  - Override some decisions → Case completed
  - Escalate (if conflicting) → Move to L2

### 3. L2 Processing (if escalated from L1)
- **L2 Maker**: Makes fresh decisions on escalated items
- **L2 Checker**: Reviews decisions and can:
  - Approve all (if matching) → Case completed
  - Override some decisions → Case completed  
  - Escalate (if conflicting) → Move to L3

### 4. L3 Processing (if escalated from L2)
- **L3 Decision Maker**: Makes final decisions
- Case is completed regardless of previous decisions

## Database Schema

### Key Tables

- **users**: User accounts with authentication details
- **user_roles**: Role assignments for workflow levels
- **workflow_cases**: Main workflow case records
- **decision_items**: Individual items requiring decisions
- **workflow_decisions**: Audit trail of all decisions made

## Monitoring and Management

### Health Checks
- **Health Endpoint**: `/api/actuator/health`
- **Metrics**: `/api/actuator/metrics`

### Flowable Admin
- **Process Instances**: Monitor running workflows
- **Task Management**: View and manage user tasks
- **Process Definitions**: Deploy and manage BPMN processes

## Testing

### Sample Workflow Test Scenario

1. **Login as testuser1** and create a new case
2. **Login as l1maker1** and submit maker decisions
3. **Login as l1checker1** and review decisions:
   - If decisions match → Case completed
   - If decisions conflict → Escalates to L2
4. **Login as l2maker1** and handle escalated case
5. **Login as l2checker1** and review L2 decisions
6. **Login as l3decision1** for final resolution (if needed)

### Decision Scenarios

1. **Happy Path**: All decisions match at L1 → Case completed
2. **Single Override**: Checker overrides one decision → Case completed
3. **L1 Escalation**: Conflicting decisions → Escalate to L2
4. **L2 Escalation**: L2 conflicts → Escalate to L3
5. **L3 Resolution**: Final decision by L3 → Case completed

## Security Features

- **JWT Authentication**: Stateless authentication
- **Role-based Authorization**: Method-level security
- **Password Encryption**: BCrypt hashing
- **CORS Protection**: Configurable CORS policies

## Production Considerations

1. **Database**: Use PostgreSQL with connection pooling
2. **Logging**: Structured logging with file rotation
3. **Monitoring**: Actuator endpoints for health checks
4. **Security**: Strong JWT secrets and HTTPS
5. **Performance**: Database indexing and query optimization

## Troubleshooting

### Common Issues

1. **Task not appearing**: Check user roles and group assignments
2. **Process not starting**: Verify BPMN process deployment
3. **Decision validation**: Check DMN decision table rules
4. **Database connection**: Verify PostgreSQL configuration

### Useful Endpoints for Debugging

- **All Cases**: `GET /api/workflow/cases/my`
- **Case Details**: `GET /api/workflow/cases/{id}`
- **Decision Items**: `GET /api/workflow/cases/{id}/items`
- **Decision History**: `GET /api/workflow/cases/{id}/decisions`

This comprehensive workflow system provides a robust foundation for multi-level approval processes with full audit trails and flexible decision logic.