import React from 'react'
import { Navigate } from 'react-router-dom'
import useAuthStore from '../stores/authStore'
import { UserRole } from '../types'

interface ProtectedRouteProps {
  children: React.ReactNode
  requiredRoles?: UserRole[]
  requiredPermissions?: string[]
}

export default function ProtectedRoute({ 
  children, 
  requiredRoles = [],
  requiredPermissions = []
}: ProtectedRouteProps) {
  const { isAuthenticated, hasAnyRole, hasPermission } = useAuthStore()

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  // Check role-based access
  if (requiredRoles.length > 0 && !hasAnyRole(requiredRoles)) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="max-w-md w-full space-y-8 text-center">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">Access Denied</h2>
            <p className="mt-2 text-gray-600">
              You don't have permission to access this page.
            </p>
            <p className="mt-2 text-sm text-gray-500">
              Required roles: {requiredRoles.join(', ')}
            </p>
          </div>
          <button
            onClick={() => window.history.back()}
            className="btn-outline"
          >
            Go Back
          </button>
        </div>
      </div>
    )
  }

  // Check permission-based access
  if (requiredPermissions.length > 0) {
    const hasRequiredPermissions = requiredPermissions.every(permission => 
      hasPermission(permission)
    )
    
    if (!hasRequiredPermissions) {
      return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
          <div className="max-w-md w-full space-y-8 text-center">
            <div>
              <h2 className="text-2xl font-bold text-gray-900">Access Denied</h2>
              <p className="mt-2 text-gray-600">
                You don't have the required permissions to access this page.
              </p>
            </div>
            <button
              onClick={() => window.history.back()}
              className="btn-outline"
            >
              Go Back
            </button>
          </div>
        </div>
      )
    }
  }

  return <>{children}</>
}