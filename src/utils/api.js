import axios from 'axios';

// Tạo một "bản sao" của axios với cấu hình mặc định
const apiClient = axios.create({
  // Tạm để trống, sau này Backend gửi link thì điền vào đây (ví dụ: 'http://localhost:8080/api')
  baseURL: '', 
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000, // Quá 10s không phản hồi thì báo lỗi
});

// "Vệ sĩ" chặn đường trước khi gửi request đi
apiClient.interceptors.request.use((config) => {
  // Lấy token từ bộ nhớ
  const token = localStorage.getItem('jwt_token');
  // Nếu có token, nhét nó vào Header để Backend biết mình là ai
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default apiClient;