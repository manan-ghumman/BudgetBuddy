import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { PlusCircle, LogOut, CreditCard, ArrowUpRight, ArrowDownRight, PiggyBank } from 'lucide-react';
import { ResponsiveContainer, BarChart, CartesianGrid, XAxis, YAxis, Tooltip, Bar, PieChart, Pie, Cell } from 'recharts';
import authService from '../services/authService';
import dashboardService from '../services/dashboardService';
import expenseService from '../services/expenseService';
import budgetService from '../services/budgetService';
import incomeService from '../services/incomeService';

const CATEGORY_COLORS = {
    'Food': 'var(--lima-green)',
    'Fun': 'var(--hot-pink)',
    'Housing': 'var(--electric-blue)',
    'Transport': 'var(--neon-orange)',
    'Other': 'var(--neon-purple)'
};

const PIE_COLORS = Object.values(CATEGORY_COLORS);

const Dashboard = () => {
    const navigate = useNavigate();
    const [stats, setStats] = useState(null);
    const [recentExpenses, setRecentExpenses] = useState([]);
    const [budgets, setBudgets] = useState([]);
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const currentUser = authService.getCurrentUser();
        if (!currentUser) {
            navigate('/login');
            return;
        }
        setUser(currentUser);

        const fetchData = async () => {
            try {
                const statsRes = await dashboardService.getStats();
                setStats(statsRes.data);

                const [expensesRes, incomesRes] = await Promise.all([
                    expenseService.getAllExpenses(),
                    incomeService.getAllIncomes()
                ]);

                // Merge and sort
                const combined = [
                    ...expensesRes.data.map(e => ({ ...e, type: 'expense' })),
                    ...incomesRes.data.map(i => ({ ...i, type: 'income', description: i.source }))
                ].sort((a, b) => new Date(b.date) - new Date(a.date));

                setRecentExpenses(combined.slice(0, 5));

                const budgetsRes = await budgetService.getUserBudgets();
                setBudgets(budgetsRes.data);
            } catch (err) {
                console.error("Error fetching dashboard data", err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [navigate]);

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    if (loading) {
        return (
            <div className="y2k-container" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
                <h1 className="y2k-header">LOADING...</h1>
            </div>
        );
    }

    const pieData = stats?.spendingByCategory ? 
        Object.entries(stats.spendingByCategory).map(([name, value]) => ({ name, value })) : [];

    return (
        <div className="y2k-container" style={{ padding: '2rem 1rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2.5rem' }}>
                <div>
                  <h1 className="y2k-header" style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>Dashboard Overview</h1>
                  <p style={{ color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>Welcome back, {user?.email.split('@')[0]}.</p>
                </div>
                <div style={{ display: 'flex', gap: '1rem' }}>
                    <button className="y2k-btn y2k-btn-primary" onClick={() => navigate('/tracker')}>
                      <PlusCircle size={20} />
                      Track Expense
                    </button>
                    <button className="y2k-btn y2k-btn-primary" onClick={() => navigate('/budget')} style={{ background: 'var(--electric-blue)' }}>
                      <PiggyBank size={20} />
                      Set Budget
                    </button>
                    <button className="y2k-btn y2k-btn-primary" onClick={() => navigate('/income')} style={{ background: 'var(--lima-green)' }}>
                      <PlusCircle size={20} />
                      Add Income
                    </button>
                    <button className="y2k-btn y2k-btn-secondary" onClick={handleLogout}>
                      <LogOut size={20} />
                      Logout
                    </button>
                </div>
            </div>

            {/* Stats Row */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '2rem', marginBottom: '3rem' }}>
                <div className="y2k-card accent-green">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                            <p className="y2k-label">Current Balance</p>
                            <div style={{ fontSize: '2.5rem', fontWeight: '900', color: 'var(--black)' }}>
                                ₹{((stats?.totalMonthlyIncome || 0) - (stats?.totalMonthlySpending || 0)).toFixed(2)}
                            </div>
                        </div>
                        <CreditCard color="var(--dark-grey)" size={28} />
                    </div>
                </div>
                <div className="y2k-card accent-blue">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                            <p className="y2k-label">Total Income</p>
                            <div style={{ fontSize: '2rem', fontWeight: '900', color: 'var(--black)', display: 'flex', alignItems: 'center' }}>
                              ₹{(stats?.totalMonthlyIncome || 0).toFixed(2)} <ArrowUpRight color="var(--lima-green)" size={24} style={{ marginLeft: '8px' }}/>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="y2k-card accent-pink">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                            <p className="y2k-label">Total Spent</p>
                            <div style={{ fontSize: '2rem', fontWeight: '900', color: 'var(--black)', display: 'flex', alignItems: 'center' }}>
                              ₹{(stats?.totalMonthlySpending || 0).toFixed(2)} <ArrowDownRight color="var(--hot-pink)" size={24} style={{ marginLeft: '8px' }}/>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="y2k-card accent-black">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                        <div>
                            <p className="y2k-label" style={{ color: 'var(--grey)' }}>Number of Expenses</p>
                            <div style={{ fontSize: '2rem', fontWeight: '900', color: 'var(--white)' }}>
                                {stats?.totalExpensesCount || '0'}
                            </div>
                        </div>
                        <PiggyBank color="var(--white)" size={28} />
                    </div>
                </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1.5fr 1fr', gap: '2rem', marginBottom: '3rem' }}>
                {/* Bar Chart Section - Using simplified data for now */}
                <div className="y2k-card">
                    <div className="y2k-title">
                        Income vs Expenses
                    </div>
                    <div style={{ height: '350px', marginTop: '1rem', paddingRight: '20px' }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <BarChart data={[{ name: 'Current Month', Income: stats?.totalMonthlyIncome || 0, Expenses: stats?.totalMonthlySpending || 0 }]}>
                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e0e0e0" />
                                <XAxis dataKey="name" stroke="var(--dark-grey)" fontFamily="var(--font-mono)" tick={{ fontSize: 12, fontWeight: 'bold' }} />
                                <YAxis stroke="var(--dark-grey)" fontFamily="var(--font-mono)" tick={{ fontSize: 12, fontWeight: 'bold' }} axisLine={false} tickLine={false} />
                                <Tooltip cursor={{fill: 'var(--off-white)'}} contentStyle={{ border: '2px solid var(--black)', borderRadius: '4px', fontFamily: 'var(--font-mono)', fontWeight: 'bold', boxShadow: '4px 4px 0px var(--black)' }} />
                                <Bar dataKey="Income" fill="var(--electric-blue)" stroke="var(--black)" strokeWidth={2} radius={[4, 4, 0, 0]} />
                                <Bar dataKey="Expenses" fill="var(--hot-pink)" stroke="var(--black)" strokeWidth={2} radius={[4, 4, 0, 0]} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                </div>

                {/* Pie Chart Section */}
                <div className="y2k-card">
                    <div className="y2k-title">
                        Spending Breakdown
                    </div>
                    <div style={{ height: '350px', marginTop: '1rem' }}>
                        <ResponsiveContainer width="100%" height="100%">
                            <PieChart>
                                <Pie 
                                    data={pieData.length > 0 ? pieData : [{ name: 'No Data', value: 1 }]} 
                                    cx="50%" 
                                    cy="50%" 
                                    innerRadius={80} 
                                    outerRadius={120} 
                                    paddingAngle={4} 
                                    dataKey="value" 
                                    stroke="var(--black)" 
                                    strokeWidth={2}
                                >
                                    {pieData.map((entry, index) => (
                                        <Cell key={`cell-${index}`} fill={PIE_COLORS[index % PIE_COLORS.length]} />
                                    ))}
                                    {pieData.length === 0 && <Cell fill="var(--light-grey)" />}
                                </Pie>
                                <Tooltip contentStyle={{ border: '2px solid var(--black)', borderRadius: '4px', fontFamily: 'var(--font-mono)', fontWeight: 'bold', boxShadow: '4px 4px 0px var(--black)' }} />
                            </PieChart>
                        </ResponsiveContainer>
                    </div>
                </div>
            </div>

            {/* Budget Progress Tracker & Live Feed */}
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
                <div className="y2k-card">
                    <div className="y2k-title">Budget Warning</div>
                    {budgets.length > 0 ? budgets.map((budget, index) => {
                        const spent = stats?.spendingByCategory?.[budget.category] || 0;
                        const percentage = Math.min((spent / budget.monthlyLimit) * 100, 100);
                        const isOver = spent > budget.monthlyLimit;

                        return (
                            <div key={index} style={{ marginBottom: '2rem' }}>
                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem', fontWeight: '800' }}>
                                    <span>{budget.category}</span>
                                    <span style={{ fontFamily: 'var(--font-mono)' }}>₹{spent.toFixed(2)} / ₹{budget.monthlyLimit.toFixed(2)}</span>
                                </div>
                                <div style={{ width: '100%', height: '16px', backgroundColor: 'var(--light-grey)', borderRadius: '999px', overflow: 'hidden', border: '2px solid var(--black)' }}>
                                    <div style={{ 
                                        width: `${percentage}%`, 
                                        height: '100%', 
                                        backgroundColor: isOver ? 'var(--hot-pink)' : 'var(--lima-green)', 
                                        borderRight: percentage > 0 ? '2px solid var(--black)' : 'none' 
                                    }}></div>
                                </div>
                                {isOver && (
                                    <p style={{ marginTop: '0.75rem', fontWeight: '800', fontSize: '0.875rem', color: 'var(--error)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        <span className="y2k-badge" style={{ borderColor: 'var(--error)', color: 'var(--error)' }}>⚠ OVER BUDGET</span>
                                        Reduce spending in {budget.category}
                                    </p>
                                )}
                            </div>
                        );
                    }) : (
                        <p style={{ fontFamily: 'var(--font-mono)', color: 'var(--grey)' }}>No budgets set yet.</p>
                    )}
                </div>

                <div className="y2k-card">
                    <div className="y2k-title">Recent Transactions</div>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                        {recentExpenses.length > 0 ? recentExpenses.map((t, i) => (
                            <div key={i} style={{ 
                              display: 'flex', 
                              justifyContent: 'space-between', 
                              padding: '1rem 1.25rem', 
                              border: '2px solid var(--light-grey)', 
                              borderRadius: '8px',
                              alignItems: 'center',
                              transition: 'all 0.2s ease',
                              cursor: 'pointer'
                            }}
                            onMouseOver={e => e.currentTarget.style.borderColor = 'var(--black)'}
                            onMouseOut={e => e.currentTarget.style.borderColor = 'var(--light-grey)'}
                            >
                                <div>
                                    <div style={{ fontWeight: '800', fontSize: '1.1rem', marginBottom: '0.25rem' }}>{t.description}</div>
                                    <span className="y2k-badge" style={{ backgroundColor: t.type === 'income' ? 'var(--lima-green)' : (CATEGORY_COLORS[t.category] || 'var(--light-grey)'), fontSize: '0.65rem', padding: '0.15rem 0.5rem' }}>
                                      {t.type === 'income' ? 'INCOME' : t.category}
                                    </span>
                                </div>
                                <div style={{ 
                                    fontWeight: '900', 
                                    fontFamily: 'var(--font-main)', 
                                    fontSize: '1.25rem', 
                                    color: t.type === 'income' ? 'var(--lima-green)' : 'var(--error)' 
                                }}>
                                  {t.type === 'income' ? '+' : '-'}₹{t.amount.toFixed(2)}
                                </div>
                            </div>
                        )) : (
                            <p style={{ fontFamily: 'var(--font-mono)', color: 'var(--grey)' }}>No recent transactions.</p>
                        )}
                    </div>
                </div>
            </div>

        </div>
    );
};

export default Dashboard;
