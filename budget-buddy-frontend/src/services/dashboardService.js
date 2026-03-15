import api from './api';

const getStats = (month) => {
  const url = month ? `/dashboard/stats?month=${month}` : '/dashboard/stats';
  return api.get(url);
};

const dashboardService = {
  getStats,
};

export default dashboardService;
