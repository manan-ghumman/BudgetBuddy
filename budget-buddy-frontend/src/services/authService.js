import api from './api';

const signup = (email, password) => {
  return api.post('/auth/signup', {
    email,
    password,
  });
};

const signin = async (email, password) => {
  const response = await api.post('/auth/signin', {
    email,
    password,
  });
  if (response.data.token) {
    localStorage.setItem('user', JSON.stringify(response.data));
  }
  return response.data;
};

const logout = () => {
  localStorage.removeItem('user');
};

const getCurrentUser = () => {
  return JSON.parse(localStorage.getItem('user'));
};

const authService = {
  signup,
  signin,
  logout,
  getCurrentUser,
};

export default authService;
