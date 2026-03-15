import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock, ArrowRight } from 'lucide-react';
import authService from '../services/authService';

const Login = ({ initialIsLogin = true }) => {
  const [isLogin, setIsLogin] = useState(initialIsLogin);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (!email || !password) {
      setError('Please fill in all the required fields');
      return;
    }
    
    setLoading(true);
    try {
      if (isLogin) {
        await authService.signin(email, password);
        navigate('/dashboard');
      } else {
        await authService.signup(email, password);
        setSuccess('Account created successfully! Please sign in.');
        setIsLogin(true);
      }
    } catch (err) {
      const resMessage =
        (err.response &&
          err.response.data &&
          err.response.data.message) ||
        err.message ||
        err.toString();
      setError(resMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="y2k-container" style={{ 
      minHeight: '100vh', 
      display: 'flex', 
      flexDirection: 'column', 
      justifyContent: 'center', 
      alignItems: 'center',
      backgroundImage: 'radial-gradient(circle at center, #e0e0e0 1px, transparent 1px)',
      backgroundSize: '24px 24px'
    }}>
      
      <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <h1 className="y2k-header" style={{ textShadow: '4px 4px 0px var(--lima-green)' }}>BudgetBuddy.</h1>
        <p style={{ fontFamily: 'var(--font-mono)', color: 'var(--dark-grey)', marginTop: '0.5rem', fontWeight: 'bold' }}>
          Smart finance for the bold.
        </p>
      </div>

      <div className="y2k-card accent-blue" style={{ width: '100%', maxWidth: '420px', padding: '2.5rem' }}>
        
        <h2 className="y2k-title" style={{ fontSize: '1.75rem', marginBottom: '2rem' }}>
          {isLogin ? 'Welcome Back' : 'Create Account'}
        </h2>

        {error && (
          <div style={{ 
            backgroundColor: '#fee', 
            color: 'var(--error)', 
            padding: '1rem', 
            border: '2px solid var(--error)', 
            borderRadius: '4px',
            marginBottom: '1.5rem', 
            fontSize: '0.875rem',
            fontWeight: '700'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1.5rem' }}>
            <label className="y2k-label">Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--grey)' }} size={20} />
              <input 
                type="email" 
                className="y2k-input" 
                placeholder="you@school.edu" 
                style={{ paddingLeft: '3rem' }}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>
          </div>

          <div style={{ marginBottom: '2rem' }}>
            <label className="y2k-label">Password</label>
            <div style={{ position: 'relative' }}>
              <Lock style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--grey)' }} size={20} />
              <input 
                type="password" 
                className="y2k-input" 
                placeholder="••••••••" 
                style={{ paddingLeft: '3rem' }}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          <button 
            type="submit" 
            className="y2k-btn y2k-btn-primary" 
            style={{ width: '100%', marginBottom: '1.5rem', padding: '1rem' }}
            disabled={loading}
          >
            {loading ? 'Processing...' : (isLogin ? 'Sign In' : 'Create Account')}
            {!loading && <ArrowRight size={20} />}
          </button>
        </form>

        <p style={{ textAlign: 'center', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
          {isLogin ? "Don't have an account? " : "Already have an account? "}
          <button 
            type="button"
            onClick={() => setIsLogin(!isLogin)} 
            style={{ 
              background: 'none', 
              border: 'none', 
              color: 'var(--black)', 
              fontWeight: '900', 
              cursor: 'pointer', 
              textDecoration: 'none',
              borderBottom: '2px solid var(--hot-pink)'
            }}
          >
            {isLogin ? 'Sign up here' : 'Log in here'}
          </button>
        </p>

      </div>
    </div>
  );
};

export default Login;
