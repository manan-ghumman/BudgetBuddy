import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Plus, Trash2, Edit, ChevronLeft, Calendar, Check, X } from 'lucide-react';
import authService from '../services/authService';
import expenseService from '../services/expenseService';

const CATEGORIES = ["All", "Food", "Fun", "Housing", "Transport", "Other"];

const ExpenseTracker = () => {
    const navigate = useNavigate();
    const [expenses, setExpenses] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [categoryFilter, setCategoryFilter] = useState('All');
    const [isAdding, setIsAdding] = useState(false);
    const [loading, setLoading] = useState(true);
    const [editingId, setEditingId] = useState(null);
    
    // Form States
    const [newName, setNewName] = useState('');
    const [newAmount, setNewAmount] = useState('');
    const [newCategory, setNewCategory] = useState('Food');
    const [newDate, setNewDate] = useState('');

    // Edit States
    const [editName, setEditName] = useState('');
    const [editAmount, setEditAmount] = useState('');
    const [editCategory, setEditCategory] = useState('');
    const [editDate, setEditDate] = useState('');

    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
            navigate('/login');
            return;
        }
        fetchExpenses();
    }, [navigate]);

    const fetchExpenses = async () => {
        setLoading(true);
        try {
            const res = await expenseService.getAllExpenses();
            setExpenses(res.data);
        } catch (err) {
            console.error("Error fetching expenses", err);
        } finally {
            setLoading(false);
        }
    };

    const handleAddExpense = async (e) => {
        e.preventDefault();
        const expenseData = {
            description: newName,
            amount: parseFloat(newAmount),
            category: newCategory,
            date: newDate || new Date().toISOString().split('T')[0]
        };

        try {
            await expenseService.addExpense(expenseData);
            fetchExpenses();
            setIsAdding(false);
            setNewName('');
            setNewAmount('');
            setNewDate('');
        } catch (err) {
            console.error("Error adding expense", err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await expenseService.deleteExpense(id);
            fetchExpenses();
        } catch (err) {
            console.error("Error deleting expense", err);
        }
    };

    const startEditing = (expense) => {
        setEditingId(expense.id);
        setEditName(expense.description);
        setEditAmount(expense.amount.toString());
        setEditCategory(expense.category);
        setEditDate(expense.date);
    };

    const cancelEditing = () => {
        setEditingId(null);
    };

    const handleUpdateExpense = async (id) => {
        const expenseData = {
            description: editName,
            amount: parseFloat(editAmount),
            category: editCategory,
            date: editDate
        };

        try {
            await expenseService.updateExpense(id, expenseData);
            setEditingId(null);
            fetchExpenses();
        } catch (err) {
            console.error("Error updating expense", err);
        }
    };

    const filteredExpenses = expenses.filter(e => {
        const matchesSearch = e.description.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = categoryFilter === 'All' || e.category === categoryFilter;
        return matchesSearch && matchesCategory;
    });

    const getCategoryBadgeColor = (category) => {
        switch(category) {
            case 'Food': return 'var(--lima-green)';
            case 'Fun': return 'var(--hot-pink)';
            case 'Housing': return 'var(--electric-blue)';
            default: return 'var(--light-grey)';
        }
    };

    if (loading && expenses.length === 0) {
        return (
            <div className="y2k-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <h1 className="y2k-header">LOADING...</h1>
            </div>
        );
    }

    return (
        <div className="y2k-container" style={{ padding: '2rem 1rem', maxWidth: '1000px' }}>
            
            <button 
              className="y2k-btn y2k-btn-secondary" 
              onClick={() => navigate('/dashboard')} 
              style={{ marginBottom: '2rem', padding: '0.5rem 1rem', fontSize: '0.875rem', boxShadow: '2px 2px 0px var(--black)' }}
            >
              <ChevronLeft size={16} /> Back to Dashboard
            </button>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                <h1 className="y2k-header" style={{ fontSize: '2rem', margin: 0 }}>Expenses Tracker</h1>
            </div>

            {/* Action Bar */}
            <div className="y2k-card" style={{ marginBottom: '2rem', padding: '1rem 1.5rem' }}>
                <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap', alignItems: 'center' }}>
                    <div style={{ flex: '1 1 250px', position: 'relative' }}>
                        <Search style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--grey)' }} size={20} />
                        <input 
                            type="text" 
                            className="y2k-input" 
                            placeholder="Search transactions..." 
                            style={{ paddingLeft: '3rem', width: '100%' }}
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    
                    <select 
                        className="y2k-input" 
                        style={{ flex: '0 0 200px', cursor: 'pointer', appearance: 'none' }}
                        value={categoryFilter}
                        onChange={(e) => setCategoryFilter(e.target.value)}
                    >
                        {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
                    </select>

                    <button 
                      className="y2k-btn y2k-btn-primary" 
                      style={{ padding: '0.875rem 1.5rem', whiteSpace: 'nowrap' }} 
                      onClick={() => setIsAdding(!isAdding)}
                    >
                        <Plus size={20} /> {isAdding ? 'Close Form' : 'New Expense'}
                    </button>
                </div>
            </div>

            {/* Add Expense Form */}
            {isAdding && (
                <div className="y2k-card accent-green" style={{ marginBottom: '2rem', padding: '2rem' }}>
                    <h2 className="y2k-title" style={{ marginBottom: '1.5rem', fontSize: '1.25rem' }}>Log New Expense</h2>
                    
                    <form onSubmit={handleAddExpense} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
                        <div>
                            <label className="y2k-label">Description</label>
                            <input required type="text" className="y2k-input" placeholder="e.g. Cinema tickets" value={newName} onChange={e => setNewName(e.target.value)} />
                        </div>
                        <div>
                            <label className="y2k-label">Amount ($)</label>
                            <input required type="number" step="0.01" className="y2k-input" placeholder="0.00" value={newAmount} onChange={e => setNewAmount(e.target.value)} />
                        </div>
                        <div>
                            <label className="y2k-label">Category</label>
                            <select className="y2k-input" value={newCategory} onChange={e => setNewCategory(e.target.value)}>
                                {CATEGORIES.filter(c => c !== 'All').map(c => <option key={c} value={c}>{c}</option>)}
                            </select>
                        </div>
                        <div>
                            <label className="y2k-label">Date</label>
                            <input required type="date" className="y2k-input" value={newDate} onChange={e => setNewDate(e.target.value)} />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                            <button type="submit" className="y2k-btn y2k-btn-primary" style={{ width: '100%', padding: '0.875rem' }}>
                                Save Transaction
                            </button>
                        </div>
                    </form>
                </div>
            )}

            {/* Expense Table */}
            <div className="y2k-table-container">
                <table className="y2k-table">
                    <thead>
                        <tr>
                            <th style={{ width: '20%' }}>Date</th>
                            <th style={{ width: '35%' }}>Description</th>
                            <th style={{ width: '15%' }}>Category</th>
                            <th style={{ width: '15%' }}>Amount</th>
                            <th style={{ width: '15%', textAlign: 'right' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {filteredExpenses.map((expense) => (
                            <tr key={expense.id}>
                                {editingId === expense.id ? (
                                    <>
                                        <td>
                                            <input type="date" className="y2k-input" value={editDate} onChange={e => setEditDate(e.target.value)} style={{ padding: '0.5rem', fontSize: '0.8rem' }} />
                                        </td>
                                        <td>
                                            <input type="text" className="y2k-input" value={editName} onChange={e => setEditName(e.target.value)} style={{ padding: '0.5rem', fontSize: '0.8rem' }} />
                                        </td>
                                        <td>
                                            <select className="y2k-input" value={editCategory} onChange={e => setEditCategory(e.target.value)} style={{ padding: '0.5rem', fontSize: '0.8rem' }}>
                                                {CATEGORIES.filter(c => c !== 'All').map(c => <option key={c} value={c}>{c}</option>)}
                                            </select>
                                        </td>
                                        <td>
                                            <input type="number" step="0.01" className="y2k-input" value={editAmount} onChange={e => setEditAmount(e.target.value)} style={{ padding: '0.5rem', fontSize: '0.8rem' }} />
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                                                <button className="y2k-btn" onClick={() => handleUpdateExpense(expense.id)} style={{ background: 'var(--lima-green)', padding: '0.4rem' }}>
                                                    <Check size={16} color="white" />
                                                </button>
                                                <button className="y2k-btn" onClick={cancelEditing} style={{ background: 'var(--grey)', padding: '0.4rem' }}>
                                                    <X size={16} color="white" />
                                                </button>
                                            </div>
                                        </td>
                                    </>
                                ) : (
                                    <>
                                        <td style={{ color: 'var(--dark-grey)', fontSize: '0.875rem', fontWeight: 'bold' }}>
                                            {new Date(expense.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}
                                        </td>
                                        <td style={{ fontWeight: '800', fontSize: '1rem' }}>{expense.description}</td>
                                        <td>
                                            <span className="y2k-badge" style={{ backgroundColor: getCategoryBadgeColor(expense.category) }}>
                                                {expense.category}
                                            </span>
                                        </td>
                                        <td style={{ fontWeight: '900', fontSize: '1rem' }}>${expense.amount.toFixed(2)}</td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                                                <button className="y2k-btn y2k-btn-secondary" onClick={() => startEditing(expense)} style={{ padding: '0.5rem', boxShadow: 'none' }}>
                                                    <Edit size={16} color='var(--dark-grey)' />
                                                </button>
                                                <button className="y2k-btn" onClick={() => handleDelete(expense.id)} style={{ padding: '0.5rem', boxShadow: 'none', background: 'var(--hot-pink)', borderColor: 'var(--hot-pink)' }}>
                                                    <Trash2 size={16} color="white" />
                                                </button>
                                            </div>
                                        </td>
                                    </>
                                )}
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default ExpenseTracker;
