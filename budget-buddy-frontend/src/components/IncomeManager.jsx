import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { ChevronLeft, Plus, Trash2, IndianRupee, Calendar } from 'lucide-react';
import authService from '../services/authService';
import incomeService from '../services/incomeService';

const IncomeManager = () => {
    const navigate = useNavigate();
    const [incomes, setIncomes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [isAdding, setIsAdding] = useState(false);
    
    // Form State
    const [source, setSource] = useState('');
    const [amount, setAmount] = useState('');
    const [date, setDate] = useState(new Date().toISOString().split('T')[0]);

    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
            navigate('/login');
            return;
        }
        fetchIncomes();
    }, [navigate]);

    const fetchIncomes = async () => {
        setLoading(true);
        try {
            const res = await incomeService.getAllIncomes();
            setIncomes(res.data);
        } catch (err) {
            console.error("Error fetching incomes", err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddIncome = async (e) => {
        e.preventDefault();
        try {
            await incomeService.addIncome({
                source,
                amount: parseFloat(amount),
                date
            });
            fetchIncomes();
            setIsAdding(false);
            setSource('');
            setAmount('');
        } catch (err) {
            console.error("Error adding income", err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await incomeService.deleteIncome(id);
            fetchIncomes();
        } catch (err) {
            console.error("Error deleting income", err);
        }
    };

    if (loading && incomes.length === 0) {
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
                <h1 className="y2k-header" style={{ fontSize: '2.5rem', margin: 0 }}>Income Manager</h1>
                <button 
                  className="y2k-btn y2k-btn-primary" 
                  onClick={() => setIsAdding(!isAdding)}
                  style={{ backgroundColor: 'var(--lima-green)' }}
                >
                    <Plus size={20} /> {isAdding ? 'Close' : 'Log New Income'}
                </button>
            </div>

            {isAdding && (
                <div className="y2k-card accent-green" style={{ marginBottom: '2rem', padding: '2rem' }}>
                    <form onSubmit={handleAddIncome} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
                        <div>
                            <label className="y2k-label">Source / Label</label>
                            <input 
                                required 
                                type="text" 
                                className="y2k-input" 
                                placeholder="e.g. Monthly Salary, Freelance" 
                                value={source} 
                                onChange={e => setSource(e.target.value)} 
                            />
                        </div>
                        <div>
                            <label className="y2k-label">Amount (₹)</label>
                            <input 
                                required 
                                type="number" 
                                step="0.01" 
                                className="y2k-input" 
                                placeholder="0.00" 
                                value={amount} 
                                onChange={e => setAmount(e.target.value)} 
                            />
                        </div>
                        <div>
                            <label className="y2k-label">Date</label>
                            <div style={{ position: 'relative' }}>
                                <Calendar style={{ position: 'absolute', right: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--grey)', pointerEvents: 'none' }} size={18} />
                                <input required type="date" className="y2k-input" value={date} onChange={e => setDate(e.target.value)} />
                            </div>
                        </div>
                        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                            <button type="submit" className="y2k-btn y2k-btn-primary" style={{ width: '100%', padding: '0.875rem' }}>
                                Save Income
                            </button>
                        </div>
                    </form>
                </div>
            )}

            <div style={{ display: 'grid', gap: '1.25rem' }}>
                {incomes.length > 0 ? incomes.map((income) => (
                    <div key={income.id} className="y2k-card" style={{ padding: '1.25rem 1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
                            <div style={{ 
                                width: '42px', 
                                height: '42px', 
                                backgroundColor: 'var(--off-white)', 
                                border: '2px solid var(--black)', 
                                borderRadius: '8px',
                                display: 'flex',
                                justifyContent: 'center',
                                alignItems: 'center'
                            }}>
                                <IndianRupee size={20} color="var(--lima-green)" />
                            </div>
                            <div>
                                <h3 style={{ margin: 0, fontSize: '1.15rem', fontWeight: '900' }}>{income.source}</h3>
                                <p style={{ margin: 0, color: 'var(--dark-grey)', fontFamily: 'var(--font-mono)', fontSize: '0.8rem' }}>
                                    {new Date(income.date).toLocaleDateString()} — <strong style={{color: 'var(--black)'}}>₹{income.amount.toFixed(2)}</strong>
                                </p>
                            </div>
                        </div>
                        <button 
                          className="y2k-btn" 
                          onClick={() => handleDelete(income.id)}
                          style={{ background: 'var(--off-white)', borderColor: 'var(--black)', padding: '0.5rem' }}
                        >
                            <Trash2 size={16} color="var(--error)" />
                        </button>
                    </div>
                )) : (
                    <div className="y2k-card" style={{ padding: '4rem', textAlign: 'center' }}>
                        <p style={{ color: 'var(--dark-grey)', fontSize: '1.1rem', fontWeight: 'bold' }}>No income sources logged yet.</p>
                        <p style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>Add your salary or other earnings to calculate your balance.</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default IncomeManager;
