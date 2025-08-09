import React from 'react'
import { ChartBarIcon, UserGroupIcon, ClipboardDocumentListIcon, BeakerIcon } from '@heroicons/react/24/outline'

const stats = [
  { name: 'Total Patients', value: '1,247', icon: UserGroupIcon, change: '+12%', changeType: 'increase' },
  { name: 'Active Treatments', value: '89', icon: ClipboardDocumentListIcon, change: '+8%', changeType: 'increase' },
  { name: 'Lab Tests Pending', value: '23', icon: BeakerIcon, change: '-5%', changeType: 'decrease' },
  { name: 'Monthly Revenue', value: '₹2,45,000', icon: ChartBarIcon, change: '+15%', changeType: 'increase' },
]

export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="mt-1 text-sm text-gray-500">
          Welcome to Heartbeat Clinical Management System
        </p>
      </div>

      {/* Stats */}
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((item) => (
          <div
            key={item.name}
            className="relative overflow-hidden rounded-lg bg-white px-4 pt-5 pb-6 shadow sm:px-6 sm:pt-6"
          >
            <div>
              <div className="absolute rounded-md bg-red-500 p-3">
                <item.icon className="h-6 w-6 text-white" aria-hidden="true" />
              </div>
              <p className="ml-16 truncate text-sm font-medium text-gray-500">{item.name}</p>
              <p className="ml-16 text-2xl font-semibold text-gray-900">{item.value}</p>
            </div>
            <div className="ml-16 mt-4">
              <div className="flex items-center">
                <span
                  className={`text-sm font-medium ${
                    item.changeType === 'increase' ? 'text-green-600' : 'text-red-600'
                  }`}
                >
                  {item.change}
                </span>
                <span className="ml-2 text-sm text-gray-500">from last month</span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Recent Patients</h3>
          </div>
          <div className="card-body">
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((item) => (
                <div key={item} className="flex items-center space-x-4">
                  <div className="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                    <span className="text-sm font-medium text-gray-600">P{item}</span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900">Patient {item}</p>
                    <p className="text-sm text-gray-500">Registered today</p>
                  </div>
                  <div className="text-sm text-gray-500">2 hours ago</div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3 className="text-lg font-medium text-gray-900">Upcoming Appointments</h3>
          </div>
          <div className="card-body">
            <div className="space-y-4">
              {[1, 2, 3, 4, 5].map((item) => (
                <div key={item} className="flex items-center space-x-4">
                  <div className="h-10 w-10 rounded-full bg-blue-100 flex items-center justify-center">
                    <span className="text-sm font-medium text-blue-600">A{item}</span>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium text-gray-900">Appointment {item}</p>
                    <p className="text-sm text-gray-500">Dr. Sharma</p>
                  </div>
                  <div className="text-sm text-gray-500">Tomorrow 10:00 AM</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="card">
        <div className="card-header">
          <h3 className="text-lg font-medium text-gray-900">Quick Actions</h3>
        </div>
        <div className="card-body">
          <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
            <button className="btn-primary">
              New Patient
            </button>
            <button className="btn-outline">
              New Treatment
            </button>
            <button className="btn-outline">
              Lab Results
            </button>
            <button className="btn-outline">
              Send Notification
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}