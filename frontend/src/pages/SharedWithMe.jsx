import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { Share2, Eye, EyeOff, Copy, Check, User, Clock, Shield, Edit2 } from 'lucide-react';

const SharedWithMe = () => {
  const [sharedCredentials, setSharedCredentials] = useState([]);
  const [loading, setLoading] = useState(true);
  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [copiedId, setCopiedId] = useState(null);

  // Edit modal for shared credentials with EDIT_ACCESS or FULL_MANAGEMENT
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedCred, setSelectedCred] = useState(null);
  const [formData, setFormData] = useState({ title: '', username: '', password: '', website: '', notes: '' });
  const [errorMsg, setErrorMsg] = useState('');

  const fetchSharedCredentials = async () => {
    try {
      const response = await axiosInstance.get('/vault/shared-with-me');
      setSharedCredentials(response.data);
    } catch (error) {
      console.error("Error fetching shared credentials", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSharedCredentials();
  }, []);

  const togglePasswordVisibility = (id) => {
    setVisiblePasswords(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleOpenEdit = (cred) => {
    setSelectedCred(cred);
    setFormData({
      title: cred.title,
      username: cred.username,
      password: cred.password || '',
      website: cred.website || '',
      notes: cred.notes || '',
    });
    setErrorMsg('');
    setShowEditModal(true);
  };

  const handleUpdateShared = async (e) => {
    e.preventDefault();
    setErrorMsg('');
    try {
      await axiosInstance.put(`/vault/credentials/${selectedCred.id}`, formData);
      setShowEditModal(false);
      fetchSharedCredentials();
    } catch (error) {
      setErrorMsg(error.response?.data?.message || 'Failed to update shared credential');
    }
  };

  const getPermissionBadge = (level) => {
    if (level === 'FULL_MANAGEMENT') return <span className="badge badge-success">Full Management</span>;
    if (level === 'EDIT_ACCESS') return <span className="badge badge-warning">Edit Access</span>;
    return <span className="badge badge-info">View Only</span>;
  };

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div className="page-title-group">
            <h1>Shared With Me</h1>
            <p>Credentials shared with you by other SecureVault users</p>
          </div>
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Fetching shared credentials...</p>
        ) : sharedCredentials.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem 1.5rem' }}>
            <Share2 size={48} color="var(--text-muted)" style={{ margin: '0 auto 1rem auto' }} />
            <h3>No shared credentials</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '0.5rem' }}>
              Credentials shared with your email address will appear here.
            </p>
          </div>
        ) : (
          <div className="grid-cards">
            {sharedCredentials.map((cred) => {
              const isPasswordVisible = visiblePasswords[cred.id];
              const canEdit = cred.permissionLevel === 'EDIT_ACCESS' || cred.permissionLevel === 'FULL_MANAGEMENT';

              return (
                <div key={cred.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                      <h3 style={{ fontSize: '1.1rem', fontWeight: '700', color: 'var(--text-main)' }}>{cred.title}</h3>
                      {getPermissionBadge(cred.permissionLevel)}
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', fontSize: '0.875rem', marginBottom: '1rem' }}>
                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ color: 'var(--text-muted)' }}>Owner:</span>
                        <span style={{ fontWeight: '600', color: 'var(--primary)' }}>{cred.owner?.name} ({cred.owner?.email})</span>
                      </div>

                      <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <span style={{ color: 'var(--text-muted)' }}>Username:</span>
                        <span className="mono" style={{ fontWeight: '600' }}>{cred.username}</span>
                      </div>

                      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <span style={{ color: 'var(--text-muted)' }}>Password:</span>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                          <span className="mono" style={{ fontWeight: '600', letterSpacing: isPasswordVisible ? '0' : '0.1em' }}>
                            {isPasswordVisible ? cred.password : '••••••••••••'}
                          </span>
                          <button
                            type="button"
                            className="toggle-password-btn"
                            style={{ position: 'static' }}
                            onClick={() => togglePasswordVisibility(cred.id)}
                            title={isPasswordVisible ? 'Hide Password' : 'Show Password'}
                          >
                            {isPasswordVisible ? <EyeOff size={16} /> : <Eye size={16} />}
                          </button>
                          <button
                            type="button"
                            className="toggle-password-btn"
                            style={{ position: 'static' }}
                            onClick={() => copyToClipboard(cred.password, cred.id)}
                            title="Copy Password"
                          >
                            {copiedId === cred.id ? <Check size={16} color="var(--success)" /> : <Copy size={16} />}
                          </button>
                        </div>
                      </div>

                      {cred.expirationDate && (
                        <div style={{ display: 'flex', justifyContent: 'space-between', color: 'var(--warning)', fontSize: '0.8rem' }}>
                          <span>Expires:</span>
                          <span>{new Date(cred.expirationDate).toLocaleString()}</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {canEdit && (
                    <div style={{ paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
                      <button className="btn btn-secondary btn-sm" style={{ width: '100%' }} onClick={() => handleOpenEdit(cred)}>
                        <Edit2 size={14} />
                        <span>Edit Shared Credential</span>
                      </button>
                    </div>
                  )}
                </div>
              );
            })}
          </div>
        )}

        {/* EDIT SHARED CREDENTIAL MODAL */}
        {showEditModal && selectedCred && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Edit Shared "{selectedCred.title}"</h2>
                <button className="modal-close" onClick={() => setShowEditModal(false)}>✕</button>
              </div>
              <form onSubmit={handleUpdateShared}>
                <div className="modal-body">
                  {errorMsg && <div className="alert-error">{errorMsg}</div>}
                  
                  <div className="form-group">
                    <label className="form-label">Title *</label>
                    <input
                      type="text"
                      required
                      className="form-control"
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Username / Email *</label>
                    <input
                      type="text"
                      required
                      className="form-control"
                      value={formData.username}
                      onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Password *</label>
                    <input
                      type="text"
                      required
                      className="form-control mono"
                      value={formData.password}
                      onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Changes</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default SharedWithMe;
