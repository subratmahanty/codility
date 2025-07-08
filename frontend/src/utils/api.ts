import axios, { AxiosInstance, AxiosResponse, AxiosError } from 'axios';
import toast from 'react-hot-toast';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error: AxiosError) => {
    return Promise.reject(error);
  }
);

// Response interceptor
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response;
  },
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      // Unauthorized - clear token and redirect to login
      localStorage.removeItem('auth_token');
      delete apiClient.defaults.headers.common['Authorization'];
      
      // Only show error if not already on login page
      if (window.location.pathname !== '/login') {
        toast.error('Session expired. Please login again.');
        window.location.href = '/login';
      }
    } else if (error.response?.status === 403) {
      toast.error('Access denied. You do not have permission to perform this action.');
    } else if (error.response?.status >= 500) {
      toast.error('Server error. Please try again later.');
    }
    
    return Promise.reject(error);
  }
);

// API endpoints
export const authAPI = {
  login: (credentials: { username: string; password: string }) =>
    apiClient.post('/auth/login', credentials),
  
  register: (data: any) =>
    apiClient.post('/auth/register', data),
  
  validateToken: () =>
    apiClient.get('/auth/validate'),
};

export const processAPI = {
  getProcessDefinitions: () =>
    apiClient.get('/processes'),
  
  deployBpmnProcess: (file: File, name?: string) => {
    const formData = new FormData();
    formData.append('file', file);
    if (name) formData.append('name', name);
    
    return apiClient.post('/processes/deploy/bpmn', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  deployDmnDecision: (file: File, name?: string) => {
    const formData = new FormData();
    formData.append('file', file);
    if (name) formData.append('name', name);
    
    return apiClient.post('/processes/deploy/dmn', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  
  startProcessInstance: (processDefinitionKey: string, variables?: any) =>
    apiClient.post(`/processes/${processDefinitionKey}/start`, variables),
  
  getProcessInstances: () =>
    apiClient.get('/processes/instances'),
  
  deleteProcessInstance: (processInstanceId: string, reason?: string) =>
    apiClient.delete(`/processes/instances/${processInstanceId}`, {
      params: { reason },
    }),
  
  undeployProcess: (deploymentId: string) =>
    apiClient.delete(`/processes/definitions/${deploymentId}`),
};

export const taskAPI = {
  getTasks: (params?: any) =>
    apiClient.get('/tasks', { params }),
  
  completeTask: (taskId: string, variables?: any) =>
    apiClient.post(`/tasks/${taskId}/complete`, variables),
  
  getTaskForm: (taskId: string) =>
    apiClient.get(`/tasks/${taskId}/form`),
};

export const formAPI = {
  getForms: () =>
    apiClient.get('/forms'),
  
  getForm: (formId: string) =>
    apiClient.get(`/forms/${formId}`),
  
  createForm: (formData: any) =>
    apiClient.post('/forms', formData),
  
  updateForm: (formId: string, formData: any) =>
    apiClient.put(`/forms/${formId}`, formData),
  
  deleteForm: (formId: string) =>
    apiClient.delete(`/forms/${formId}`),
  
  renderForm: (formId: string, data?: any) =>
    apiClient.post(`/forms/${formId}/render`, data),
};

export const userAPI = {
  getUsers: () =>
    apiClient.get('/users'),
  
  getUser: (userId: number) =>
    apiClient.get(`/users/${userId}`),
  
  updateUser: (userId: number, userData: any) =>
    apiClient.put(`/users/${userId}`, userData),
  
  deleteUser: (userId: number) =>
    apiClient.delete(`/users/${userId}`),
};

export default apiClient;