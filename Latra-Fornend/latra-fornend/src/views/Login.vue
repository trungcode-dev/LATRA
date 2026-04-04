<template>
  <div class="login-container">
    <h2>ĐĂNG NHẬP HỆ THỐNG</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>Tài khoản:</label>
        <input v-model="form.username" type="text" required placeholder="Nhập username" />
      </div>
      <div class="form-group">
        <label>Mật khẩu:</label>
        <input v-model="form.password" type="password" required placeholder="Nhập mật khẩu" />
      </div>
      <button type="submit">Đăng nhập</button>
    </form>
    
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({
  username: '',
  password: ''
})
const errorMessage = ref('')

const handleLogin = async () => {
  try {
    errorMessage.value = ''
    const response = await axios.post('http://localhost:8080/api/auth/login', form.value)
    
    // Lưu thông tin user vào localStorage (để các trang khác dùng)
    localStorage.setItem('user', JSON.stringify(response.data))
    
    alert('Đăng nhập thành công!')
    
    // Chuyển hướng sang trang chủ sau khi login
    router.push('/')
  } catch (error) {
    if (error.response) {
      errorMessage.value = error.response.data
    } else {
      errorMessage.value = 'Không thể kết nối đến server!'
    }
  }
}
</script>

<style scoped>
.login-container { max-width: 400px; margin: 50px auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;}
.form-group { margin-bottom: 15px; }
label { display: block; margin-bottom: 5px; }
input { width: 100%; padding: 8px; box-sizing: border-box; }
button { width: 100%; padding: 10px; background-color: #2c3e50; color: white; border: none; cursor: pointer; }
.error { color: red; margin-top: 10px; text-align: center; }
</style>