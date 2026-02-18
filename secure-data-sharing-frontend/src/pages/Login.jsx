import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { getUsers } from '../services/api';

const Login = () => {
    const [users, setUsers] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        getUsers()
            .then((res) => setUsers(res.data))
            .catch((err) => console.error("Failed to load users", err));
    }, []);

    const handleLogin = (user) => {
        localStorage.setItem('currentUser', JSON.stringify(user));
        if (user.role === 'DOCTOR_A') navigate('/doctor-a');
        else if (user.role === 'PATIENT') navigate('/patient');
        else if (user.role === 'DOCTOR_B') navigate('/doctor-b');
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-[50vh]">
            <h2 className="text-2xl font-bold mb-6">Select Role to Login</h2>
            <div className="grid gap-4 w-full max-w-md">
                {users.map((user) => (
                    <button
                        key={user.id}
                        onClick={() => handleLogin(user)}
                        className="p-4 border rounded-lg hover:bg-blue-50 text-left shadow-sm transition-colors flex justify-between items-center"
                    >
                        <div>
                            <span className="font-semibold block">{user.name}</span>
                            <span className="text-sm text-gray-500">{user.role}</span>
                        </div>
                        <span className="text-blue-600 text-sm">Login &rarr;</span>
                    </button>
                ))}
                {users.length === 0 && <p className="text-center text-gray-500">Loading users from backend...</p>}
            </div>
        </div>
    );
};

export default Login;
