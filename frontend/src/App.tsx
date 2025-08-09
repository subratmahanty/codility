import React from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import useAuthStore from './stores/authStore'
import Layout from './components/Layout'
import LoginPage from './pages/LoginPage'
import DashboardPage from './pages/DashboardPage'
import PatientsPage from './pages/PatientsPage'
import TreatmentsPage from './pages/TreatmentsPage'
import MedicinesPage from './pages/MedicinesPage'
import LabTestsPage from './pages/LabTestsPage'
import NotificationsPage from './pages/NotificationsPage'
import ProtectedRoute from './components/ProtectedRoute'

function App() {
  const { isAuthenticated, checkToken } = useAuthStore()

  // Check token validity on app start
  React.useEffect(() => {
    checkToken()
  }, [checkToken])

  if (!isAuthenticated) {
    return <LoginPage />
  }

  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        
        <Route 
          path="/dashboard" 
          element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/patients/*" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'HELPDESK', 'NURSE', 'DOCTOR']}>
              <PatientsPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/treatments/*" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'DOCTOR', 'NURSE']}>
              <TreatmentsPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/medicines/*" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'PHARMA_ADMIN']}>
              <MedicinesPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/lab-tests/*" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'PHARMA_ADMIN', 'LAB_ADMIN']}>
              <LabTestsPage />
            </ProtectedRoute>
          } 
        />
        
        <Route 
          path="/notifications/*" 
          element={
            <ProtectedRoute requiredRoles={['ADMIN', 'DOCTOR', 'NURSE']}>
              <NotificationsPage />
            </ProtectedRoute>
          } 
        />
        
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Layout>
  )
}

export default App