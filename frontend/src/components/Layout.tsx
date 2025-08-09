import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import {
  Bars3Icon,
  XMarkIcon,
  HeartIcon,
  UserGroupIcon,
  ClipboardDocumentListIcon,
  BeakerIcon,
  BellIcon,
  ChartBarIcon,
  Cog6ToothIcon,
  ArrowRightOnRectangleIcon,
} from '@heroicons/react/24/outline'
import useAuthStore from '../stores/authStore'

const navigation = [
  { name: 'Dashboard', href: '/dashboard', icon: ChartBarIcon, roles: [] },
  { name: 'Patients', href: '/patients', icon: UserGroupIcon, roles: ['ADMIN', 'HELPDESK', 'NURSE', 'DOCTOR'] },
  { name: 'Treatments', href: '/treatments', icon: ClipboardDocumentListIcon, roles: ['ADMIN', 'DOCTOR', 'NURSE'] },
  { name: 'Medicines', href: '/medicines', icon: BeakerIcon, roles: ['ADMIN', 'PHARMA_ADMIN'] },
  { name: 'Lab Tests', href: '/lab-tests', icon: BeakerIcon, roles: ['ADMIN', 'PHARMA_ADMIN', 'LAB_ADMIN'] },
  { name: 'Notifications', href: '/notifications', icon: BellIcon, roles: ['ADMIN', 'DOCTOR', 'NURSE'] },
]

interface LayoutProps {
  children: React.ReactNode
}

export default function Layout({ children }: LayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false)
  const location = useLocation()
  const { user, logout, hasAnyRole } = useAuthStore()

  const handleLogout = () => {
    logout()
  }

  const visibleNavigation = navigation.filter(item => 
    item.roles.length === 0 || hasAnyRole(item.roles as any[])
  )

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Mobile sidebar */}
      <div className={`fixed inset-0 z-50 lg:hidden ${sidebarOpen ? 'block' : 'hidden'}`}>
        <div className="fixed inset-0 bg-gray-600 bg-opacity-75" onClick={() => setSidebarOpen(false)} />
        <div className="fixed inset-y-0 left-0 flex w-64 flex-col bg-white">
          <div className="flex h-16 items-center justify-between px-4">
            <div className="flex items-center">
              <HeartIcon className="h-8 w-8 text-red-500" />
              <span className="ml-2 text-lg font-semibold text-gray-900">Heartbeat</span>
            </div>
            <button onClick={() => setSidebarOpen(false)}>
              <XMarkIcon className="h-6 w-6 text-gray-400" />
            </button>
          </div>
          <nav className="flex-1 space-y-1 px-2 py-4">
            {visibleNavigation.map((item) => {
              const isActive = location.pathname.startsWith(item.href)
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`group flex items-center rounded-md px-2 py-2 text-sm font-medium ${
                    isActive
                      ? 'bg-red-100 text-red-900'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                  onClick={() => setSidebarOpen(false)}
                >
                  <item.icon className="mr-3 h-6 w-6 flex-shrink-0" />
                  {item.name}
                </Link>
              )
            })}
          </nav>
        </div>
      </div>

      {/* Desktop sidebar */}
      <div className="hidden lg:fixed lg:inset-y-0 lg:flex lg:w-64 lg:flex-col">
        <div className="flex flex-col flex-grow bg-white border-r border-gray-200">
          <div className="flex h-16 items-center px-4">
            <HeartIcon className="h-8 w-8 text-red-500" />
            <span className="ml-2 text-lg font-semibold text-gray-900">Heartbeat</span>
          </div>
          <nav className="flex-1 space-y-1 px-2 py-4">
            {visibleNavigation.map((item) => {
              const isActive = location.pathname.startsWith(item.href)
              return (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`group flex items-center rounded-md px-2 py-2 text-sm font-medium ${
                    isActive
                      ? 'bg-red-100 text-red-900'
                      : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }`}
                >
                  <item.icon className="mr-3 h-6 w-6 flex-shrink-0" />
                  {item.name}
                </Link>
              )
            })}
          </nav>
          <div className="border-t border-gray-200 p-4">
            <div className="flex items-center">
              <div className="flex-shrink-0">
                <div className="h-8 w-8 rounded-full bg-red-500 flex items-center justify-center">
                  <span className="text-sm font-medium text-white">
                    {user?.firstName?.[0]}{user?.lastName?.[0]}
                  </span>
                </div>
              </div>
              <div className="ml-3 flex-1">
                <p className="text-sm font-medium text-gray-900">{user?.firstName} {user?.lastName}</p>
                <p className="text-xs text-gray-500">{user?.role}</p>
              </div>
              <button
                onClick={handleLogout}
                className="flex-shrink-0 p-1 text-gray-400 hover:text-gray-500"
                title="Logout"
              >
                <ArrowRightOnRectangleIcon className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="lg:pl-64">
        {/* Top bar */}
        <div className="sticky top-0 z-40 bg-white shadow-sm border-b border-gray-200">
          <div className="flex h-16 items-center gap-x-4 px-4 sm:gap-x-6 sm:px-6 lg:px-8">
            <button
              type="button"
              className="-m-2.5 p-2.5 text-gray-700 lg:hidden"
              onClick={() => setSidebarOpen(true)}
            >
              <Bars3Icon className="h-6 w-6" />
            </button>

            <div className="flex flex-1 gap-x-4 self-stretch lg:gap-x-6">
              <div className="flex flex-1"></div>
              <div className="flex items-center gap-x-4 lg:gap-x-6">
                <div className="hidden lg:block lg:h-6 lg:w-px lg:bg-gray-200" />
                <div className="flex items-center gap-x-3">
                  <span className="text-sm text-gray-900">{user?.firstName} {user?.lastName}</span>
                  <span className="inline-flex items-center rounded-md bg-gray-100 px-2 py-1 text-xs font-medium text-gray-600">
                    {user?.role}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Page content */}
        <main className="py-6">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            {children}
          </div>
        </main>
      </div>
    </div>
  )
}