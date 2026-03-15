import api from './api';

const getAllExpenses = () => {
  return api.get('/tr-exp');
};

const addExpense = (expenseData) => {
  return api.post('/tr-exp', expenseData);
};

const deleteExpense = (id) => {
  return api.delete(`/tr-exp/${id}`);
};

const updateExpense = (id, expenseData) => {
    return api.put(`/tr-exp/${id}`, expenseData);
};

const getExpensesByRange = (startDate, endDate) => {
  return api.get(`/tr-exp/range?startDate=${startDate}&endDate=${endDate}`);
};

const expenseService = {
  getAllExpenses,
  addExpense,
  deleteExpense,
  updateExpense,
  getExpensesByRange,
};

export default expenseService;
