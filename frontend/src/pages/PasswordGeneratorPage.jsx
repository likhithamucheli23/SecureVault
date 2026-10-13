import React from 'react';
import Sidebar from '../components/Sidebar';
import PasswordGenerator from '../components/PasswordGenerator';

const PasswordGeneratorPage = () => {
  return (
    <div className="app-container">
      <Sidebar />

      <main className="main-content">
        <div className="page-header">
          <div className="page-title-group">
            <h1>Password Generator & Analyzer</h1>
            <p>Generate cryptographically strong passwords and evaluate security strength</p>
          </div>
        </div>

        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '1rem' }}>
          <PasswordGenerator />
        </div>
      </main>
    </div>
  );
};

export default PasswordGeneratorPage;
