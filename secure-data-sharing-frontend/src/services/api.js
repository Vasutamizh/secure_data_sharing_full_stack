import axios from 'axios';

const api = axios.create({
    baseURL: '/api', // Vite proxy handles this
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getUsers = () => api.get('/users');
export const createRecord = (data) => api.post('/doctorA/create-record', data);
export const getPatientRecords = (patientId) => api.get(`/patient/${patientId}/records`);
export const decryptRecord = (patientId, recordId) => api.get(`/patient/${patientId}/records/${recordId}/decrypt`);
export const shareRecord = (data) => api.post('/patient/share', data);
export const getSharedRecords = (doctorId) => api.get(`/doctorB/${doctorId}/shared-records`);
export const decryptSharedRecord = (doctorId, sharedId) => api.get(`/doctorB/${doctorId}/shared-records/${sharedId}/decrypt`);
export const createUser = (data) => api.post('/users', data);

export default api;
