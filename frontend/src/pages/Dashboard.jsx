import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { 
  Search, 
  Star, 
  Briefcase, 
  User, 
  GraduationCap, 
  Archive, 
  Plus, 
  UploadCloud, 
  Share2, 
  MoreHorizontal, 
  ShieldCheck, 
  Key, 
  Users, 
  Activity, 
  AlertTriangle, 
  ShieldAlert,
  Download,
  FileSpreadsheet,
  FileText,
  Bell,
  Check
} from 'lucide-react';

const Dashboard = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [timeRange, setTimeRange] = useState('7days');
  const [analytics, setAnalytics] = useState({
    successfulLogins: 0,
    failedLogins: 0,
    suspiciousActivities: 0,
    activeSecurityAlerts: 0,
    totalCredentials: 0,
    sharedCredentials: 0,
    teamVaults: 0,
    activeAlertsList: [],
    recentActivity: []
  });
  const [loading, setLoading] = useState(true);
  const [notifications, setNotifications] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);

  const fetchAnalytics = async (range) => {
    setLoading(true);
    try {
      const response = await axiosInstance.get(`/security/analytics?range=${range}`);
      setAnalytics(response.data);
    } catch (err) {
      console.error("Error loading security analytics", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchNotifications = async () => {
    try {
      const res = await axiosInstance.get('/notifications');
      setNotifications(res.data);
    } catch (e) {
      // silent catch
    }
  };

  useEffect(() => {
    fetchAnalytics(timeRange);
    fetchNotifications();
  }, [timeRange]);

  const handleDownloadReport = async (format) => {
    try {
      const response = await axiosInstance.get(`/reports/download?type=security&format=${format}`, {
        responseType: 'blob',
      });
      const mimeType = format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf';
      const fileExt = format === 'excel' ? 'xlsx' : 'pdf';
      
      const blob = new Blob([response.data], { type: mimeType });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', `SecureVault_Security_Report.${fileExt}`);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Error downloading report", err);
      alert("Failed to download report. Please try again.");
    }
  };

  const handleMarkNotificationsRead = async () => {
    try {
      await axiosInstance.post('/notifications/read-all');
      fetchNotifications();
    } catch (e) {}
  };



  const unreadCount = notifications.filter(n => !n.readStatus).length;

  return (
    <div className="app-container">
      <Sidebar />

      <div className="dashboard-layout">
        {/* Main Content (Center) */}
        <main className="main-content">
          {/* Top Search Bar & Header Controls */}
          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', marginBottom: '1.5rem' }}>
            <div className="top-search-bar" style={{ flex: 1, margin: 0 }}>
              <Search size={18} color="var(--text-light)" />
              <input 
                type="text" 
                className="top-search-input" 
                placeholder="Search credentials, security activity, or audit logs..." 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>

            {/* Time Filter */}
            <select
              className="form-control"
              value={timeRange}
              onChange={(e) => setTimeRange(e.target.value)}
              style={{ width: 'auto', padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
            >
              <option value="today">Today</option>
              <option value="7days">Last 7 Days</option>
              <option value="30days">Last 30 Days</option>
              <option value="all">All Time</option>
            </select>

            {/* Export Reports Buttons */}
            <button 
              className="btn btn-secondary btn-sm" 
              onClick={() => handleDownloadReport('pdf')}
              title="Export PDF Security Report"
              style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}
            >
              <FileText size={16} color="#ef4444" /> PDF Report
            </button>
            <button 
              className="btn btn-secondary btn-sm" 
              onClick={() => handleDownloadReport('excel')}
              title="Export Excel Security Audit"
              style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}
            >
              <FileSpreadsheet size={16} color="#10b981" /> Excel Export
            </button>

            {/* Notifications Bell */}
            <div style={{ position: 'relative' }}>
              <button
                className="btn btn-secondary btn-sm"
                onClick={() => setShowNotifications(!showNotifications)}
                style={{ padding: '0.5rem', position: 'relative' }}
              >
                <Bell size={18} />
                {unreadCount > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '-4px',
                    right: '-4px',
                    backgroundColor: 'var(--danger)',
                    color: '#fff',
                    borderRadius: '50%',
                    width: '16px',
                    height: '16px',
                    fontSize: '0.65rem',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    fontWeight: 'bold'
                  }}>
                    {unreadCount}
                  </span>
                )}
              </button>

              {/* Notification Drawer */}
              {showNotifications && (
                <div style={{
                  position: 'absolute',
                  right: 0,
                  top: '2.5rem',
                  width: '320px',
                  backgroundColor: 'var(--bg-card)',
                  border: '1px solid var(--border-color)',
                  borderRadius: 'var(--radius-lg)',
                  boxShadow: '0 10px 25px rgba(0,0,0,0.15)',
                  zIndex: 100,
                  padding: '1rem'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.75rem' }}>
                    <strong style={{ fontSize: '0.9rem' }}>Notifications ({notifications.length})</strong>
                    {unreadCount > 0 && (
                      <button
                        onClick={handleMarkNotificationsRead}
                        style={{ background: 'none', border: 'none', color: 'var(--primary)', fontSize: '0.75rem', cursor: 'pointer' }}
                      >
                        Mark all read
                      </button>
                    )}
                  </div>
                  {notifications.length === 0 ? (
                    <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>No notifications recorded.</p>
                  ) : (
                    <div style={{ maxHeight: '250px', overflowY: 'auto' }}>
                      {notifications.slice(0, 5).map((n) => (
                        <div key={n.id} style={{ padding: '0.5rem 0', borderBottom: '1px solid var(--border-color)', fontSize: '0.8rem' }}>
                          <div style={{ fontWeight: 'bold', color: 'var(--text-main)' }}>{n.title}</div>
                          <div style={{ color: 'var(--text-muted)' }}>{n.message}</div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>

          {/* Categories Grid */}
          <h2 className="section-title">Categories</h2>
          <div className="categories-grid">
            <div className="category-card purple" onClick={() => navigate('/vault')}>
              <div className="category-card-top">
                <div className="category-card-icon">
                  <Key size={22} />
                </div>
                <Star size={16} className="category-card-star" fill="#fde047" />
              </div>
              <div className="category-card-title">Passwords</div>
              <div className="category-card-subtitle">{analytics.totalCredentials} credentials</div>
            </div>

            <div className="category-card cyan" onClick={() => navigate('/login-activity')}>
              <div className="category-card-top">
                <div className="category-card-icon">
                  <ShieldCheck size={22} />
                </div>
              </div>
              <div className="category-card-title">Access Logins</div>
              <div className="category-card-subtitle">{analytics.successfulLogins} successful</div>
            </div>

            <div className="category-card pink" onClick={() => navigate('/login-activity')}>
              <div className="category-card-top">
                <div className="category-card-icon">
                  <ShieldAlert size={22} />
                </div>
              </div>
              <div className="category-card-title">Security Alerts</div>
              <div className="category-card-subtitle">{analytics.activeSecurityAlerts} active alerts</div>
            </div>

            <div className="category-card blue" onClick={() => navigate('/teams')}>
              <div className="category-card-top">
                <div className="category-card-icon">
                  <Users size={22} />
                </div>
              </div>
              <div className="category-card-title">Team Vaults</div>
              <div className="category-card-subtitle">{analytics.teamVaults} active vaults</div>
            </div>
          </div>

          {/* Vault Folders */}
          <h2 className="section-title">Vault Folders</h2>
          <div className="vault-groups-row">
            <div className="vault-group-item" onClick={() => navigate('/vault')}>
              <div className="vault-group-header">
                <div className="vault-group-icon" style={{ backgroundColor: '#dbeafe', color: '#2563eb' }}>
                  <Briefcase size={16} />
                </div>
                <span className="vault-group-name">Work</span>
              </div>
              <div className="vault-group-count">{analytics.totalCredentials} items</div>
              <div className="vault-group-progress">
                <div 
                  className="vault-group-bar" 
                  style={{ 
                    width: analytics.totalCredentials > 0 ? `${Math.min(100, analytics.totalCredentials * 10)}%` : '0%', 
                    backgroundColor: '#2563eb' 
                  }}
                ></div>
              </div>
            </div>

            <div className="vault-group-item" onClick={() => navigate('/shared-with-me')}>
              <div className="vault-group-header">
                <div className="vault-group-icon" style={{ backgroundColor: '#ccfbf1', color: '#0d9488' }}>
                  <User size={16} />
                </div>
                <span className="vault-group-name">Personal</span>
              </div>
              <div className="vault-group-count">{analytics.sharedCredentials} items</div>
              <div className="vault-group-progress">
                <div 
                  className="vault-group-bar" 
                  style={{ 
                    width: analytics.sharedCredentials > 0 ? `${Math.min(100, analytics.sharedCredentials * 10)}%` : '0%', 
                    backgroundColor: '#0d9488' 
                  }}
                ></div>
              </div>
            </div>

            <div className="vault-group-item" onClick={() => navigate('/teams')}>
              <div className="vault-group-header">
                <div className="vault-group-icon" style={{ backgroundColor: '#fce7f3', color: '#db2777' }}>
                  <GraduationCap size={16} />
                </div>
                <span className="vault-group-name">Teams</span>
              </div>
              <div className="vault-group-count">{analytics.teamVaults} vaults</div>
              <div className="vault-group-progress">
                <div 
                  className="vault-group-bar" 
                  style={{ 
                    width: analytics.teamVaults > 0 ? `${Math.min(100, analytics.teamVaults * 10)}%` : '0%', 
                    backgroundColor: '#db2777' 
                  }}
                ></div>
              </div>
            </div>

            <div className="vault-group-item" onClick={() => navigate('/login-activity')}>
              <div className="vault-group-header">
                <div className="vault-group-icon" style={{ backgroundColor: '#e0e7ff', color: '#4f46e5' }}>
                  <Archive size={16} />
                </div>
                <span className="vault-group-name">Audit Logs</span>
              </div>
              <div className="vault-group-count">{analytics.successfulLogins + analytics.failedLogins} logs</div>
              <div className="vault-group-progress">
                <div 
                  className="vault-group-bar" 
                  style={{ 
                    width: (analytics.successfulLogins + analytics.failedLogins) > 0 ? `${Math.min(100, (analytics.successfulLogins + analytics.failedLogins) * 10)}%` : '0%', 
                    backgroundColor: '#4f46e5' 
                  }}
                ></div>
              </div>
            </div>

            <div className="vault-group-add-btn" onClick={() => navigate('/vault')} title="Add New Credential">
              <Plus size={24} />
            </div>
          </div>


        </main>

        {/* Right Panel */}
        <aside className="right-panel">
          <div className="add-file-card" onClick={() => navigate('/vault')}>
            <div className="add-file-icon">
              <UploadCloud size={28} />
            </div>
            <div className="add-file-title">Add new credential</div>
          </div>

          <div className="storage-card">
            <div className="storage-header">
              <span className="storage-title">Vault Security Health</span>
              <span className="storage-percentage">
                {analytics.failedLogins > 0 ? '75% Health' : '98% Excellent'}
              </span>
            </div>
            <div className="storage-subtitle">
              {analytics.totalCredentials} credentials protected with AES-256-GCM
            </div>
            <div className="storage-progress-track">
              <div 
                className="storage-progress-fill" 
                style={{ width: analytics.failedLogins > 0 ? '75%' : '98%' }}
              ></div>
            </div>
          </div>

          <div>
            <h3 className="section-title" style={{ fontSize: '0.95rem', marginBottom: '0.85rem' }}>
              Shared Vaults & Access
            </h3>
            <div className="shared-folders-list">
              <div className="shared-folder-pill cyan" onClick={() => navigate('/shared-with-me')}>
                <span className="shared-folder-title">Shared Credentials</span>
                <div className="avatar-stack">
                  <div className="avatar-stack-item" style={{ backgroundColor: '#0284c7' }}>A</div>
                  <div className="avatar-stack-item" style={{ backgroundColor: '#7c3aed' }}>B</div>
                </div>
              </div>

              <div className="shared-folder-pill purple" onClick={() => navigate('/teams')}>
                <span className="shared-folder-title">Team Vault</span>
                <div className="avatar-stack">
                  <div className="avatar-stack-item" style={{ backgroundColor: '#059669' }}>T</div>
                </div>
              </div>

              <button 
                className="btn btn-secondary btn-sm" 
                style={{ width: '100%', marginTop: '0.25rem', color: 'var(--primary)', borderColor: 'var(--border-color)' }}
                onClick={() => navigate('/teams')}
              >
                + Add team vault
              </button>
            </div>
          </div>
        </aside>
      </div>
    </div>
  );
};

export default Dashboard;
