import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { Smartphone, Monitor, ShieldCheck, ShieldAlert, Trash2, CheckCircle } from 'lucide-react';

const DevicesPage = () => {
  const [devices, setDevices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchDevices = async () => {
    setLoading(true);
    try {
      const res = await axiosInstance.get('/devices');
      setDevices(res.data);
    } catch (err) {
      setError('Failed to load device list.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDevices();
  }, []);

  const handleRevoke = async (id) => {
    try {
      await axiosInstance.post(`/devices/${id}/revoke`);
      setSuccess('Device access revoked successfully.');
      fetchDevices();
    } catch (err) {
      setError('Failed to revoke device.');
    }
  };

  const handleTrust = async (id) => {
    try {
      await axiosInstance.post(`/devices/${id}/trust`);
      setSuccess('Device trust status updated.');
      fetchDevices();
    } catch (err) {
      setError('Failed to update device trust.');
    }
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="dashboard-layout">
        <main className="main-content" style={{ maxWidth: '900px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 'bold', margin: 0 }}>Device Management</h1>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                View and control devices associated with your SecureVault account
              </p>
            </div>
          </div>

          {error && <div className="alert-error" style={{ marginBottom: '1rem' }}>{error}</div>}
          {success && <div style={{ padding: '0.75rem', backgroundColor: 'var(--success-light)', color: 'var(--success)', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>{success}</div>}

          {loading ? (
            <p style={{ color: 'var(--text-muted)' }}>Loading devices...</p>
          ) : devices.length === 0 ? (
            <div style={{ padding: '2rem', textAlign: 'center', backgroundColor: 'var(--bg-card)', borderRadius: 'var(--radius-lg)', color: 'var(--text-muted)' }}>
              No devices registered. Log in from a device to track it here.
            </div>
          ) : (
            <div className="activity-list">
              {devices.map((device) => (
                <div key={device.id} className="activity-item" style={{ padding: '1rem 1.25rem' }}>
                  <div className="activity-item-left">
                    <div className="activity-icon-badge" style={{ backgroundColor: device.revoked ? 'var(--danger-light)' : 'var(--success-light)', color: device.revoked ? 'var(--danger)' : 'var(--success)' }}>
                      {device.deviceName.toLowerCase().includes('mobile') || device.deviceName.toLowerCase().includes('iphone') ? <Smartphone size={20} /> : <Monitor size={20} />}
                    </div>
                    <div>
                      <div className="activity-title" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        {device.deviceName}
                        {device.trusted && (
                          <span style={{ fontSize: '0.75rem', padding: '0.1rem 0.4rem', backgroundColor: '#d1fae5', color: '#047857', borderRadius: '4px' }}>
                            Trusted Device
                          </span>
                        )}
                        {device.revoked && (
                          <span style={{ fontSize: '0.75rem', padding: '0.1rem 0.4rem', backgroundColor: '#fee2e2', color: '#b91c1c', borderRadius: '4px' }}>
                            Revoked Access
                          </span>
                        )}
                      </div>
                      <div className="activity-subtitle">
                        OS: {device.os} • Browser: {device.browser} • IP: {device.ipAddress || '127.0.0.1'}
                      </div>
                    </div>
                  </div>

                  <div className="activity-item-right" style={{ gap: '0.5rem' }}>
                    {!device.revoked && (
                      <button 
                        className="btn btn-secondary btn-sm"
                        onClick={() => handleTrust(device.id)}
                      >
                        {device.trusted ? 'Untrust' : 'Trust Device'}
                      </button>
                    )}
                    {!device.revoked ? (
                      <button 
                        className="btn btn-secondary btn-sm" 
                        style={{ color: 'var(--danger)', borderColor: '#fca5a5' }}
                        onClick={() => handleRevoke(device.id)}
                      >
                        Revoke Access
                      </button>
                    ) : (
                      <span style={{ fontSize: '0.85rem', color: 'var(--danger)', fontWeight: 'bold' }}>Revoked</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default DevicesPage;
