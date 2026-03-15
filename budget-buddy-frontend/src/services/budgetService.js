import api from './api';

const getUserBudgets = () => {
  return api.get('/budgets');
};

const setBudget = (budgetData) => {
  return api.post('/budgets', budgetData);
};

const deleteBudget = (id) => {
  return api.delete(`/budgets/${id}`);
};

const budgetService = {
  getUserBudgets,
  setBudget,
  deleteBudget,
};

export default budgetService;
