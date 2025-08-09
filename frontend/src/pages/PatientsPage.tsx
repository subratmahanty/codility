import React from 'react'
import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline'

export default function PatientsPage() {
  return (
    <div className="space-y-6">
      <div className="sm:flex sm:items-center sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Patients</h1>
          <p className="mt-1 text-sm text-gray-500">
            Manage patient records and registrations
          </p>
        </div>
        <div className="mt-4 sm:mt-0">
          <button className="btn-primary">
            <PlusIcon className="h-5 w-5 mr-2" />
            New Patient
          </button>
        </div>
      </div>

      {/* Search and filters */}
      <div className="card">
        <div className="card-body">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-1">
              <div className="relative">
                <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search patients..."
                  className="pl-10 input-field"
                />
              </div>
            </div>
            <div className="flex gap-2">
              <select className="input-field">
                <option value="">All Status</option>
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
              </select>
              <button className="btn-outline">Filter</button>
            </div>
          </div>
        </div>
      </div>

      {/* Patients table */}
      <div className="card">
        <div className="card-body p-0">
          <table className="table">
            <thead className="table-header">
              <tr>
                <th className="table-header-cell">Patient</th>
                <th className="table-header-cell">Contact</th>
                <th className="table-header-cell">Age</th>
                <th className="table-header-cell">Blood Group</th>
                <th className="table-header-cell">Status</th>
                <th className="table-header-cell">Actions</th>
              </tr>
            </thead>
            <tbody className="table-body">
              {[1, 2, 3, 4, 5].map((item) => (
                <tr key={item} className="table-row">
                  <td className="table-cell">
                    <div className="flex items-center">
                      <div className="h-10 w-10 rounded-full bg-gray-200 flex items-center justify-center">
                        <span className="text-sm font-medium text-gray-600">P{item}</span>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-gray-900">Patient {item}</div>
                        <div className="text-sm text-gray-500">ID: PAT00{item}</div>
                      </div>
                    </div>
                  </td>
                  <td className="table-cell">
                    <div className="text-sm text-gray-900">+91-98765432{item}</div>
                    <div className="text-sm text-gray-500">patient{item}@email.com</div>
                  </td>
                  <td className="table-cell">
                    <div className="text-sm text-gray-900">{30 + item} years</div>
                  </td>
                  <td className="table-cell">
                    <div className="text-sm text-gray-900">B+</div>
                  </td>
                  <td className="table-cell">
                    <span className="status-badge status-badge-active">Active</span>
                  </td>
                  <td className="table-cell">
                    <div className="flex space-x-2">
                      <button className="text-indigo-600 hover:text-indigo-900 text-sm">
                        View
                      </button>
                      <button className="text-green-600 hover:text-green-900 text-sm">
                        Edit
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Pagination */}
      <div className="flex items-center justify-between">
        <div className="text-sm text-gray-500">
          Showing 1 to 5 of 100 results
        </div>
        <div className="flex space-x-2">
          <button className="btn-outline">Previous</button>
          <button className="btn-outline">Next</button>
        </div>
      </div>
    </div>
  )
}