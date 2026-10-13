import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { ShieldAlert, Users, Activity, CheckCircle } from 'lucide-react';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const [logs, setLogs] = useState([]);
  const [activeTab, setActiveTab] = useState('users');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchAdminData = async () => {
    setLoading(true);
    try {
      if (activeTab === 'users') {
        const res = await axiosInstance.get('/admin/users');
        setUsers(res.data);
      } else {
        const res = await axiosInstance.get('/admin/audit-logs');
        setLogs(res.data);
      }
    } catch (err) {
      setError(err.response?.data?.message || 'Access denied: Requires ADMINISTRATOR role.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, [activeTab]);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await axiosInstance.put(`/admin/users/${userId}/role`, { role: newRole });
      setSuccess(`User role updated to ${newRole}`);
      fetchAdminData();
    } catch (err) {
      setError('Failed to update user role.');
    }
  };

  return (
    <div className="app-container">
      <Sidebar />
      <div className="dashboard-layout">
        <main className="main-content">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div>
              <h1 style={{ fontSize: '1.5rem', fontWeight: 'bold', margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <ShieldAlert style={{ color: 'var(--danger)' }} /> Admin Security Console
              </h1>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                Backend RBAC enforcement for user permissions and system audit logs
              </p>
            </div>
            <div style={{ display: 'flex', gap: '0.5rem' }}>
              <button 
                className={`btn ${activeTab === 'users' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setActiveTab('users')}
              >
                User Roles
              </button>
              <button 
                className={`btn ${activeTab === 'logs' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setActiveTab('logs')}
              >
                Audit Logs
              </button>
            </div>
          </div>

          {error && <div className="alert-error" style={{ marginBottom: '1rem' }}>{error}</div>}
          {success && <div style={{ padding: '0.75rem', backgroundColor: 'var(--success-light)', color: 'var(--success)', borderRadius: 'var(--radius-md)', marginBottom: '1rem' }}>{success}</div>}

          {loading ? (
            <p style={{ color: 'var(--text-muted)' }}>Loading administrative data...</p>
          ) : activeTab === 'users' ? (
            <div className="activity-list">
              {users.map((u) => (
                <div key={u.id} className="activity-item">
                  <div>
                    <div className="activity-title">{u.name} ({u.email})</div>
                    <div className="activity-subtitle">Role: <strong>{u.role}</strong> • MFA: {u.mfaEnabled ? 'Enabled' : 'Disabled'}</div>
                  </div>
                  <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                    <select
                      className="form-control"
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                      style={{ padding: '0.35rem 0.6rem', fontSize: '0.85rem' }}
                    >
                      <option value="USER">USER</option>
                      <option value="TEAM_MEMBER">TEAM_MEMBER</option>
                      <option value="ADMINISTRATOR">ADMINISTRATOR</option>
                    </select>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="activity-list">
              {logs.map((log) => (
                <div key={log.id} className="activity-item">
                  <div>
                    <div className="activity-title">{log.eventType} ({log.attemptedEmail})</div>
                    <div className="activity-subtitle">IP: {log.ipAddress} • {new Date(log.timestamp).toLocaleString()}</div>
                  </div>
                  <span className="activity-tag" style={{ backgroundColor: log.loginStatus === 'SUCCESS' ? '#d1fae5' : '#fee2e2', color: log.loginStatus === 'SUCCESS' ? '#047857' : '#b91c1c' }}>
                    {log.loginStatus}
                  </span>
                </div>
              ))}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default AdminPage;
