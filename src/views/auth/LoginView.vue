<template>
  <div class="auth-layout">
    <div class="auth-card">
      <div class="logo">LATRA</div>
      
      <div class="auth-header">
        <h2>Đăng nhập</h2>
        <p class="subtitle">Đăng nhập hoặc tạo tài khoản mới</p>
      </div>

      <button type="button" class="social-btn">
        Tiếp tục với Google
      </button>

      <div class="divider">
        <span>hoặc</span>
      </div>

      <p v-if="errorMessage" class="error-msg">{{ errorMessage }}</p>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <input type="text" v-model="username" placeholder="Email hoặc Tên đăng nhập" required>
        </div>
        <div class="form-group">
          <input type="password" v-model="password" placeholder="Mật khẩu" required>
        </div>
        
        <button type="submit" class="submit-btn">Tiếp tục</button>

        <div class="checkbox-group">
          <input type="checkbox" id="news">
          <label for="news">Gửi cho tôi tin tức và ưu đãi</label>
        </div>
      </form>

      <div class="terms">
        Bằng cách tiếp tục, bạn đồng ý với <a href="#">Điều khoản dịch vụ</a><br><br>
        Chưa có tài khoản? <router-link to="/register">Đăng ký ngay</router-link>
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
const errorMessage = ref('');

const handleLogin = () => {
  errorMessage.value = '';

  // 1. Lấy tài khoản vừa đăng ký từ bộ nhớ ra (nếu có)
  const savedUserStr = localStorage.getItem('registered_user');
  let savedUser = null;
  if (savedUserStr) {
    savedUser = JSON.parse(savedUserStr);
  }

  // 2. Kiểm tra các kịch bản đăng nhập
  if (username.value === 'admin' && password.value === 'admin') {
    localStorage.setItem('jwt_token', 'token_admin');
    localStorage.setItem('user_role', 'ROLE_ADMIN');
    router.push('/admin/dashboard');
  } 
  else if (username.value === 'staff' && password.value === 'staff') {
    localStorage.setItem('jwt_token', 'token_staff');
    localStorage.setItem('user_role', 'ROLE_STAFF');
    router.push('/pos');
  } 
  // 3. KIỂM TRA TÀI KHOẢN MỚI TẠO 
  else if (savedUser && username.value === savedUser.username && password.value === savedUser.password) {
    localStorage.setItem('jwt_token', 'token_moi_tao_789');
    localStorage.setItem('user_role', savedUser.role);
    
    // Check xem lúc nãy đăng ký chức vụ gì để đá về đúng trang
    if (savedUser.role === 'ROLE_STAFF') {
      router.push('/pos');
    } else {
      router.push('/admin/dashboard');
    }
  } 
  // 4. Nhập sai hết
  else {
    errorMessage.value = 'Sai tài khoản hoặc mật khẩu! Bạn chưa đăng ký tài khoản này.';
  }
};
</script>

<style scoped>
.auth-layout {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f3f4f6; /* Nền xám nhạt */
}

.auth-card {
  background: white;
  padding: 40px 48px;
  border-radius: 8px;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
  width: 100%;
  max-width: 440px;
  box-sizing: border-box;
}

.logo {
  font-size: 42px;
  font-weight: 900;
  color: #000000;
  text-align: center;
  letter-spacing: 10px; /* Tạo khoảng cách chữ giống logo PEEL */
  margin-bottom: 40px;
}

.auth-header h2 {
  font-size: 22px;
  color: #111827;
  margin: 0 0 8px 0;
  font-weight: normal;
}

.auth-header .subtitle {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 24px 0;
}

.social-btn {
  width: 100%;
  background-color: #5a31f4; /* Màu tím chuẩn PEEL */
  color: white;
  padding: 14px;
  border: none;
  border-radius: 4px;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  margin-bottom: 24px;
  transition: opacity 0.2s;
}
.social-btn:hover { opacity: 0.9; }

.divider {
  display: flex;
  align-items: center;
  text-align: center;
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 24px;
}
.divider::before, .divider::after {
  content: '';
  flex: 1;
  border-bottom: 1px solid #e5e7eb;
}
.divider span { padding: 0 16px; }

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}
.form-group input:focus {
  border-color: #000000;
  box-shadow: 0 0 0 1px #000000;
}

.submit-btn {
  background-color: #000000;
  color: white;
  padding: 14px;
  border: none;
  border-radius: 4px;
  font-size: 15px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 4px;
  transition: background-color 0.2s;
}
.submit-btn:hover { background-color: #1f2937; }

.checkbox-group {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}
.checkbox-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #000000;
  cursor: pointer;
}
.checkbox-group label {
  font-size: 14px;
  color: #111827;
  cursor: pointer;
}

.terms {
  margin-top: 32px;
  text-align: center;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
}
.terms a {
  color: #111827;
  text-decoration: underline;
  font-weight: 500;
}
.error-msg {
  color: #ef4444;
  font-size: 13px;
  margin-bottom: 16px;
  text-align: center;
}
</style>