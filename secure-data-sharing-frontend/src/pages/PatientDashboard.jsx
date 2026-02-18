import React, { useEffect, useState } from 'react';
import { getPatientRecords, decryptRecord, getUsers, shareRecord } from '../services/api';

const PatientDashboard = () => {
    const [records, setRecords] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [decryptedData, setDecryptedData] = useState({});
    const [sharingRecord, setSharingRecord] = useState(null); // ID of record being shared
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    useEffect(() => {
        if (currentUser?.id) {
            getPatientRecords(currentUser.id).then((res) => {
                console.log(res);
                setRecords(res.data)
            });
        }
        getUsers().then(res => setDoctors(res.data.filter(u => u.role === 'DOCTOR_B')));
    }, []);

    const handleDecrypt = async (recordId) => {
        try {
            const res = await decryptRecord(currentUser.id, recordId);
            setDecryptedData(prev => ({ ...prev, [recordId]: res.data }));
        } catch (err) {
            alert('Decryption failed!');
        }
    };

    const handleShare = async (recordId, doctorId) => {
        try {
            await shareRecord({
                recordId: recordId,
                targetDoctorId: doctorId
            });
            alert('Record shared successfully via Proxy Re-Encryption!');
            setSharingRecord(null);
        } catch (err) {
            console.error(err);
            alert('Sharing failed.');
        }
    };

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold">My Medical Records</h2>
                <span className="text-gray-600">{currentUser?.name}</span>
            </div>

            <div className="grid gap-6">
                {records.map(record => (
                    <div key={record.id} className="bg-white p-6 rounded shadow border border-gray-100">
                        <div className="flex justify-between items-start mb-4">
                            <div>
                                <h3 className="font-semibold text-lg">{record.description || "Medical Record"}</h3>
                                <p className="text-sm text-gray-500">Created: {new Date(record.createdAt).toLocaleString()}</p>
                                <p className="text-sm text-gray-500">Dr. {record.doctorA.name}</p>
                            </div>
                        </div>

                        <div className="bg-gray-50 p-4 rounded mb-4 font-mono text-xs break-all">
                            <strong>Encrypted Data (Server View):</strong>
                            <br />
                            {record.encryptedData.substring(0, 100)}...
                        </div>

                        {decryptedData[record.id] && (
                            <div className="bg-green-50 p-4 rounded mb-4 border border-green-200">
                                <strong className="text-green-800">Decrypted Content:</strong>
                                <p className="text-green-900 mt-1">{decryptedData[record.id]}</p>
                            </div>
                        )}

                        <div className="flex gap-4 mt-4">
                            <button
                                onClick={() => handleDecrypt(record.id)}
                                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 text-sm"
                            >
                                Decrypt Record
                            </button>

                            <div className="relative">
                                <button
                                    onClick={() => setSharingRecord(sharingRecord === record.id ? null : record.id)}
                                    className="px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700 text-sm"
                                >
                                    Share with Specialist
                                </button>

                                {sharingRecord === record.id && (
                                    <div className="absolute top-full mt-2 left-0 w-64 bg-white border shadow-lg rounded p-2 z-10">
                                        <p className="text-xs font-semibold mb-2 px-2">Select Specialist (Doctor B):</p>
                                        {doctors.map(doc => (
                                            <button
                                                key={doc.id}
                                                onClick={() => handleShare(record.id, doc.id)}
                                                className="block w-full text-left px-2 py-1 text-sm hover:bg-purple-50 rounded"
                                            >
                                                {doc.name}
                                            </button>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                ))}
                {records.length === 0 && <p>No records found.</p>}
            </div>
        </div>
    );
};

export default PatientDashboard;
