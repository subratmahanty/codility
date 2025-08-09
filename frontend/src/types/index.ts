// Core types for Heartbeat Clinical Management System

export interface User {
  id: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  phone?: string;
  isActive: boolean;
  createdAt: string;
  lastLogin?: string;
}

export type UserRole = 
  | 'ADMIN' 
  | 'DOCTOR' 
  | 'PHARMA_ADMIN' 
  | 'LAB_ADMIN' 
  | 'NURSE' 
  | 'HELPDESK' 
  | 'SUPERVISOR';

export interface AuthResponse {
  token: string;
  user: User;
  expiresAt: string;
}

export interface Patient {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  pincode?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  bloodGroup?: string;
  allergies?: string;
  medicalHistory?: string;
  photoUrl?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  createdBy?: UserSummary;
  identifiers: PatientIdentifier[];
  age?: number;
}

export interface PatientIdentifier {
  id: string;
  type: 'PAN' | 'AADHAR' | 'VOTER_ID';
  value: string;
  isVerified: boolean;
  verifiedAt?: string;
  verifiedBy?: UserSummary;
  createdAt: string;
}

export interface UserSummary {
  id: string;
  firstName: string;
  lastName: string;
  role: string;
}

export interface Treatment {
  id: string;
  patient: PatientSummary;
  doctor: UserSummary;
  diagnosis: string;
  symptoms?: string;
  examinationNotes?: string;
  status: 'ACTIVE' | 'COMPLETED' | 'CANCELLED';
  followUpDate?: string;
  treatmentDate: string;
  createdAt: string;
  updatedAt: string;
  prescriptionItems: PrescriptionItem[];
  labTestRequests: LabTestRequest[];
}

export interface PatientSummary {
  id: string;
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  phone?: string;
}

export interface PrescriptionItem {
  id: string;
  medicine: Medicine;
  dosage: string;
  frequency: string;
  duration: string;
  instructions?: string;
  quantity?: number;
}

export interface Medicine {
  id: string;
  name: string;
  genericName?: string;
  manufacturer?: string;
  dosageForm: string;
  strength: string;
  unit: string;
  description?: string;
  isActive: boolean;
  createdAt: string;
}

export interface LabTest {
  id: string;
  testName: string;
  testCode: string;
  category: string;
  normalRangeMin?: number;
  normalRangeMax?: number;
  unit?: string;
  sampleType: string;
  preparationNotes?: string;
  cost: number;
  isActive: boolean;
  createdAt: string;
}

export interface LabTestRequest {
  id: string;
  labTest: LabTest;
  requestedDate: string;
  notes?: string;
  isUrgent: boolean;
  result?: LabTestResult;
}

export interface LabTestResult {
  id: string;
  resultValue?: string;
  resultText?: string;
  isNormal?: boolean;
  resultDate: string;
  performedBy?: UserSummary;
  reviewedBy?: UserSummary;
  notes?: string;
  createdAt: string;
}

export interface PatientVitals {
  id: string;
  heightCm?: number;
  weightKg?: number;
  temperatureF?: number;
  bloodPressureSystolic?: number;
  bloodPressureDiastolic?: number;
  heartRateBpm?: number;
  respiratoryRate?: number;
  oxygenSaturation?: number;
  bloodSugar?: number;
  notes?: string;
  recordedDate: string;
  recordedBy: UserSummary;
  createdAt: string;
}

export interface FileAttachment {
  id: string;
  filename: string;
  originalFilename: string;
  fileType: 'PHOTO' | 'REPORT' | 'PRESCRIPTION' | 'LAB_RESULT' | 'OTHER';
  fileSize: number;
  mimeType: string;
  publicUrl: string;
  description?: string;
  uploadedBy: UserSummary;
  createdAt: string;
}

export interface Notification {
  id: string;
  patient?: PatientSummary;
  treatment?: { id: string };
  recipient: string;
  channel: 'SMS' | 'EMAIL' | 'WHATSAPP';
  subject?: string;
  message: string;
  scheduledAt: string;
  sentAt?: string;
  status: 'SCHEDULED' | 'SENT' | 'FAILED' | 'CANCELLED';
  retryCount: number;
  errorMessage?: string;
  createdAt: string;
}

export interface NotificationTemplate {
  id: string;
  name: string;
  subject?: string;
  messageTemplate: string;
  channel: 'SMS' | 'EMAIL' | 'WHATSAPP';
  isActive: boolean;
  createdAt: string;
}

export interface PracticeTemplate {
  id: string;
  name: string;
  diagnosis: string;
  symptoms?: string;
  recommendedTests: string[];
  recommendedMedicines: string[];
  notes?: string;
  isActive: boolean;
  createdAt: string;
  createdBy: UserSummary;
}

export interface DashboardSummary {
  dateRange: {
    from: string;
    to: string;
  };
  metrics: {
    newPatients: number;
    totalVisits: number;
    prescriptionsIssued: number;
    labTestsRequested: number;
    labTestsCompleted: number;
    pendingFollowUps: number;
  };
  topDiagnoses: Array<{
    diagnosis: string;
    count: number;
  }>;
  monthlyTrends: Array<{
    month: string;
    patients: number;
    visits: number;
    prescriptions: number;
  }>;
}

// API Response types
export interface ApiResponse<T> {
  data?: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: Array<{
    field: string;
    message: string;
  }>;
}

// Form types
export interface LoginForm {
  username: string;
  password: string;
}

export interface PatientForm {
  firstName: string;
  lastName: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phone?: string;
  email?: string;
  address?: string;
  city?: string;
  state?: string;
  pincode?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  bloodGroup?: string;
  allergies?: string;
  medicalHistory?: string;
  identifiers: Array<{
    type: 'PAN' | 'AADHAR' | 'VOTER_ID';
    value: string;
  }>;
}

export interface TreatmentForm {
  patientId: string;
  diagnosis: string;
  symptoms?: string;
  examinationNotes?: string;
  followUpDate?: string;
  prescriptionItems: Array<{
    medicineId: string;
    dosage: string;
    frequency: string;
    duration: string;
    instructions?: string;
    quantity?: number;
  }>;
  labTestRequests: Array<{
    labTestId: string;
    notes?: string;
    isUrgent: boolean;
  }>;
}

export interface VitalsForm {
  heightCm?: number;
  weightKg?: number;
  temperatureF?: number;
  bloodPressureSystolic?: number;
  bloodPressureDiastolic?: number;
  heartRateBpm?: number;
  respiratoryRate?: number;
  oxygenSaturation?: number;
  bloodSugar?: number;
  notes?: string;
  recordedDate: string;
}

// Route protection types
export interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredRoles?: UserRole[];
  requiredPermissions?: string[];
}

// Navigation types
export interface NavigationItem {
  name: string;
  href: string;
  icon: React.ComponentType<any>;
  current: boolean;
  requiredRoles?: UserRole[];
  children?: NavigationItem[];
}

// Search and filter types
export interface SearchFilters {
  query?: string;
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
  [key: string]: any;
}