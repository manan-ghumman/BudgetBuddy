import api from './api';

const getAllIncomes = () => {
  return api.get('/incomes');
};

const addIncome = (incomeData) => {
  return api.post('/incomes', incomeData);
};

const deleteIncome = (id) => {
  return api.delete(`/incomes/${id}`);
};

const incomeService = {
  getAllIncomes,
  addIncome,
  deleteIncome,
};

export default incomeService;
