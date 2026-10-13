import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  Cloud, 
  Share2, 
  Star, 
  Upload, 
  Settings, 
  LogOut, 
  Shield, 
  Key, 
  Users, 
  Wand2, 
  Activity,
  Smartphone,
  ShieldAlert
} from 'lucide-react';

const Sidebar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  const getInitials = (name) => {
    if (!name) return 'SV';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().substring(0, 2);
  };

  return (
    <aside className="sidebar">
      {/* Top Profile Header */}
      <div className="sidebar-profile">
        <div className="sidebar-avatar">
          {getInitials(user?.name)}
        </div>
        <div className="sidebar-user-name">{user?.name || 'User Account'}</div>
        <div className="sidebar-user-role">
          <span style={{ 
            fontSize: '0.75rem', 
            padding: '0.15rem 0.4rem', 
            backgroundColor: 'var(--primary-light)', 
            color: 'var(--primary)', 
            borderRadius: '4px',
            fontWeight: '600'
          }}>
            {user?.role || 'USER'}
          </span>
          <span style={{ marginLeft: '0.5rem', color: 'var(--text-muted)' }}>{user?.email}</span>
        </div>
      </div>

      {/* Navigation Links */}
      <nav className="sidebar-nav">
        <NavLink to="/dashboard" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Cloud size={18} />
          <span>My Dashboard</span>
        </NavLink>

        <NavLink to="/vault" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Key size={18} />
          <span>Password Vault</span>
        </NavLink>

        <NavLink to="/shared-with-me" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Share2 size={18} />
          <span>Shared Credentials</span>
        </NavLink>

        <NavLink to="/teams" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Users size={18} />
          <span>Team Vault</span>
        </NavLink>

        <NavLink to="/devices" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Smartphone size={18} />
          <span>My Devices</span>
        </NavLink>

        <NavLink to="/generator" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Wand2 size={18} />
          <span>Generator</span>
        </NavLink>

        <NavLink to="/login-activity" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}>
          <Activity size={18} />
          <span>Login Activity</span>
        </NavLink>

        {user?.role === 'ADMINISTRATOR' && (
          <NavLink to="/admin" className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`} style={{ color: 'var(--danger)' }}>
            <ShieldAlert size={18} />
            <span>Admin Console</span>
          </NavLink>
        )}
      </nav>

      {/* Footer / Logout */}
      <div className="sidebar-footer">
        <button className="nav-item" style={{ background: 'none', border: 'none', width: '100%', cursor: 'pointer' }} onClick={handleLogout}>
          <LogOut size={18} />
          <span>Log out</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
