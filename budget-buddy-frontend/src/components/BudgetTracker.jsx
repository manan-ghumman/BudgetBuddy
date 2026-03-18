import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Plus, Trash2, PiggyBank } from 'lucide-react';
import authService from '../services/authService';
import budgetService from '../services/budgetService';

const CATEGORIES = ["Food", "Fun", "Housing", "Transport", "Other"];

const BudgetTracker = () => {
    const navigate = useNavigate();
    const [budgets, setBudgets] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isAdding, setIsAdding] = useState(false);
    
    // Form State
    const [category, setCategory] = useState(CATEGORIES[0]);
    const [limit, setLimit] = useState('');

    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
            navigate('/login');
            return;
        }
        fetchBudgets();
    }, [navigate]);

    const fetchBudgets = async () => {
        setLoading(true);
        try {
            const res = await budgetService.getUserBudgets();
            setBudgets(res.data);
        } catch (err) {
            console.error("Error fetching budgets", err);
        } finally {
            setLoading(false);
        }
    };

    const handleSaveBudget = async (e) => {
        e.preventDefault();
        try {
            await budgetService.setBudget({
                category,
                monthlyLimit: parseFloat(limit)
            });
            fetchBudgets();
            setIsAdding(false);
            setLimit('');
        } catch (err) {
            console.error("Error saving budget", err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await budgetService.deleteBudget(id);
            fetchBudgets();
        } catch (err) {
            console.error("Error deleting budget", err);
        }
    };

    if (loading && budgets.length === 0) {
        return (
            <div className="y2k-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <h1 className="y2k-header">LOADING...</h1>
            </div>
        );
    }

    return (
        <div className="y2k-container" style={{ padding: '2rem 1rem', maxWidth: '800px' }}>
            
            <button 
              className="y2k-btn y2k-btn-secondary" 
              onClick={() => navigate('/dashboard')} 
              style={{ marginBottom: '2rem', padding: '0.5rem 1rem', fontSize: '0.875rem', boxShadow: '2px 2px 0px var(--black)' }}
            >
              <ChevronLeft size={16} /> Back to Dashboard
            </button>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 className="y2k-header" style={{ fontSize: '2.5rem', margin: 0 }}>Budget Limits</h1>
                <button 
                  className="y2k-btn y2k-btn-primary" 
                  onClick={() => setIsAdding(!isAdding)}
                >
                    <Plus size={20} /> {isAdding ? 'Close' : 'Set New Limit'}
                </button>
            </div>

            {isAdding && (
                <div className="y2k-card accent-blue" style={{ marginBottom: '2rem', padding: '1.5rem' }}>
                    <form onSubmit={handleSaveBudget} style={{ display: 'flex', gap: '1rem', alignItems: 'flex-end', flexWrap: 'wrap' }}>
                        <div style={{ flex: 1 }}>
                            <label className="y2k-label">Category</label>
                            <select className="y2k-input" value={category} onChange={e => setCategory(e.target.value)}>
                                {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                            </select>
                        </div>
                        <div style={{ flex: 1 }}>
                            <label className="y2k-label">Monthly Limit (₹)</label>
                            <input 
                                required 
                                type="number" 
                                step="0.01" 
                                className="y2k-input" 
                                placeholder="0.00" 
                                value={limit} 
                                onChange={e => setLimit(e.target.value)} 
                            />
                        </div>
                        <button type="submit" className="y2k-btn y2k-btn-primary" style={{ padding: '0.875rem 2rem' }}>
                            Save Budget
                        </button>
                    </form>
                </div>
            )}

            <div style={{ display: 'grid', gap: '1.5rem' }}>
                {budgets.length > 0 ? budgets.map((budget) => (
                    <div key={budget.id} className="y2k-card" style={{ padding: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                            <div style={{ 
                                width: '48px', 
                                height: '48px', 
                                backgroundColor: 'var(--off-white)', 
                                border: '2px solid var(--black)', 
                                borderRadius: '8px',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center'
                            }}>
                                <PiggyBank size={24} />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.25rem', fontWeight: '900' }}>{budget.category}</h3>
                                <p style={{ margin: 0, color: 'var(--dark-grey)', fontFamily: 'var(--font-mono)', fontSize: '0.875rem' }}>
                                    Monthly Limit: <strong>₹{budget.monthlyLimit.toFixed(2)}</strong>
                                </p>
                            </div>
                        </div>
                        <button 
                          className="y2k-btn" 
                          onClick={() => handleDelete(budget.id)}
                          style={{ background: 'var(--hot-pink)', borderColor: 'var(--black)', padding: '0.5rem' }}
                        >
                            <Trash2 size={18} color="white" />
                        </button>
                    </div>
                )) : (
                    <div className="y2k-card" style={{ padding: '3rem', textAlign: 'center' }}>
                        <p style={{ color: 'var(--dark-grey)', fontSize: '1.1rem' }}>No budget limits configured.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default BudgetTracker;
