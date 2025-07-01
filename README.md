# Flowable Spring Boot Application

A comprehensive Spring Boot application integrated with the Flowable workflow engine, demonstrating business process management capabilities.

## Features

- **Spring Boot 3.2.0** with Java 17
- **Flowable 7.0.1** workflow engine integration
- **REST API** for workflow operations
- **Example BPMN Processes**: Simple approval and vacation request workflows
- **H2 In-memory Database** for development
- **Spring Security** integration with role-based access
- **Service Task Delegates** for custom business logic
- **Comprehensive Testing** with JUnit 5

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone and navigate to the project directory**
2. **Build and run the application:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
3. **Access the application at:** http://localhost:8080

## User Accounts

The application comes with pre-configured users:

| Username | Password | Roles |
|----------|----------|-------|
| admin | password | ADMIN, FLOWABLE-USER |
| user | password | FLOWABLE-USER |

## Sample Processes

### 1. Simple Approval Process (`simpleApproval`)

A basic approval workflow with the following steps:
1. **Submit Request** (User Task)
2. **Manager Approval** (User Task)
3. **Decision Gateway** (Automatic)
4. **Send Email** (Service Task)
5. **End**

### 2. Vacation Request Process (`vacationRequest`)

A more complex workflow with conditional logic:
1. **Fill Vacation Form** (User Task)
2. **Days Gateway** (Automatic - checks vacation length)
3. **Manager Approval** (< 5 days) OR **HR Approval** (≥ 5 days)
4. **Final Decision Gateway** (Automatic)
5. **Send Confirmation/Denial Email** (Service Task)
6. **End**

## REST API Endpoints

### Public Endpoints (No Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/public/` | Application information |
| GET | `/api/public/health` | Health check |
| GET | `/api/public/info` | Detailed application info with all endpoints |
| POST | `/api/public/demo/start-simple-approval` | Start simple approval demo |
| POST | `/api/public/demo/start-vacation-request?days=3` | Start vacation request demo |

### Workflow API Endpoints (Authentication Required)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/workflow/process/start` | Start a new process instance |
| GET | `/api/workflow/process/{id}` | Get process instance details |
| GET | `/api/workflow/process/active` | Get all active process instances |
| DELETE | `/api/workflow/process/{id}` | Delete a process instance |
| GET | `/api/workflow/tasks/user/{assignee}` | Get tasks for a specific user |
| GET | `/api/workflow/tasks/unassigned` | Get all unassigned tasks |
| POST | `/api/workflow/tasks/{id}/complete` | Complete a task |
| POST | `/api/workflow/tasks/{id}/assign/{assignee}` | Assign a task to a user |
| GET | `/api/workflow/definitions` | Get all process definitions |

## API Usage Examples

### Start a Simple Approval Process
```bash
curl -X POST http://localhost:8080/api/public/demo/start-simple-approval
```

### Start a Vacation Request Process
```bash
curl -X POST "http://localhost:8080/api/public/demo/start-vacation-request?days=7"
```

### Get All Active Process Instances (with authentication)
```bash
curl -u admin:password http://localhost:8080/api/workflow/process/active
```

### Complete a Task (with authentication)
```bash
curl -X POST -u admin:password \
  -H "Content-Type: application/json" \
  -d '{"approved": true}' \
  http://localhost:8080/api/workflow/tasks/{taskId}/complete
```

## Configuration

### Database Configuration

The application uses H2 in-memory database by default. Access the H2 console at:
- **URL:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:flowable`
- **Username:** `sa`
- **Password:** `password`

### Flowable Configuration

Key configuration properties in `application.yml`:

```yaml
flowable:
  database-schema-update: true
  idm:
    enabled: true
    password-encoder: spring_bcrypt
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/flowable/
│   │   ├── FlowableApplication.java          # Main application class
│   │   ├── config/
│   │   │   └── SecurityConfig.java           # Security configuration
│   │   ├── controller/
│   │   │   ├── WorkflowController.java       # Workflow REST API
│   │   │   └── PublicController.java         # Public endpoints
│   │   ├── service/
│   │   │   └── WorkflowService.java          # Workflow service layer
│   │   ├── delegate/
│   │   │   └── EmailServiceTask.java         # Service task delegate
│   │   └── dto/                              # Data Transfer Objects
│   └── resources/
│       ├── application.yml                   # Application configuration
│       └── processes/                        # BPMN process definitions
│           ├── simple-approval.bpmn20.xml
│           └── vacation-request.bpmn20.xml
└── test/
    └── java/com/example/flowable/
        └── FlowableApplicationTests.java     # Integration tests
```

## Development Features

- **Hot Reload:** Spring Boot DevTools enabled
- **Logging:** Detailed logging for Flowable and application components
- **Testing:** Comprehensive integration tests
- **Security:** Role-based access control for API endpoints

## Building for Production

```bash
# Build the application
mvn clean package

# Run the JAR file
java -jar target/flowable-spring-boot-1.0.0.jar
```

## Extending the Application

### Adding New Process Definitions

1. Create BPMN files in `src/main/resources/processes/`
2. Use Flowable Modeler or any BPMN 2.0 compliant editor
3. Deploy automatically on application restart

### Creating Custom Service Tasks

1. Implement the `JavaDelegate` interface
2. Annotate with `@Component`
3. Reference in BPMN using `${beanName}` syntax

### Adding New REST Endpoints

1. Create controllers in the `controller` package
2. Use `@RestController` and appropriate security annotations
3. Inject `WorkflowService` for workflow operations

## Troubleshooting

### Common Issues

1. **Port 8080 already in use:** Change the port in `application.yml`
2. **Database connection issues:** Check H2 console connectivity
3. **Process deployment fails:** Validate BPMN syntax
4. **Authentication issues:** Verify username/password combinations

### Logs

Check application logs for detailed error information:
```bash
mvn spring-boot:run | grep -E "(ERROR|WARN|Flowable)"
```

## License

This project is for demonstration purposes and showcases the integration between Spring Boot and Flowable workflow engine.