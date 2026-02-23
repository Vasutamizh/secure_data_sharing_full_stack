import React, { useEffect, useState } from 'react';
import { getUsers, createRecord } from '../services/api';
import { useToast } from '../components/Toast';

const DoctorADashboard = () => {
    const [patients, setPatients] = useState([]);
    const [selectedPatient, setSelectedPatient] = useState('');
    const [diagnosis, setDiagnosis] = useState('');
    const [loading, setLoading] = useState(false);
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const toast = useToast();

    useEffect(() => {
        getUsers().then(res => {
            setPatients(res.data.filter(u => u.role === 'PATIENT'));
        }).catch(console.error);
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedPatient || !diagnosis) return;

        setLoading(true);
        try {
            await createRecord({
                doctorId: currentUser.id,
                patientId: selectedPatient,
                diagnosis: diagnosis
            });
            toast.success('Record created and encrypted successfully!');
            setDiagnosis('');
        } catch (err) {
            const msg = err.response?.data?.message || 'Failed to create record.';
            toast.error(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto p-6 bg-white rounded shadow">
            <h2 className="text-2xl font-bold mb-4">{currentUser?.name} Dashboard</h2>
            <p className="mb-4 text-gray-600">Welcome, {currentUser?.name}</p>

            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Select Patient</label>
                    <select
                        className="mt-1 block w-full p-2 border rounded"
                        value={selectedPatient}
                        onChange={(e) => setSelectedPatient(e.target.value)}
                        required
                    >
                        <option value="">-- Select --</option>
                        {patients.map(p => (
                            <option key={p.id} value={p.id}>{p.name}</option>
                        ))}
                    </select>
                </div>

                <div>
                    <label className="block text-sm font-medium text-gray-700">Diagnosis</label>
                    <textarea
                        className="mt-1 block w-full p-2 border rounded"
                        rows="4"
                        value={diagnosis}
                        onChange={(e) => setDiagnosis(e.target.value)}
                        placeholder="Enter medical diagnosis..."
                        required
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-green-600 text-white py-2 rounded hover:bg-green-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                >
                    {loading ? 'Encrypting...' : 'Encrypt & Create Record'}
                </button>
            </form>
        </div>
    );
};

export default DoctorADashboard;
