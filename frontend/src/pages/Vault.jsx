import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import PasswordGenerator from '../components/PasswordGenerator';
import { 
  Key, Plus, Search, Eye, EyeOff, Copy, Check, Edit2, Trash2, Share2, 
  X, ExternalLink, Star, FileText, CreditCard, ShieldCheck, Filter
} from 'lucide-react';

const Vault = () => {
  const [credentials, setCredentials] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [selectedType, setSelectedType] = useState('ALL');
  const [onlyFavorites, setOnlyFavorites] = useState(false);
  const [loading, setLoading] = useState(true);

  // Modals state
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showShareModal, setShowShareModal] = useState(false);
  const [showGeneratorModal, setShowGeneratorModal] = useState(false);

  const [selectedCred, setSelectedCred] = useState(null);

  // Form states
  const [formData, setFormData] = useState({
    title: '',
    username: '',
    password: '',
    website: '',
    notes: '',
    category: 'Work',
    type: 'LOGIN',
    favorite: false
  });

  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [copiedId, setCopiedId] = useState(null);
  const [formError, setFormError] = useState('');

  // Share form state
  const [shareEmail, setShareEmail] = useState('');
  const [sharePermission, setSharePermission] = useState('VIEW_ONLY');
  const [shareExpiration, setShareExpiration] = useState('');
  const [shareMessage, setShareMessage] = useState('');

  const fetchCredentials = async () => {
    try {
      const response = await axiosInstance.get('/vault/credentials');
      setCredentials(response.data);
    } catch (error) {
      console.error("Error fetching credentials", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCredentials();
  }, []);

  const togglePasswordVisibility = (id) => {
    setVisiblePasswords(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleToggleFavorite = async (id) => {
    try {
      await axiosInstance.put(`/vault/credentials/${id}/favorite`);
      fetchCredentials();
    } catch (error) {
      console.error("Failed to toggle favorite", error);
    }
  };

  const handleOpenAddModal = () => {
    setFormData({ title: '', username: '', password: '', website: '', notes: '', category: 'Work', type: 'LOGIN', favorite: false });
    setFormError('');
    setShowAddModal(true);
  };

  const handleOpenEditModal = (cred) => {
    setSelectedCred(cred);
    setFormData({
      title: cred.title,
      username: cred.username,
      password: cred.password || '',
      website: cred.website || '',
      notes: cred.notes || '',
      category: cred.category || 'Work',
      type: cred.type || 'LOGIN',
      favorite: cred.favorite || false
    });
    setFormError('');
    setShowEditModal(true);
  };

  const handleOpenShareModal = (cred) => {
    setSelectedCred(cred);
    setShareEmail('');
    setSharePermission('VIEW_ONLY');
    setShareExpiration('');
    setShareMessage('');
    setShowShareModal(true);
  };

  const handleCreateCredential = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await axiosInstance.post('/vault/credentials', formData);
      setShowAddModal(false);
      fetchCredentials();
    } catch (error) {
      setFormError(error.response?.data?.message || 'Failed to save credential');
    }
  };

  const handleUpdateCredential = async (e) => {
    e.preventDefault();
    setFormError('');
    try {
      await axiosInstance.put(`/vault/credentials/${selectedCred.id}`, formData);
      setShowEditModal(false);
      fetchCredentials();
    } catch (error) {
      setFormError(error.response?.data?.message || 'Failed to update credential');
    }
  };

  const handleDeleteCredential = async (id) => {
    if (!window.confirm("Are you sure you want to delete this credential? This action cannot be undone.")) {
      return;
    }
    try {
      await axiosInstance.delete(`/vault/credentials/${id}`);
      fetchCredentials();
    } catch (error) {
      alert(error.response?.data?.message || 'Failed to delete credential');
    }
  };

  const handleShareSubmit = async (e) => {
    e.preventDefault();
    setShareMessage('');
    try {
      await axiosInstance.post(`/vault/credentials/${selectedCred.id}/share`, {
        userEmail: shareEmail,
        permissionLevel: sharePermission,
        expirationDate: shareExpiration ? new Date(shareExpiration).toISOString() : null,
      });
      setShareMessage('Credential shared successfully!');
      setShareEmail('');
      setShareExpiration('');
    } catch (error) {
      setShareMessage(`Error: ${error.response?.data?.message || 'Failed to share credential'}`);
    }
  };

  const filteredCredentials = credentials.filter(c => {
    const q = searchQuery.toLowerCase();
    const matchesSearch = !q || c.title.toLowerCase().includes(q) || c.username?.toLowerCase().includes(q) || (c.website && c.website.toLowerCase().includes(q));
    const matchesCategory = selectedCategory === 'ALL' || (c.category && c.category.toUpperCase() === selectedCategory.toUpperCase());
    const matchesType = selectedType === 'ALL' || (c.type && c.type.toUpperCase() === selectedType.toUpperCase());
    const matchesFavorite = !onlyFavorites || c.favorite;
    return matchesSearch && matchesCategory && matchesType && matchesFavorite;
  });

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content" style={{ maxWidth: '100%' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div>
            <h1 style={{ fontSize: '1.5rem', fontWeight: '800', color: 'var(--text-main)' }}>My Password Vault</h1>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>AES-256 encrypted credential & secure note repository</p>
          </div>
          <button className="btn btn-primary" onClick={handleOpenAddModal}>
            <Plus size={18} />
            <span>Add Item</span>
          </button>
        </div>

        {/* Top Search Bar & Advanced Categorization Filters */}
        <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', marginBottom: '1.5rem' }}>
          <div className="top-search-bar" style={{ flex: 1, margin: 0 }}>
            <Search size={18} color="var(--text-light)" />
            <input
              type="text"
              className="top-search-input"
              placeholder="Search credentials, notes, or categories..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <select
            className="form-control"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            style={{ width: 'auto', minWidth: '130px' }}
          >
            <option value="ALL">All Categories</option>
            <option value="Work">Work</option>
            <option value="Personal">Personal</option>
            <option value="Finance">Finance</option>
            <option value="Social">Social</option>
            <option value="Infrastructure">Infrastructure</option>
          </select>

          <select
            className="form-control"
            value={selectedType}
            onChange={(e) => setSelectedType(e.target.value)}
            style={{ width: 'auto', minWidth: '130px' }}
          >
            <option value="ALL">All Types</option>
            <option value="LOGIN">Login</option>
            <option value="SECURE_NOTE">Secure Note</option>
            <option value="API_KEY">API Key</option>
            <option value="BANK_CARD">Bank Card</option>
          </select>

          <button
            className={`btn ${onlyFavorites ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setOnlyFavorites(!onlyFavorites)}
            style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}
          >
            <Star size={16} fill={onlyFavorites ? '#fde047' : 'none'} color={onlyFavorites ? '#f59e0b' : 'currentColor'} />
            Favorites
          </button>
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Decrypting vault contents...</p>
        ) : filteredCredentials.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3.5rem 1.5rem' }}>
            <Key size={48} color="var(--primary)" style={{ margin: '0 auto 1rem auto' }} />
            <h3 style={{ fontSize: '1.1rem', fontWeight: '700' }}>No vault items found</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', margin: '0.5rem 0 1.5rem 0' }}>
              {searchQuery ? 'No items match your filter criteria.' : 'Your vault is empty. Add your first credential or secure note!'}
            </p>
            <button className="btn btn-primary" onClick={handleOpenAddModal}>
              <Plus size={18} />
              <span>Add Credential</span>
            </button>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '1.25rem' }}>
            {filteredCredentials.map((cred) => {
              const isPasswordVisible = visiblePasswords[cred.id];
              return (
                <div key={cred.id} className="card" style={{ display: 'flex', flexDirection: 'column', justifyContent: 'space-between', margin: 0 }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.85rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <button
                          type="button"
                          onClick={() => handleToggleFavorite(cred.id)}
                          style={{ background: 'none', border: 'none', cursor: 'pointer', padding: 0 }}
                          title={cred.favorite ? 'Unmark Favorite' : 'Mark Favorite'}
                        >
                          <Star size={18} fill={cred.favorite ? '#fde047' : 'none'} color={cred.favorite ? '#f59e0b' : 'var(--text-muted)'} />
                        </button>
                        <h3 style={{ fontSize: '1.05rem', fontWeight: '700', color: 'var(--text-main)', margin: 0 }}>{cred.title}</h3>
                      </div>
                      <span className="badge badge-info">{cred.category || 'General'}</span>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.6rem', fontSize: '0.875rem', marginBottom: '1.25rem' }}>
                      {cred.type === 'SECURE_NOTE' ? (
                        <div style={{ backgroundColor: 'var(--bg-app)', padding: '0.75rem', borderRadius: 'var(--radius-md)', fontSize: '0.85rem', color: 'var(--text-main)', whiteSpace: 'pre-wrap' }}>
                          <strong>Secure Note:</strong><br />
                          {cred.notes || '(No encrypted note content)'}
                        </div>
                      ) : (
                        <>
                          <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                            <span style={{ color: 'var(--text-muted)' }}>Username:</span>
                            <span className="mono" style={{ fontWeight: '600' }}>{cred.username}</span>
                          </div>

                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                            <span style={{ color: 'var(--text-muted)' }}>Password:</span>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                              <span className="mono" style={{ fontWeight: '600', letterSpacing: isPasswordVisible ? '0' : '0.1em' }}>
                                {isPasswordVisible ? cred.password : '••••••••••••'}
                              </span>
                              <button
                                type="button"
                                className="activity-action-btn"
                                onClick={() => togglePasswordVisibility(cred.id)}
                                title={isPasswordVisible ? 'Hide Password' : 'Show Password'}
                              >
                                {isPasswordVisible ? <EyeOff size={15} /> : <Eye size={15} />}
                              </button>
                              <button
                                type="button"
                                className="activity-action-btn"
                                onClick={() => copyToClipboard(cred.password, cred.id)}
                                title="Copy Password"
                              >
                                {copiedId === cred.id ? <Check size={15} color="var(--success)" /> : <Copy size={15} />}
                              </button>
                            </div>
                          </div>
                        </>
                      )}

                      {cred.website && (
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                          <span style={{ color: 'var(--text-muted)' }}>Website:</span>
                          <a
                            href={cred.website.startsWith('http') ? cred.website : `https://${cred.website}`}
                            target="_blank"
                            rel="noopener noreferrer"
                            style={{ color: 'var(--primary)', fontWeight: '600', display: 'inline-flex', alignItems: 'center', gap: '0.25rem' }}
                          >
                            {cred.website} <ExternalLink size={12} />
                          </a>
                        </div>
                      )}
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '0.5rem', paddingTop: '1rem', borderTop: '1px solid var(--border-color-light)' }}>
                    <button className="btn btn-secondary btn-sm" style={{ flex: 1 }} onClick={() => handleOpenShareModal(cred)}>
                      <Share2 size={14} />
                      <span>Share</span>
                    </button>
                    <button className="btn btn-secondary btn-sm" onClick={() => handleOpenEditModal(cred)}>
                      <Edit2 size={14} />
                    </button>
                    <button className="btn btn-danger btn-sm" onClick={() => handleDeleteCredential(cred.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* ADD CREDENTIAL MODAL */}
        {showAddModal && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Add Vault Item</h2>
                <button className="modal-close" onClick={() => setShowAddModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleCreateCredential}>
                <div className="modal-body">
                  {formError && <div className="badge badge-danger" style={{ display: 'block', width: '100%', marginBottom: '1rem', padding: '0.75rem' }}>{formError}</div>}
                  
                  <div className="form-group">
                    <label className="form-label">Title *</label>
                    <input
                      type="text"
                      required
                      className="form-control"
                      placeholder="e.g. GitHub, AWS Key, Private Note"
                      value={formData.title}
                      onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                    />
                  </div>

                  <div style={{ display: 'flex', gap: '1rem' }}>
                    <div className="form-group" style={{ flex: 1 }}>
                      <label className="form-label">Category</label>
                      <select
                        className="form-control"
                        value={formData.category}
                        onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                      >
                        <option value="Work">Work</option>
                        <option value="Personal">Personal</option>
                        <option value="Finance">Finance</option>
                        <option value="Social">Social</option>
                        <option value="Infrastructure">Infrastructure</option>
                      </select>
                    </div>

                    <div className="form-group" style={{ flex: 1 }}>
                      <label className="form-label">Item Type</label>
                      <select
                        className="form-control"
                        value={formData.type}
                        onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                      >
                        <option value="LOGIN">Login</option>
                        <option value="SECURE_NOTE">Secure Note</option>
                        <option value="API_KEY">API Key</option>
                        <option value="BANK_CARD">Bank Card</option>
                      </select>
                    </div>
                  </div>

                  {formData.type !== 'SECURE_NOTE' && (
                    <>
                      <div className="form-group">
                        <label className="form-label">Username / Email</label>
                        <input
                          type="text"
                          className="form-control"
                          placeholder="user@example.com"
                          value={formData.username}
                          onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                        />
                      </div>

                      <div className="form-group">
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                          <label className="form-label" style={{ margin: 0 }}>Password</label>
                          <button
                            type="button"
                            style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontSize: '0.8rem', fontWeight: '600' }}
                            onClick={() => setShowGeneratorModal(true)}
                          >
                            ⚡ Generate Password
                          </button>
                        </div>
                        <input
                          type="text"
                          className="form-control mono"
                          placeholder="Enter or generate password"
                          value={formData.password}
                          onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        />
                      </div>
                    </>
                  )}

                  <div className="form-group">
                    <label className="form-label">Notes (Encrypted)</label>
                    <textarea
                      className="form-control"
                      rows="3"
                      placeholder="Encrypted notes or secret code details..."
                      value={formData.notes}
                      onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowAddModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Save Item</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* EDIT CREDENTIAL MODAL */}
        {showEditModal && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Edit Vault Item</h2>
                <button className="modal-close" onClick={() => setShowEditModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleUpdateCredential}>
                <div className="modal-body">
                  {formError && <div className="badge badge-danger" style={{ display: 'block', width: '100%', marginBottom: '1rem', padding: '0.75rem' }}>{formError}</div>}
                  
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

                  <div style={{ display: 'flex', gap: '1rem' }}>
                    <div className="form-group" style={{ flex: 1 }}>
                      <label className="form-label">Category</label>
                      <select
                        className="form-control"
                        value={formData.category}
                        onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                      >
                        <option value="Work">Work</option>
                        <option value="Personal">Personal</option>
                        <option value="Finance">Finance</option>
                        <option value="Social">Social</option>
                        <option value="Infrastructure">Infrastructure</option>
                      </select>
                    </div>

                    <div className="form-group" style={{ flex: 1 }}>
                      <label className="form-label">Item Type</label>
                      <select
                        className="form-control"
                        value={formData.type}
                        onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                      >
                        <option value="LOGIN">Login</option>
                        <option value="SECURE_NOTE">Secure Note</option>
                        <option value="API_KEY">API Key</option>
                        <option value="BANK_CARD">Bank Card</option>
                      </select>
                    </div>
                  </div>

                  {formData.type !== 'SECURE_NOTE' && (
                    <>
                      <div className="form-group">
                        <label className="form-label">Username / Email</label>
                        <input
                          type="text"
                          className="form-control"
                          value={formData.username}
                          onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                        />
                      </div>

                      <div className="form-group">
                        <label className="form-label">Password</label>
                        <input
                          type="text"
                          className="form-control mono"
                          value={formData.password}
                          onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                        />
                      </div>
                    </>
                  )}

                  <div className="form-group">
                    <label className="form-label">Notes (Encrypted)</label>
                    <textarea
                      className="form-control"
                      rows="3"
                      value={formData.notes}
                      onChange={(e) => setFormData({ ...formData, notes: e.target.value })}
                    />
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowEditModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Update Item</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* SHARE CREDENTIAL MODAL */}
        {showShareModal && selectedCred && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Share "{selectedCred.title}"</h2>
                <button className="modal-close" onClick={() => setShowShareModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleShareSubmit}>
                <div className="modal-body">
                  {shareMessage && (
                    <div className={shareMessage.startsWith('Error') ? 'badge badge-danger' : 'badge badge-success'} style={{ display: 'block', width: '100%', marginBottom: '1rem', padding: '0.75rem' }}>
                      {shareMessage}
                    </div>
                  )}

                  <div className="form-group">
                    <label className="form-label">User Email to Share With *</label>
                    <input
                      type="email"
                      required
                      className="form-control"
                      placeholder="user@example.com"
                      value={shareEmail}
                      onChange={(e) => setShareEmail(e.target.value)}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Permission Level *</label>
                    <select
                      className="form-control"
                      value={sharePermission}
                      onChange={(e) => setSharePermission(e.target.value)}
                    >
                      <option value="VIEW_ONLY">View Only</option>
                      <option value="EDIT_ACCESS">Edit Access</option>
                      <option value="FULL_MANAGEMENT">Full Management</option>
                    </select>
                  </div>
                </div>

                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowShareModal(false)}>Close</button>
                  <button type="submit" className="btn btn-primary">Share Credential</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* PASSWORD GENERATOR MODAL */}
        {showGeneratorModal && (
          <div className="modal-overlay">
            <div className="modal-content" style={{ maxWidth: '640px' }}>
              <div className="modal-header">
                <h2>Password Generator</h2>
                <button className="modal-close" onClick={() => setShowGeneratorModal(false)}><X size={20} /></button>
              </div>
              <div className="modal-body">
                <PasswordGenerator
                  onUsePassword={(genPass) => {
                    setFormData({ ...formData, password: genPass });
                    setShowGeneratorModal(false);
                  }}
                />
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Vault;
