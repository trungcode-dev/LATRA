<template>
  <div class="auth-layout">
    <div class="auth-card">
      <div class="logo">LATRA</div>
      
      <div class="auth-header">
        <h2>Tạo tài khoản</h2>
        <p class="subtitle">Điền thông tin để đăng ký hệ thống</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <input type="text" v-model="username" placeholder="Email hoặc Tên đăng nhập" required>
        </div>
        
        <div class="form-group">
          <input type="password" v-model="password" placeholder="Mật khẩu" required>
        </div>

        <div class="form-group">
          <select v-model="role" required class="role-select">
            <option value="" disabled>-- Chọn chức vụ --</option>
            <option value="ROLE_ADMIN">Quản lý cửa hàng (Admin)</option>
            <option value="ROLE_STAFF">Nhân viên thu ngân (Staff)</option>
          </select>
        </div>

        <button type="submit" class="submit-btn">Đăng ký tài khoản</button>
      </form>

      <div class="terms">
        Đã có tài khoản? <router-link to="/login">Đăng nhập ngay</router-link>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const username = ref('');
const password = ref('');
const role = ref('');

const handleRegister = () => {
  if (!role.value) {
    alert("Vui lòng chọn chức vụ!");
    return;
  }

  // 1. Tạo một object chứa tài khoản mới
  const newUser = {
    username: username.value,
    password: password.value,
    role: role.value
  };

  // 2. Lưu tài khoản này vào bộ nhớ trình duyệt (Giả lập Database)
  localStorage.setItem('registered_user', JSON.stringify(newUser));

  alert(`Đăng ký thành công!\nTài khoản: ${username.value}\nBạn có thể dùng tài khoản này để đăng nhập ngay.`);
  
  router.push('/login');
};
</script>

<style scoped>
/* Giữ nguyên CSS cũ của bạn, chỉ thêm style cho thẻ select */
.auth-layout {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background-color: #f8fafc;
}
.auth-card {
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
  text-align: center;
}
.logo {
  font-size: 42px;
  font-weight: 900;
  color: #000000;
  margin-bottom: 24px;
}
.auth-card h2 {
  font-size: 24px;
  color: #1e293b;
  margin-bottom: 8px;
}
.subtitle {
  color: #64748b;
  font-size: 14px;
  margin-bottom: 24px;
}
.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
  text-align: left;
}
.form-group label {
  display: block;
  font-size: 14px;
  font-weight: bold;
  color: #334155;
  margin-bottom: 8px;
}
.form-group input, .role-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #cbd5e1;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  background-color: white;
}
.form-group input:focus, .role-select:focus {
  border-color: #000000;
}
.submit-btn {
  background-color: #000000;
  color: white;
  padding: 12px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 8px;
}
.submit-btn:hover {
  background-color: #333333;
}
.auth-links {
  margin-top: 24px;
  font-size: 14px;
  color: #64748b;
}
.auth-links a {
  color: #000000;
  text-decoration: none;
  font-weight: bold;
}
.role-select {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
  background-color: white;
  cursor: pointer;
}
.role-select:focus {
  border-color: #000000;
  box-shadow: 0 0 0 1px #000000;
}
</style>