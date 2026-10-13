import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../api/axiosInstance';
import { Shield, Eye, EyeOff, Lock, Mail, KeyRound, CheckCircle } from 'lucide-react';

const GoogleIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path fillRule="evenodd" clipRule="evenodd" d="M23.52 12.273c0-.851-.076-1.67-.218-2.455H12v4.646h6.458a5.52 5.52 0 01-2.395 3.627v3.013h3.878c2.269-2.089 3.579-5.164 3.579-8.831z" fill="#4285F4" />
    <path fillRule="evenodd" clipRule="evenodd" d="M12 24c3.24 0 5.956-1.074 7.942-2.908l-3.878-3.013c-1.076.721-2.451 1.147-4.064 1.147-3.125 0-5.772-2.112-6.717-4.952H1.287v3.11A11.996 11.996 0 0012 24z" fill="#34A853" />
    <path fillRule="evenodd" clipRule="evenodd" d="M5.283 14.274a7.195 7.195 0 010-4.548v-3.11H1.287a11.996 11.996 0 000 10.768l3.996-3.11z" fill="#FBBC05" />
    <path fillRule="evenodd" clipRule="evenodd" d="M12 4.75c1.762 0 3.345.606 4.588 1.794l3.441-3.441C17.95 1.19 15.234 0 12 0 7.392 0 3.42 2.649 1.287 6.616l3.996 3.11C6.228 6.862 8.875 4.75 12 4.75z" fill="#EA4335" />
  </svg>
);

const GithubIcon = () => (
  <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor" xmlns="http://www.w3.org/2000/svg">
    <path fillRule="evenodd" clipRule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.53 1.032 1.53 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.113-4.555-4.951 0-1.093.39-1.988 1.029-2.688-.103-.253-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.026A9.564 9.564 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.7 1.028 1.595 1.028 2.688 0 3.848-2.339 4.695-4.566 4.943.359.309.678.92.678 1.855 0 1.338-.012 2.419-.012 2.747 0 .268.18.58.688.482A10.019 10.019 0 0022 12.017C22 6.484 17.522 2 12 2z" />
  </svg>
);

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  // MFA state
  const [mfaChallengeToken, setMfaChallengeToken] = useState(null);
  const [mfaCode, setMfaCode] = useState('');

  // Password Recovery state
  const [showForgotModal, setShowForgotModal] = useState(false);
  const [forgotEmail, setForgotEmail] = useState('');
  const [resetToken, setResetToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [recoveryStep, setRecoveryStep] = useState(1); // 1: Email, 2: Reset

  const { login, verifyMfa, oauthLogin } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const result = await login(email, password);
      if (result.mfaRequired) {
        setMfaChallengeToken(result.mfaToken);
      } else {
        navigate('/dashboard');
      }
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Failed to log in. Please check your credentials.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleMfaSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await verifyMfa(mfaChallengeToken, mfaCode);
      navigate('/dashboard');
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Invalid MFA code. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleOAuthLogin = async (provider) => {
    setError('');
    setLoading(true);
    try {
      const mockOAuthId = 'oauth-' + Math.floor(Math.random() * 100000);
      const mockEmail = provider === 'google' ? 'user.google@gmail.com' : 'user.github@github.com';
      const mockName = provider === 'google' ? 'Google User' : 'GitHub User';

      await oauthLogin(provider, mockOAuthId, mockEmail, mockName);
      navigate('/dashboard');
    } catch (err) {
      setError('OAuth2 login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleForgotPassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    try {
      const res = await axiosInstance.post('/auth/forgot-password', { email: forgotEmail });
      setSuccess('Recovery token generated: ' + (res.data.token || 'Check server logs'));
      setResetToken(res.data.token || '');
      setRecoveryStep(2);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to initiate password recovery.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    if (newPassword !== confirmPassword) {
      setError('New passwords do not match');
      return;
    }
    setLoading(true);

    try {
      await axiosInstance.post('/auth/reset-password', {
        token: resetToken,
        newPassword,
        confirmPassword
      });
      setSuccess('Password successfully reset! You can now sign in.');
      setShowForgotModal(false);
      setRecoveryStep(1);
    } catch (err) {
      setError(err.response?.data?.message || 'Password reset failed.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <div className="auth-header">
          <div className="brand-icon">
            <Shield size={28} />
          </div>
          <h1>Welcome to SecureVault</h1>
          <p>Cybersecurity credential & password manager</p>
        </div>

        {error && <div className="alert-error">{error}</div>}
        {success && <div style={{ padding: '0.75rem', backgroundColor: 'var(--success-light)', color: 'var(--success)', borderRadius: 'var(--radius-md)', marginBottom: '1rem', fontSize: '0.85rem' }}>{success}</div>}

        {/* MFA Challenge Screen */}
        {mfaChallengeToken ? (
          <form onSubmit={handleMfaSubmit}>
            <div style={{ textAlign: 'center', marginBottom: '1.25rem' }}>
              <KeyRound size={40} style={{ color: 'var(--primary)', marginBottom: '0.5rem' }} />
              <h3 style={{ margin: 0, fontSize: '1.1rem' }}>MFA Verification Required</h3>
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Enter your 6-digit authentication code (Default: <code>123456</code>)
              </p>
            </div>

            <div className="form-group">
              <label className="form-label">Authentication Code</label>
              <input
                type="text"
                required
                maxLength={6}
                className="form-control"
                placeholder="123456"
                style={{ textAlign: 'center', letterSpacing: '0.5rem', fontSize: '1.25rem', fontWeight: 'bold' }}
                value={mfaCode}
                onChange={(e) => setMfaCode(e.target.value)}
              />
            </div>

            <button type="submit" disabled={loading} className="btn btn-primary" style={{ width: '100%', marginTop: '0.5rem' }}>
              {loading ? 'Verifying...' : 'Verify Code & Sign In'}
            </button>
            <button
              type="button"
              className="btn btn-secondary"
              style={{ width: '100%', marginTop: '0.5rem' }}
              onClick={() => setMfaChallengeToken(null)}
            >
              Back to Login
            </button>
          </form>
        ) : (
          /* Standard Login Form */
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label className="form-label">Email Address</label>
              <div className="input-password-wrapper">
                <input
                  type="email"
                  required
                  className="form-control"
                  placeholder="name@company.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div className="form-group">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <label className="form-label">Password</label>
                <button
                  type="button"
                  onClick={() => setShowForgotModal(true)}
                  style={{ background: 'none', border: 'none', color: 'var(--primary)', fontSize: '0.8rem', cursor: 'pointer', padding: 0 }}
                >
                  Forgot password?
                </button>
              </div>
              <div className="input-password-wrapper">
                <input
                  type={showPassword ? 'text' : 'password'}
                  required
                  className="form-control"
                  placeholder="••••••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  className="toggle-password-btn"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '0.5rem' }}
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </button>

            {/* OAuth2 Login Option */}
            <div style={{ margin: '1.25rem 0', textAlign: 'center', position: 'relative' }}>
              <div style={{ borderBottom: '1px solid var(--border-color)', position: 'absolute', top: '50%', width: '100%' }}></div>
              <span style={{ position: 'relative', backgroundColor: 'var(--bg-card)', padding: '0 0.5rem', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                OR CONTINUE WITH
              </span>
            </div>

            <div style={{ display: 'flex', gap: '0.75rem', flexDirection: 'column' }}>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.6rem', fontSize: '0.85rem' }}
                onClick={() => handleOAuthLogin('google')}
              >
                <GoogleIcon /> Continue with Google
              </button>
              <button
                type="button"
                className="btn btn-secondary"
                style={{ width: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.6rem', fontSize: '0.85rem' }}
                onClick={() => handleOAuthLogin('github')}
              >
                <GithubIcon /> Continue with GitHub
              </button>
            </div>
          </form>
        )}

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
          Don't have an account?{' '}
          <Link to="/register" style={{ color: 'var(--primary)', fontWeight: '600' }}>
            Register now
          </Link>
        </div>
      </div>

      {/* Forgot Password Modal */}
      {showForgotModal && (
        <div className="modal-backdrop" onClick={() => setShowForgotModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '420px' }}>
            <h2 className="modal-title">Password Recovery</h2>

            {recoveryStep === 1 ? (
              <form onSubmit={handleForgotPassword}>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginBottom: '1rem' }}>
                  Enter your registered account email to receive a recovery token.
                </p>
                <div className="form-group">
                  <label className="form-label">Account Email</label>
                  <input
                    type="email"
                    required
                    className="form-control"
                    placeholder="user@securevault.com"
                    value={forgotEmail}
                    onChange={(e) => setForgotEmail(e.target.value)}
                  />
                </div>
                <div className="modal-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setShowForgotModal(false)}>
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    Send Recovery Token
                  </button>
                </div>
              </form>
            ) : (
              <form onSubmit={handleResetPassword}>
                <div className="form-group">
                  <label className="form-label">Recovery Token</label>
                  <input
                    type="text"
                    required
                    className="form-control"
                    placeholder="Paste recovery token"
                    value={resetToken}
                    onChange={(e) => setResetToken(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">New Password</label>
                  <input
                    type="password"
                    required
                    className="form-control"
                    placeholder="New password (min 8 chars)"
                    value={newPassword}
                    onChange={(e) => setNewPassword(e.target.value)}
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Confirm New Password</label>
                  <input
                    type="password"
                    required
                    className="form-control"
                    placeholder="Confirm new password"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                  />
                </div>
                <div className="modal-actions">
                  <button type="button" className="btn btn-secondary" onClick={() => setRecoveryStep(1)}>
                    Back
                  </button>
                  <button type="submit" className="btn btn-primary" disabled={loading}>
                    Reset Password
                  </button>
                </div>
              </form>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
