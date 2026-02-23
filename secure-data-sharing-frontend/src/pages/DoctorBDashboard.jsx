import React, { useEffect, useState } from 'react';
import { getSharedRecords, decryptSharedRecord } from '../services/api';
import { useToast } from '../components/Toast';

const DoctorBDashboard = () => {
    const [sharedRecords, setSharedRecords] = useState([]);
    const [decryptedData, setDecryptedData] = useState({});
    const [loadingId, setLoadingId] = useState(null);
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));
    const toast = useToast();

    useEffect(() => {
        if (currentUser?.id) {
            getSharedRecords(currentUser.id)
                .then(res => setSharedRecords(res.data))
                .catch(console.error);
        }
    }, []);

    const handleDecrypt = async (sharedId) => {
        setLoadingId(sharedId);
        try {
            const res = await decryptSharedRecord(currentUser.id, sharedId);
            setDecryptedData(prev => ({ ...prev, [sharedId]: res.data }));
        } catch (err) {
            const msg = err.response?.data?.message || 'Decryption failed.';
            toast.error(msg);
        } finally {
            setLoadingId(null);
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">Shared with Me ({currentUser?.name})</h2>
                <span className="text-gray-600">{currentUser?.name}</span>
            </div>

            <div className="grid gap-6">
                {sharedRecords.map(record => (
                    <div key={record.id} className="bg-white p-6 rounded shadow border border-purple-100">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="font-semibold text-lg text-purple-900">Shared Record</h3>
                                <p className="text-sm text-gray-500">Shared At: {new Date(record.sharedAt).toLocaleString()}</p>
                                <p className="text-sm text-gray-500">Original Record ID: {record.originalRecord?.id}</p>
                            </div>
                        </div>

                        <div className="bg-gray-50 p-4 rounded mb-4 font-mono text-xs break-all">
                            <strong>Re-Encrypted Data (Server View):</strong>
                            <br />
                            {record.reEncryptedData?.substring(0, 100)}...
                        </div>

                        {decryptedData[record.id] && (
                            <div className="bg-green-50 p-4 rounded mb-4 border border-green-200">
                                <strong className="text-green-800">Decrypted Content (Accessible to Me):</strong>
                                <p className="text-green-900 mt-1">{decryptedData[record.id]}</p>
                            </div>
                        )}

                        <button
                            onClick={() => handleDecrypt(record.id)}
                            disabled={loadingId === record.id}
                            className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 text-sm disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
                        >
                            {loadingId === record.id ? 'Decrypting...' : 'Decrypt Shared Record'}
                        </button>
                    </div>
                ))}
                {sharedRecords.length === 0 && <p className="text-gray-500">No shared records found.</p>}
            </div>
        </div>
    );
};

export default DoctorBDashboard;
