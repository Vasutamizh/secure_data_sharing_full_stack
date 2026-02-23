import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createUser } from '../services/api';
import { useToast } from '../components/Toast';

const Register = () => {
    const [name, setName] = useState('');
    const [role, setRole] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const toast = useToast();

    const roles = [
        { value: 'PATIENT', label: 'Patient', desc: 'Can view encrypted records and share with specialists' },
        { value: 'DOCTOR_A', label: 'Primary Doctor', desc: 'Can create and encrypt medical records' },
        { value: 'DOCTOR_B', label: 'Specialist Doctor', desc: 'Can receive and decrypt shared records' },
    ];

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!name.trim() || !role) {
            toast.warning('Please fill in all fields.');
            return;
        }

        setLoading(true);
        try {
            await createUser({ name: name.trim(), role });
            toast.success(`User "${name.trim()}" registered successfully! ECC keys generated.`);
            setName('');
            setRole('');
            setTimeout(() => navigate('/'), 1500);
        } catch (err) {
            const msg = err.response?.data?.message || 'Registration failed. Please try again.';
            toast.error(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[60vh]">
            <div className="w-full max-w-lg bg-white rounded-lg shadow-md p-8">
                <h2 className="text-2xl font-bold mb-2 text-center">Register New User</h2>
                <p className="text-gray-500 text-sm text-center mb-6">
                    An ECC key pair will be automatically generated for the new user.
                </p>

                <form onSubmit={handleSubmit} className="space-y-5">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                        <input
                            type="text"
                            className="block w-full p-2.5 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            placeholder="e.g. Dr. Sarah Wilson"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-2">Role</label>
                        <div className="grid gap-3">
                            {roles.map((r) => (
                                <label
                                    key={r.value}
                                    className={`flex items-start gap-3 p-3 border rounded-lg cursor-pointer transition-colors ${role === r.value
                                            ? 'border-blue-500 bg-blue-50'
                                            : 'border-gray-200 hover:border-gray-300'
                                        }`}
                                >
                                    <input
                                        type="radio"
                                        name="role"
                                        value={r.value}
                                        checked={role === r.value}
                                        onChange={(e) => setRole(e.target.value)}
                                        className="mt-0.5"
                                    />
                                    <div>
                                        <span className="font-medium text-sm">{r.label}</span>
                                        <p className="text-xs text-gray-500 mt-0.5">{r.desc}</p>
                                    </div>
                                </label>
                            ))}
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-2.5 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors font-medium"
                    >
                        {loading ? 'Generating Keys & Registering...' : 'Register User'}
                    </button>
                </form>

                <p className="text-center mt-5 text-sm text-gray-500">
                    Already registered?{' '}
                    <a href="/" className="text-blue-600 hover:underline">Go to Login</a>
                </p>
            </div>
        </div>
    );
};

export default Register;
