import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import Sidebar from '../components/Sidebar';
import { Users, Plus, Shield, UserPlus, Key, Eye, EyeOff, Copy, Check, X } from 'lucide-react';

const TeamVault = () => {
  const [teams, setTeams] = useState([]);
  const [selectedTeam, setSelectedTeam] = useState(null);
  const [teamMembers, setTeamMembers] = useState([]);
  const [teamCredentials, setTeamCredentials] = useState([]);
  const [loading, setLoading] = useState(true);

  // Modals
  const [showCreateTeamModal, setShowCreateTeamModal] = useState(false);
  const [showAddMemberModal, setShowAddMemberModal] = useState(false);
  const [teamNameInput, setTeamNameInput] = useState('');
  const [memberEmailInput, setMemberEmailInput] = useState('');
  const [memberRoleInput, setMemberRoleInput] = useState('MEMBER');
  const [modalError, setModalError] = useState('');

  const [visiblePasswords, setVisiblePasswords] = useState({});
  const [copiedId, setCopiedId] = useState(null);

  const fetchTeams = async () => {
    try {
      const response = await axiosInstance.get('/teams');
      setTeams(response.data);
      if (response.data.length > 0 && !selectedTeam) {
        selectTeam(response.data[0]);
      }
    } catch (error) {
      console.error("Error fetching teams", error);
    } finally {
      setLoading(false);
    }
  };

  const selectTeam = async (team) => {
    setSelectedTeam(team);
    try {
      const [membersRes, vaultRes] = await Promise.all([
        axiosInstance.get(`/teams/${team.id}/members`),
        axiosInstance.get(`/teams/${team.id}/vault`),
      ]);
      setTeamMembers(membersRes.data);
      setTeamCredentials(vaultRes.data);
    } catch (error) {
      console.error("Error fetching team details", error);
    }
  };

  useEffect(() => {
    fetchTeams();
  }, []);

  const handleCreateTeam = async (e) => {
    e.preventDefault();
    setModalError('');
    try {
      const response = await axiosInstance.post('/teams', { teamName: teamNameInput });
      setShowCreateTeamModal(false);
      setTeamNameInput('');
      fetchTeams();
      selectTeam(response.data);
    } catch (error) {
      setModalError(error.response?.data?.message || 'Failed to create team');
    }
  };

  const handleAddMember = async (e) => {
    e.preventDefault();
    setModalError('');
    try {
      await axiosInstance.post(`/teams/${selectedTeam.id}/members`, {
        email: memberEmailInput,
        role: memberRoleInput,
      });
      setShowAddMemberModal(false);
      setMemberEmailInput('');
      selectTeam(selectedTeam);
    } catch (error) {
      setModalError(error.response?.data?.message || 'Failed to add team member');
    }
  };

  const togglePasswordVisibility = (id) => {
    setVisiblePasswords(prev => ({ ...prev, [id]: !prev[id] }));
  };

  const copyToClipboard = (text, id) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header" style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
          <div className="page-title-group">
            <h1 style={{ fontSize: '1.5rem', fontWeight: '800', color: 'var(--text-main)' }}>Team Vault</h1>
            <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginTop: '0.2rem' }}>Collaborative credential vaults & team permission controls</p>
          </div>
          <button className="btn btn-primary" onClick={() => { setTeamNameInput(''); setModalError(''); setShowCreateTeamModal(true); }}>
            <Plus size={18} />
            <span>Create New Team</span>
          </button>
        </div>

        {loading ? (
          <p style={{ color: 'var(--text-muted)' }}>Loading team vaults...</p>
        ) : teams.length === 0 ? (
          <div className="card" style={{ textAlign: 'center', padding: '3rem 1.5rem' }}>
            <Users size={48} color="var(--text-muted)" style={{ margin: '0 auto 1rem auto' }} />
            <h3>No Teams Found</h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', margin: '0.5rem 0 1.5rem 0' }}>
              Create a team to securely share credential vaults with your organization or team members.
            </p>
            <button className="btn btn-primary" onClick={() => { setTeamNameInput(''); setModalError(''); setShowCreateTeamModal(true); }}>
              <Plus size={18} />
              <span>Create New Team</span>
            </button>
          </div>
        ) : (
          <div>
            {/* Team Selection Tabs */}
            <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', overflowX: 'auto', paddingBottom: '0.5rem' }}>
              {teams.map((t) => (
                <button
                  key={t.id}
                  className={`btn ${selectedTeam?.id === t.id ? 'btn-primary' : 'btn-secondary'}`}
                  onClick={() => selectTeam(t)}
                >
                  <Users size={16} />
                  <span>{t.teamName}</span>
                </button>
              ))}
            </div>

            {selectedTeam && (
              <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1.5rem' }}>
                {/* Team Credentials */}
                <div className="card">
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem' }}>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: '700', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <Key size={20} color="var(--primary)" />
                      {selectedTeam.teamName} Credentials
                    </h3>
                  </div>

                  {teamCredentials.length === 0 ? (
                    <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>
                      No credentials associated with this team yet. When adding a credential in My Vault, select this team to share it.
                    </p>
                  ) : (
                    <div className="table-container">
                      <table className="table">
                        <thead>
                          <tr>
                            <th>Title</th>
                            <th>Username</th>
                            <th>Password</th>
                            <th>Owner</th>
                          </tr>
                        </thead>
                        <tbody>
                          {teamCredentials.map((cred) => {
                            const isPasswordVisible = visiblePasswords[cred.id];
                            return (
                              <tr key={cred.id}>
                                <td style={{ fontWeight: '600' }}>{cred.title}</td>
                                <td className="mono">{cred.username}</td>
                                <td>
                                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <span className="mono" style={{ fontWeight: '600', letterSpacing: isPasswordVisible ? '0' : '0.1em' }}>
                                      {isPasswordVisible ? cred.password : '••••••••••••'}
                                    </span>
                                    <button
                                      type="button"
                                      className="toggle-password-btn"
                                      style={{ position: 'static' }}
                                      onClick={() => togglePasswordVisibility(cred.id)}
                                    >
                                      {isPasswordVisible ? <EyeOff size={16} /> : <Eye size={16} />}
                                    </button>
                                    <button
                                      type="button"
                                      className="toggle-password-btn"
                                      style={{ position: 'static' }}
                                      onClick={() => copyToClipboard(cred.password, cred.id)}
                                    >
                                      {copiedId === cred.id ? <Check size={16} color="var(--success)" /> : <Copy size={16} />}
                                    </button>
                                  </div>
                                </td>
                                <td>{cred.owner?.name}</td>
                              </tr>
                            );
                          })}
                        </tbody>
                      </table>
                    </div>
                  )}
                </div>

                {/* Team Members List */}
                <div className="card">
                  <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.25rem' }}>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: '700' }}>Team Members</h3>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => { setMemberEmailInput(''); setModalError(''); setShowAddMemberModal(true); }}
                    >
                      <UserPlus size={14} />
                      <span>Add</span>
                    </button>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                    {teamMembers.map((m) => (
                      <div key={m.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0.85rem 1rem', backgroundColor: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: 'var(--radius-md)' }}>
                        <div>
                          <div style={{ fontWeight: '700', fontSize: '0.9rem', color: 'var(--text-main)' }}>{m.user?.name}</div>
                          <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)' }}>{m.user?.email}</div>
                        </div>
                        <span className={`badge ${m.role === 'OWNER' ? 'badge-success' : 'badge-info'}`}>
                          {m.role}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        )}

        {/* CREATE TEAM MODAL */}
        {showCreateTeamModal && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Create Team Vault</h2>
                <button className="modal-close" onClick={() => setShowCreateTeamModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleCreateTeam}>
                <div className="modal-body">
                  {modalError && <div className="alert-error">{modalError}</div>}
                  <div className="form-group">
                    <label className="form-label">Team Name *</label>
                    <input
                      type="text"
                      required
                      className="form-control"
                      placeholder="e.g. Security Operations, DevOps, Engineering"
                      value={teamNameInput}
                      onChange={(e) => setTeamNameInput(e.target.value)}
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowCreateTeamModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Create Team</button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* ADD MEMBER MODAL */}
        {showAddMemberModal && (
          <div className="modal-overlay">
            <div className="modal-content">
              <div className="modal-header">
                <h2>Add Member to {selectedTeam?.teamName}</h2>
                <button className="modal-close" onClick={() => setShowAddMemberModal(false)}><X size={20} /></button>
              </div>
              <form onSubmit={handleAddMember}>
                <div className="modal-body">
                  {modalError && <div className="alert-error">{modalError}</div>}
                  <div className="form-group">
                    <label className="form-label">User Email *</label>
                    <input
                      type="email"
                      required
                      className="form-control"
                      placeholder="colleague@company.com"
                      value={memberEmailInput}
                      onChange={(e) => setMemberEmailInput(e.target.value)}
                    />
                    <small style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginTop: '0.35rem', display: 'block' }}>
                      Enter another registered user's email. Note: You ({selectedTeam?.owner?.email}) are already a member (OWNER).
                    </small>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Role</label>
                    <select
                      className="form-control"
                      value={memberRoleInput}
                      onChange={(e) => setMemberRoleInput(e.target.value)}
                    >
                      <option value="MEMBER">Member (Can view team credentials)</option>
                      <option value="ADMIN">Admin (Can add members & credentials)</option>
                    </select>
                  </div>
                </div>
                <div className="modal-footer">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowAddMemberModal(false)}>Cancel</button>
                  <button type="submit" className="btn btn-primary">Add Member</button>
                </div>
              </form>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default TeamVault;
