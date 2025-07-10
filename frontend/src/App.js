import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import Login from './components/auth/Login';
import Register from './components/auth/Register';
import PatientDashboard from './components/patient/PatientDashboard';
import DoctorDashboard from './components/doctor/DoctorDashboard';
import AppointmentForm from './components/appointments/AppointmentForm';
import LoadingSpinner from './components/common/LoadingSpinner';
import './index.css';

function AppContent() {
  const { user, loading, isAuthenticated } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <Router>
      <div className="min-h-screen bg-gray-50">
        {isAuthenticated && <Navbar />}
        
        <main className={isAuthenticated ? "pt-16" : ""}>
          <Routes>
            <Route 
              path="/login" 
              element={
                isAuthenticated ? (
                  <Navigate to={getDashboardRoute(user.role)} replace />
                ) : (
                  <Login />
                )
              } 
            />
            <Route 
              path="/register" 
              element={
                isAuthenticated ? (
                  <Navigate to={getDashboardRoute(user.role)} replace />
                ) : (
                  <Register />
                )
              } 
            />
            
            {/* Protected Routes */}
            <Route 
              path="/patient/*" 
              element={
                isAuthenticated && user.role === 'PATIENT' ? (
                  <PatientDashboard />
                ) : (
                  <Navigate to="/login" replace />
                )
              } 
            />
            <Route 
              path="/doctor/*" 
              element={
                isAuthenticated && user.role === 'DOCTOR' ? (
                  <DoctorDashboard />
                ) : (
                  <Navigate to="/login" replace />
                )
              } 
            />
            <Route 
              path="/book-appointment" 
              element={
                isAuthenticated && user.role === 'PATIENT' ? (
                  <AppointmentForm />
                ) : (
                  <Navigate to="/login" replace />
                )
              } 
            />
            
            {/* Default Route */}
            <Route 
              path="/" 
              element={
                isAuthenticated ? (
                  <Navigate to={getDashboardRoute(user.role)} replace />
                ) : (
                  <Navigate to="/login" replace />
                )
              } 
            />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

function getDashboardRoute(role) {
  switch (role) {
    case 'PATIENT':
      return '/patient';
    case 'DOCTOR':
      return '/doctor';
    case 'ADMIN':
      return '/admin';
    default:
      return '/login';
  }
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
}

export default App;