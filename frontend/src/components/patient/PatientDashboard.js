import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { appointmentsAPI } from '../../services/api';
import { useAuth } from '../../context/AuthContext';
import LoadingSpinner from '../common/LoadingSpinner';

const PatientDashboard = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { user } = useAuth();

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      const response = await appointmentsAPI.getAll();
      setAppointments(response.data);
    } catch (error) {
      setError('Failed to fetch appointments');
      console.error('Error fetching appointments:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelAppointment = async (appointmentId) => {
    if (window.confirm('Are you sure you want to cancel this appointment?')) {
      try {
        await appointmentsAPI.cancel(appointmentId);
        fetchAppointments(); // Refresh the list
      } catch (error) {
        setError('Failed to cancel appointment');
        console.error('Error cancelling appointment:', error);
      }
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'SCHEDULED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'COMPLETED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const formatDateTime = (dateTimeString) => {
    const date = new Date(dateTimeString);
    return date.toLocaleString();
  };

  if (loading) {
    return <LoadingSpinner text="Loading your appointments..." />;
  }

  const upcomingAppointments = appointments.filter(apt => apt.status === 'SCHEDULED');
  const pastAppointments = appointments.filter(apt => apt.status !== 'SCHEDULED');

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">
          Welcome, {user.firstName}!
        </h1>
        <p className="text-gray-600 mt-2">
          Manage your appointments and health records
        </p>
      </div>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-6">
          {error}
        </div>
      )}

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="card">
          <h3 className="text-lg font-semibold mb-2">Book New Appointment</h3>
          <p className="text-gray-600 mb-4">Schedule a visit with your preferred doctor</p>
          <Link to="/book-appointment" className="btn-primary">
            Book Appointment
          </Link>
        </div>
        
        <div className="card">
          <h3 className="text-lg font-semibold mb-2">Upcoming Appointments</h3>
          <p className="text-3xl font-bold text-primary-600">{upcomingAppointments.length}</p>
          <p className="text-gray-600">Scheduled visits</p>
        </div>
        
        <div className="card">
          <h3 className="text-lg font-semibold mb-2">Total Appointments</h3>
          <p className="text-3xl font-bold text-gray-700">{appointments.length}</p>
          <p className="text-gray-600">All time</p>
        </div>
      </div>

      {/* Upcoming Appointments */}
      <div className="card mb-8">
        <h2 className="text-xl font-bold mb-4">Upcoming Appointments</h2>
        {upcomingAppointments.length === 0 ? (
          <p className="text-gray-500">No upcoming appointments</p>
        ) : (
          <div className="space-y-4">
            {upcomingAppointments.map((appointment) => (
              <div key={appointment.id} className="border rounded-lg p-4 hover:bg-gray-50">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold text-lg">{appointment.doctorName}</h3>
                    <p className="text-gray-600">{appointment.doctorSpecialty}</p>
                    <p className="text-sm text-gray-500 mt-1">
                      {formatDateTime(appointment.startTime)}
                    </p>
                    {appointment.notes && (
                      <p className="text-sm text-gray-700 mt-2">
                        <strong>Notes:</strong> {appointment.notes}
                      </p>
                    )}
                  </div>
                  <div className="text-right">
                    <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(appointment.status)}`}>
                      {appointment.status}
                    </span>
                    <div className="mt-2">
                      <button
                        onClick={() => handleCancelAppointment(appointment.id)}
                        className="btn-danger text-sm"
                      >
                        Cancel
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Past Appointments */}
      <div className="card">
        <h2 className="text-xl font-bold mb-4">Past Appointments</h2>
        {pastAppointments.length === 0 ? (
          <p className="text-gray-500">No past appointments</p>
        ) : (
          <div className="space-y-4">
            {pastAppointments.map((appointment) => (
              <div key={appointment.id} className="border rounded-lg p-4">
                <div className="flex justify-between items-start">
                  <div>
                    <h3 className="font-semibold text-lg">{appointment.doctorName}</h3>
                    <p className="text-gray-600">{appointment.doctorSpecialty}</p>
                    <p className="text-sm text-gray-500 mt-1">
                      {formatDateTime(appointment.startTime)}
                    </p>
                    {appointment.notes && (
                      <p className="text-sm text-gray-700 mt-2">
                        <strong>Notes:</strong> {appointment.notes}
                      </p>
                    )}
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(appointment.status)}`}>
                    {appointment.status}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default PatientDashboard;