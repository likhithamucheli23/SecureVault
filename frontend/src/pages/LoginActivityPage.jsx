import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { Activity, ShieldAlert, ShieldCheck } from 'lucide-react';

const LoginActivityPage = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHistory = async () => {
      try {
        const response = await axiosInstance.get('/security/login-history');
        setLogs(response.data);
      } catch (error) {
        console.error("Error fetching login history", error);
      } finally {
        setLoading(false);
      }
    };

    fetchHistory();
  }, []);

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div className="page-title-group">
            <h1>Login Activity & Security Audit</h1>
            <p>Recorded authentication attempts, device details, and security audit logs</p>
          </div>
        </div>

        <div className="card">
          {loading ? (
            <p style={{ color: 'var(--text-muted)' }}>Loading security logs...</p>
          ) : logs.length === 0 ? (
            <p style={{ color: 'var(--text-muted)' }}>No login activity recorded yet.</p>
          ) : (
            <div className="table-container">
              <table className="table">
                <thead>
                  <tr>
                    <th>Status</th>
                    <th>Timestamp</th>
                    <th>IP Address</th>
                    <th>Browser / User Agent</th>
                    <th>Event Type</th>
                    <th>Failure Reason</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log) => (
                    <tr key={log.id}>
                      <td>
                        {log.loginStatus === 'SUCCESS' ? (
                          <span className="badge badge-success">✓ Success</span>
                        ) : (
                          <span className="badge badge-danger">✕ Failed</span>
                        )}
                      </td>
                      <td>{new Date(log.timestamp).toLocaleString()}</td>
                      <td className="mono">{log.ipAddress}</td>
                      <td style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>{log.userAgent}</td>
                      <td>
                        <span className="badge badge-info">{log.eventType}</span>
                      </td>
                      <td style={{ color: log.failureReason ? 'var(--danger)' : 'var(--text-muted)' }}>
                        {log.failureReason || '—'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default LoginActivityPage;
