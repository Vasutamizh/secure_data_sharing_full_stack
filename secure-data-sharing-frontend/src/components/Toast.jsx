import React, { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

let toastIdCounter = 0;

export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([]);

    const addToast = useCallback((message, type = 'info', duration = 4500) => {
        const id = ++toastIdCounter;
        setToasts(prev => [...prev, { id, message, type }]);
        setTimeout(() => {
            setToasts(prev => prev.filter(t => t.id !== id));
        }, duration);
    }, []);

    const removeToast = useCallback((id) => {
        setToasts(prev => prev.filter(t => t.id !== id));
    }, []);

    const toast = {
        success: (msg) => addToast(msg, 'success'),
        error: (msg) => addToast(msg, 'error', 6000),
        info: (msg) => addToast(msg, 'info'),
        warning: (msg) => addToast(msg, 'warning', 5500),
    };

    return (
        <ToastContext.Provider value={toast}>
            {children}
            {/* Toast Container */}
            <div className="toast-container">
                {toasts.map((t) => (
                    <div key={t.id} className={`toast toast-${t.type}`}>
                        <div className="toast-icon">
                            {t.type === 'success' && '✓'}
                            {t.type === 'error' && '✕'}
                            {t.type === 'info' && 'ℹ'}
                            {t.type === 'warning' && '⚠'}
                        </div>
                        <p className="toast-message">{t.message}</p>
                        <button className="toast-close" onClick={() => removeToast(t.id)}>×</button>
                    </div>
                ))}
            </div>
        </ToastContext.Provider>
    );
}

export function useToast() {
    const ctx = useContext(ToastContext);
    if (!ctx) throw new Error('useToast must be used within a ToastProvider');
    return ctx;
}
