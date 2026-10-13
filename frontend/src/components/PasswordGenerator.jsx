import React, { useState, useEffect } from 'react';
import axiosInstance from '../api/axiosInstance';
import { RefreshCw, Copy, Check, ShieldCheck, ShieldAlert } from 'lucide-react';

const PasswordGenerator = ({ onUsePassword }) => {
  const [length, setLength] = useState(16);
  const [includeUppercase, setIncludeUppercase] = useState(true);
  const [includeLowercase, setIncludeLowercase] = useState(true);
  const [includeNumbers, setIncludeNumbers] = useState(true);
  const [includeSpecial, setIncludeSpecial] = useState(true);
  
  const [password, setPassword] = useState('');
  const [strengthInfo, setStrengthInfo] = useState({ strength: 'STRONG', score: 90, suggestions: [] });
  const [copied, setCopied] = useState(false);

  const generatePassword = async () => {
    try {
      const response = await axiosInstance.post('/password/generate', {
        length: Number(length),
        includeUppercase,
        includeLowercase,
        includeNumbers,
        includeSpecial,
      });
      const genPass = response.data.password;
      setPassword(genPass);
      analyzePassword(genPass);
    } catch (error) {
      console.error("Error generating password", error);
    }
  };

  const analyzePassword = async (passToAnalyze) => {
    try {
      const response = await axiosInstance.post('/password/analyze', { password: passToAnalyze });
      setStrengthInfo(response.data);
    } catch (error) {
      console.error("Error analyzing password", error);
    }
  };

  useEffect(() => {
    generatePassword();
  }, [length, includeUppercase, includeLowercase, includeNumbers, includeSpecial]);

  const handlePasswordChange = (newVal) => {
    setPassword(newVal);
    analyzePassword(newVal);
  };

  const handleCopy = () => {
    if (!password) return;
    navigator.clipboard.writeText(password);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const getBadgeClass = (str) => {
    if (str === 'STRONG') return 'badge-success';
    if (str === 'MEDIUM') return 'badge-warning';
    return 'badge-danger';
  };

  return (
    <div className="card" style={{ maxWidth: '600px' }}>
      <h3 style={{ fontSize: '1.1rem', fontWeight: '700', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        <ShieldCheck size={20} color="var(--primary)" />
        Password Generator & Analyzer
      </h3>

      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.25rem' }}>
        <input
          type="text"
          value={password}
          onChange={(e) => handlePasswordChange(e.target.value)}
          placeholder="Type or generate a password..."
          className="form-control mono"
          style={{ fontSize: '1.1rem', letterSpacing: '0.05em', fontWeight: '600' }}
        />
        <button type="button" className="btn btn-secondary" onClick={generatePassword} title="Regenerate">
          <RefreshCw size={18} />
        </button>
        <button type="button" className="btn btn-secondary" onClick={handleCopy} title="Copy to clipboard">
          {copied ? <Check size={18} color="var(--success)" /> : <Copy size={18} />}
        </button>
      </div>

      <div style={{ marginBottom: '1.25rem', padding: '0.85rem 1rem', backgroundColor: '#f8fafc', border: '1px solid #e2e8f0', borderRadius: 'var(--radius-md)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
          <span style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-muted)' }}>Strength Rating</span>
          <span className={`badge ${getBadgeClass(strengthInfo.strength)}`}>{strengthInfo.strength}</span>
        </div>
        <div style={{ height: '6px', backgroundColor: '#e2e8f0', borderRadius: '3px', overflow: 'hidden' }}>
          <div
            style={{
              height: '100%',
              width: `${strengthInfo.score}%`,
              backgroundColor: strengthInfo.strength === 'STRONG' ? 'var(--success)' : strengthInfo.strength === 'MEDIUM' ? 'var(--warning)' : 'var(--danger)',
              transition: 'all 0.3s ease',
            }}
          />
        </div>
      </div>

      {strengthInfo.suggestions && strengthInfo.suggestions.length > 0 && (
        <div style={{ marginBottom: '1.5rem', fontSize: '0.825rem', color: 'var(--text-muted)' }}>
          <div style={{ fontWeight: '600', marginBottom: '0.35rem' }}>Suggestions:</div>
          <ul style={{ listStyleType: 'none', paddingLeft: 0, display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            {strengthInfo.suggestions.map((sug, idx) => (
              <li key={idx} style={{ color: sug.startsWith('✓') ? 'var(--success)' : 'var(--text-muted)' }}>
                {sug}
              </li>
            ))}
          </ul>
        </div>
      )}

      <div className="form-group">
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
          <label className="form-label" style={{ margin: 0 }}>Length: {length}</label>
        </div>
        <input
          type="range"
          min="8"
          max="48"
          value={length}
          onChange={(e) => setLength(e.target.value)}
          style={{ width: '100%', accentColor: 'var(--primary)', cursor: 'pointer' }}
        />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem', marginBottom: '1.5rem' }}>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', cursor: 'pointer' }}>
          <input
            type="checkbox"
            checked={includeUppercase}
            onChange={(e) => setIncludeUppercase(e.target.checked)}
          />
          Uppercase (A-Z)
        </label>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', cursor: 'pointer' }}>
          <input
            type="checkbox"
            checked={includeLowercase}
            onChange={(e) => setIncludeLowercase(e.target.checked)}
          />
          Lowercase (a-z)
        </label>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', cursor: 'pointer' }}>
          <input
            type="checkbox"
            checked={includeNumbers}
            onChange={(e) => setIncludeNumbers(e.target.checked)}
          />
          Numbers (0-9)
        </label>
        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.85rem', cursor: 'pointer' }}>
          <input
            type="checkbox"
            checked={includeSpecial}
            onChange={(e) => setIncludeSpecial(e.target.checked)}
          />
          Symbols (!@#$)
        </label>
      </div>

      {onUsePassword && (
        <button
          type="button"
          className="btn btn-primary"
          style={{ width: '100%' }}
          onClick={() => onUsePassword(password)}
        >
          Use Password
        </button>
      )}
    </div>
  );
};

export default PasswordGenerator;
