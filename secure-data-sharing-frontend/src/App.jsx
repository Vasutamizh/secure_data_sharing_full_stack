import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import DoctorADashboard from './pages/DoctorADashboard';
import PatientDashboard from './pages/PatientDashboard';
import DoctorBDashboard from './pages/DoctorBDashboard';

function App() {
    return (
        <Router>
            <div className="min-h-screen">
                <nav className="bg-white shadow-sm p-4 mb-6">
                    <div className="container mx-auto flex justify-between items-center">
                        <h1 className="text-xl font-bold text-blue-600">Secure Medical Data Share (PRE)</h1>
                        <a href="/" className="text-gray-600 hover:text-blue-500">Logout / Home</a>
                    </div>
                </nav>
                <div className="container mx-auto px-4">
                    <Routes>
                        <Route path="/" element={<Login />} />
                        <Route path="/doctor-a" element={<DoctorADashboard />} />
                        <Route path="/patient" element={<PatientDashboard />} />
                        <Route path="/doctor-b" element={<DoctorBDashboard />} />
                        <Route path="*" element={<Navigate to="/" />} />
                    </Routes>
                </div>
            </div>
        </Router>
    );
}

export default App;
