# Doctor's Appointment Management System

A comprehensive appointment management system built with Spring Boot, MongoDB, and React that enables patients to book appointments, doctors to manage their schedules, and automated SMS/WhatsApp notifications via Twilio.

## 🚀 Features

### Core Functionality
- **User Authentication**: JWT-based authentication with role-based access control (Patient, Doctor, Admin)
- **Appointment Booking**: Patients can book appointments with available doctors
- **Appointment Management**: Cancel appointments, update status, view history
- **Real-time Notifications**: SMS and WhatsApp notifications via Twilio API
- **Automated Reminders**: Scheduled reminders sent 1 hour before appointments
- **Comprehensive History**: Immutable audit trail of all appointment actions

### User Roles
- **Patients**: Book appointments, view history, receive notifications
- **Doctors**: Manage schedule, view patient appointments, update appointment status
- **Admins**: Full system access and management capabilities

### Notification Channels
- **SMS**: Appointment confirmations, reminders, cancellations
- **WhatsApp**: Via Twilio WhatsApp Business API
- **In-App**: Real-time dashboard updates

## 🏗️ Architecture

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2.0 with Java 17
- **Database**: MongoDB for flexible document storage
- **Security**: Spring Security with JWT tokens
- **Scheduling**: Spring Scheduler for automated tasks
- **Notifications**: Twilio SDK for SMS/WhatsApp integration
- **API**: RESTful APIs with comprehensive error handling

### Frontend (React)
- **Framework**: React 18 with functional components and hooks
- **Styling**: Tailwind CSS for modern, responsive design
- **Routing**: React Router for SPA navigation
- **State Management**: Context API for authentication state
- **HTTP Client**: Axios with interceptors for API communication

### Database Design (MongoDB)
- **users**: Stores both patients and doctors with role-based fields
- **appointments**: Appointment records with status tracking
- **notifications**: Complete notification history and status
- **appointment_history**: Immutable audit log of all changes

## 📋 Prerequisites

- **Java 17+**
- **Node.js 16+**
- **MongoDB 4.4+**
- **Twilio Account** (for SMS/WhatsApp notifications)

## 🛠️ Installation & Setup

### 1. Clone the Repository
```bash
git clone <repository-url>
cd doctor-appointment-system
```

### 2. Backend Setup

#### Navigate to backend directory
```bash
cd backend
```

#### Configure MongoDB
Create a MongoDB database named `doctor_appointment` or update the connection string in `application.yml`.

#### Configure Twilio (Optional)
Update `application.yml` with your Twilio credentials:
```yaml
twilio:
  account-sid: YOUR_TWILIO_ACCOUNT_SID
  auth-token: YOUR_TWILIO_AUTH_TOKEN
  phone-number: YOUR_TWILIO_PHONE_NUMBER
```

#### Set JWT Secret
Update the JWT secret in `application.yml` or set as environment variable:
```yaml
spring:
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-here}
```

#### Install dependencies and run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

#### Navigate to frontend directory
```bash
cd frontend
```

#### Install dependencies
```bash
npm install
```

#### Start the development server
```bash
npm start
```

The frontend will start on `http://localhost:3000`

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the backend directory:
```env
JWT_SECRET=your-jwt-secret-key
TWILIO_ACCOUNT_SID=your-twilio-account-sid
TWILIO_AUTH_TOKEN=your-twilio-auth-token
TWILIO_PHONE_NUMBER=your-twilio-phone-number
```

### MongoDB Configuration
Default configuration connects to `mongodb://localhost:27017/doctor_appointment`. Update in `application.yml` if different:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://your-mongodb-uri
      database: doctor_appointment
```

## 📱 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/validate` - Token validation

### Appointments
- `POST /api/appointments` - Book appointment
- `GET /api/appointments` - Get user appointments
- `GET /api/appointments/{id}` - Get specific appointment
- `PUT /api/appointments/{id}/cancel` - Cancel appointment
- `PUT /api/appointments/{id}/status` - Update appointment status

### Doctors
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{id}` - Get doctor details
- `GET /api/doctors/specialty/{specialty}` - Get doctors by specialty

### Notifications
- `POST /api/notifications/sms/callback` - Twilio webhook
- `GET /api/notifications/user/{userId}` - Get user notifications

## 🔒 Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Authorization**: Granular access control
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Configurable cross-origin resource sharing
- **Input Validation**: Comprehensive request validation
- **SQL Injection Prevention**: MongoDB ODM protection

## 📊 Scheduled Tasks

### Appointment Reminders
- **Frequency**: Every 15 minutes
- **Function**: Sends SMS/WhatsApp reminders 1 hour before appointments
- **Configuration**: `app.scheduler.reminder-cron`

### Status Updates
- **Frequency**: Every hour
- **Function**: Automatically marks past appointments as COMPLETED
- **Configuration**: `app.scheduler.status-update-cron`

## 🧪 Testing

### Backend Testing
```bash
cd backend
./mvnw test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## 🚀 Deployment

### Backend Deployment
1. Build the JAR file:
```bash
./mvnw clean package
```

2. Deploy the JAR file to your server:
```bash
java -jar target/doctor-appointment-backend-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
1. Build the production bundle:
```bash
npm run build
```

2. Deploy the `build/` directory to your web server (Nginx, Apache, or CDN)

### Docker Deployment (Optional)
Create Dockerfiles for both frontend and backend for containerized deployment.

## 🔧 Monitoring & Maintenance

### Logs
- Backend logs are available in the console and can be configured in `application.yml`
- Frontend errors are logged to the browser console

### Health Checks
- Backend: `GET /actuator/health` (if Spring Actuator is enabled)
- Frontend: Application should load successfully

### Database Maintenance
- Regular MongoDB backups recommended
- Monitor database performance and indexing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in the GitHub repository
- Check the documentation for common solutions
- Review the API endpoints and error messages

## 🛣️ Roadmap

### Upcoming Features
- [ ] Email notifications
- [ ] Video consultation integration
- [ ] Patient medical records
- [ ] Prescription management
- [ ] Multi-language support
- [ ] Mobile app (React Native)
- [ ] Advanced reporting and analytics
- [ ] Integration with external calendar systems

## 📚 Technical Details

### Design Patterns Used
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic separation
- **DTO Pattern**: Data transfer objects for API communication
- **Observer Pattern**: Event-driven notifications
- **Strategy Pattern**: Multiple notification channels

### Performance Optimizations
- **Database Indexing**: Optimized queries with proper indexes
- **Async Processing**: Non-blocking notification sending
- **Caching**: JWT token validation caching
- **Lazy Loading**: Efficient data loading in React components

### Security Best Practices
- **Input Validation**: Server-side validation for all inputs
- **Rate Limiting**: Protection against API abuse
- **Error Handling**: Secure error messages without sensitive data exposure
- **HTTPS Enforcement**: Secure communication in production