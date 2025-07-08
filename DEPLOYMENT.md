# Deployment Guide - Generic Workflow Platform

This guide provides step-by-step instructions for deploying the Generic Workflow Platform in different environments.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Quick Start with Docker](#quick-start-with-docker)
3. [Local Development Setup](#local-development-setup)
4. [Production Deployment](#production-deployment)
5. [Configuration](#configuration)
6. [Troubleshooting](#troubleshooting)

## Prerequisites

### System Requirements
- **Java**: 17 or higher
- **Node.js**: 18 or higher
- **Docker**: 20.10 or higher (for containerized deployment)
- **Database**: PostgreSQL 13+ (production) or H2 (development)

### Development Tools (Optional)
- Maven 3.9+
- npm/yarn
- Git

## Quick Start with Docker

The fastest way to get the platform running:

### 1. Clone and Setup
```bash
git clone <repository-url>
cd workflow-platform
cp .env.example .env
```

### 2. Configure Environment
Edit the `.env` file with your settings:
```bash
# Database
DB_PASSWORD=your_secure_password

# JWT
JWT_SECRET=your_jwt_secret_key_here

# Optional: Change ports if needed
```

### 3. Start Services
```bash
docker-compose up -d
```

### 4. Verify Deployment
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/api/swagger-ui.html
- **Database**: PostgreSQL on port 5432

### 5. Default Login
- Username: `admin`
- Password: `admin` (change immediately after first login)

## Local Development Setup

### Backend Setup

1. **Navigate to backend directory**:
   ```bash
   cd backend
   ```

2. **Run with Maven wrapper** (recommended):
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Or install Maven and run**:
   ```bash
   mvn spring-boot:run
   ```

4. **Backend will be available at**: http://localhost:8080

### Frontend Setup

1. **Navigate to frontend directory**:
   ```bash
   cd frontend
   ```

2. **Install dependencies**:
   ```bash
   npm install
   ```

3. **Start development server**:
   ```bash
   npm start
   ```

4. **Frontend will be available at**: http://localhost:3000

### Database Setup

For development, the application uses H2 in-memory database by default. For persistent storage:

1. **Install PostgreSQL**
2. **Create database**:
   ```sql
   CREATE DATABASE workflow_platform;
   CREATE USER workflow_user WITH PASSWORD 'workflow_password';
   GRANT ALL PRIVILEGES ON DATABASE workflow_platform TO workflow_user;
   ```

3. **Update application.yml** or set environment variables:
   ```yaml
   spring:
     profiles:
       active: prod
   ```

## Production Deployment

### Option 1: Docker Compose (Recommended)

1. **Prepare production environment**:
   ```bash
   cp .env.example .env
   # Edit .env with production values
   ```

2. **Deploy**:
   ```bash
   docker-compose -f docker-compose.yml up -d
   ```

3. **Set up reverse proxy** (Nginx example):
   ```nginx
   server {
       listen 80;
       server_name your-domain.com;
       
       location / {
           proxy_pass http://localhost:3000;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
       
       location /api/ {
           proxy_pass http://localhost:8080/api/;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
       }
   }
   ```

### Option 2: Manual Deployment

#### Backend Deployment

1. **Build the application**:
   ```bash
   cd backend
   ./mvnw clean package -DskipTests
   ```

2. **Deploy JAR file**:
   ```bash
   java -jar target/workflow-backend-1.0.0.jar \
     --spring.profiles.active=prod \
     --server.port=8080
   ```

3. **Set up as systemd service** (Linux):
   ```ini
   [Unit]
   Description=Workflow Platform Backend
   After=syslog.target network.target
   
   [Service]
   Type=simple
   User=workflow
   ExecStart=/usr/bin/java -jar /opt/workflow-platform/workflow-backend-1.0.0.jar
   SuccessExitStatus=143
   TimeoutStopSec=10
   Restart=on-failure
   RestartSec=5
   
   [Install]
   WantedBy=multi-user.target
   ```

#### Frontend Deployment

1. **Build the application**:
   ```bash
   cd frontend
   npm install
   npm run build
   ```

2. **Deploy to web server**:
   ```bash
   # Copy build files to web server
   cp -r build/* /var/www/html/
   ```

3. **Configure web server** (Nginx example):
   ```nginx
   server {
       listen 80;
       root /var/www/html;
       index index.html;
       
       location / {
           try_files $uri $uri/ /index.html;
       }
       
       location /api/ {
           proxy_pass http://localhost:8080/api/;
       }
   }
   ```

## Configuration

### Environment Variables

| Variable | Description | Default | Required |
|----------|-------------|---------|----------|
| `DB_HOST` | Database host | localhost | Yes |
| `DB_PORT` | Database port | 5432 | Yes |
| `DB_NAME` | Database name | workflow_platform | Yes |
| `DB_USERNAME` | Database username | workflow_user | Yes |
| `DB_PASSWORD` | Database password | - | Yes |
| `JWT_SECRET` | JWT signing secret | - | Yes |
| `JWT_EXPIRATION` | JWT expiration time (ms) | 86400000 | No |
| `SPRING_PROFILES_ACTIVE` | Spring profile | dev | No |

### Application Configuration

#### Backend Configuration Files
- `application.yml`: Main configuration
- `application-dev.yml`: Development settings
- `application-prod.yml`: Production settings

#### Frontend Configuration
- Environment variables: `REACT_APP_API_URL`
- Build configuration: `package.json`

### Security Configuration

1. **Change default credentials**
2. **Set strong JWT secret**
3. **Configure HTTPS** (production)
4. **Set up database security**
5. **Configure firewall rules**

## Initial Setup

### 1. Create Admin User

After deployment, create an admin user:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@yourcompany.com",
    "password": "your_secure_password",
    "firstName": "Admin",
    "lastName": "User",
    "roles": ["ROLE_ADMIN"]
  }'
```

### 2. Deploy Sample Processes

Upload sample BPMN files from the `examples/` directory:

1. Log in to the web interface
2. Navigate to "Process Management"
3. Upload `examples/simple-approval-process.bpmn`

## Monitoring and Maintenance

### Health Checks

- **Backend Health**: http://localhost:8080/api/actuator/health
- **Database Connection**: Check logs for connection issues
- **Application Logs**: `/app/logs/` (Docker) or configured log directory

### Metrics

Access metrics at: http://localhost:8080/api/actuator/metrics

### Backup

#### Database Backup
```bash
pg_dump -h localhost -U workflow_user workflow_platform > backup.sql
```

#### Application Backup
- Configuration files
- Deployed process definitions
- Custom forms
- User data

## Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Check database credentials
   - Verify database is running
   - Check network connectivity

2. **JWT Token Errors**:
   - Verify JWT secret configuration
   - Check token expiration settings

3. **File Upload Issues**:
   - Check file permissions
   - Verify file size limits
   - Check disk space

4. **CORS Errors**:
   - Configure CORS settings in backend
   - Check API URL configuration in frontend

### Logs

#### Backend Logs
```bash
# Docker
docker logs workflow-platform_backend_1

# Local
tail -f logs/workflow-platform.log
```

#### Frontend Logs
Check browser console for client-side errors

### Performance Tuning

1. **Database**:
   - Configure connection pooling
   - Optimize database indexes
   - Monitor query performance

2. **JVM**:
   - Set appropriate heap size
   - Configure garbage collection
   - Monitor memory usage

3. **Web Server**:
   - Enable gzip compression
   - Configure caching headers
   - Optimize static asset delivery

## Support

For additional support:
1. Check application logs
2. Review configuration settings
3. Verify system requirements
4. Test with sample data

## Security Considerations

1. **Production Deployment**:
   - Use HTTPS
   - Secure database connections
   - Regular security updates
   - Monitor access logs

2. **Access Control**:
   - Implement proper role-based access
   - Regular user access reviews
   - Strong password policies

3. **Data Protection**:
   - Regular backups
   - Data encryption at rest
   - Secure data transmission