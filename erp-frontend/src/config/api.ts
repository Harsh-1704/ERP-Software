import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add the JWT token to headers
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to:
// 1. Unwrap the backend ApiResponse wrapper ({ success, data, message } -> data)
// 2. Handle 401 Unauthorized
api.interceptors.response.use(
  (response) => {
    // The backend wraps all responses in ApiResponse: { success, data, message, timestamp }
    // Unwrap so that service calls get the actual data directly
    const body = response.data;
    if (body !== null && typeof body === 'object' && 'success' in body && 'data' in body) {
      if (!body.success) {
        // Backend reported an error inside the wrapper
        return Promise.reject(new Error(body.message || 'Request failed'));
      }
      // Unwrap: put the inner data back into response.data
      response.data = body.data;
    }
    return response;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // Clear token and redirect to login if unauthorized
      localStorage.removeItem('token');
      localStorage.removeItem('username');
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    const errorBody = error.response?.data;
    if (errorBody && typeof errorBody === 'object' && 'success' in errorBody && errorBody.success === false) {
      return Promise.reject(new Error(errorBody.message || 'Request failed'));
    }
    return Promise.reject(error);
  }
);

export default api;
