<template>
  <div class="admin-container">
    <aside class="sidebar">
      <div class="sidebar-logo">
        <span class="logo-icon">L</span>
        <h2>LATRA ADMIN</h2>
      </div>
      
      <nav class="sidebar-menu">
        <router-link to="/admin/dashboard" class="menu-item" active-class="active">
          <span>📊</span> Tổng quan
        </router-link>
        <router-link to="/admin/orders" class="menu-item" active-class="active">
          <span>🧾</span> Đơn hàng
        </router-link>
        <router-link to="/admin/products" class="menu-item" active-class="active">
          <span>📦</span> Sản phẩm
        </router-link>
        <router-link to="/admin/users" class="menu-item" active-class="active">
          <span>👥</span> Nhân sự & User
        </router-link>
        <router-link to="/pos" class="menu-item pos-link">
          <span>🛒</span> Vào màn hình POS
        </router-link>
      </nav>
    </aside>

    <div class="main-wrapper">
      <header class="top-header">
        <div class="header-left">
          <h3>Hệ thống quản trị</h3>
        </div>
        <div class="header-right">
          <div class="admin-info">
            <span class="admin-name">Xin chào, Admin</span>
            <div class="avatar">AD</div>
          </div>
          <button @click="handleLogout" class="logout-btn">Đăng xuất</button>
        </div>
      </header>

      <main class="content-area">
        <router-view /> 
      </main>
      
      <footer class="footer">
        <p>© 2026 LATRA. Thiết kế bởi Nhóm Đồ Án.</p>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const handleLogout = () => {
  localStorage.removeItem('jwt_token');
  router.push('/login');
};
</script>

<style scoped>
.admin-container {
  display: flex;
  height: 100vh;
  background-color: #f1f5f9;
  font-family: Arial, sans-serif;
}

/* SIDEBAR */
.sidebar {
  width: 260px;
  background-color: #111111; /* Đen xám sang trọng */
  color: white;
  display: flex;
  flex-direction: column;
}
.sidebar-logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #000000; /* Nền logo đen tuyền */
  gap: 12px;
}
.logo-icon {
  background-color: #ffffff; /* Icon nổi bật với nền trắng */
  color: #000000; /* Chữ L màu đen */
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-weight: bold;
}
.sidebar-logo h2 {
  font-size: 18px;
  margin: 0;
}
.sidebar-menu {
  padding: 20px 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #94a3b8;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.2s;
}
.menu-item:hover {
  background-color: #333333; /* Xám đậm khi đưa chuột qua */
  color: white;
}
.menu-item.active {
  background-color: #ffffff; /* Nổi bật menu đang chọn bằng màu trắng */
  color: #000000; /* Chữ màu đen */
  font-weight: bold;
}
.pos-link {
  margin-top: 20px;
  border: 1px dashed #666666; /* Viền xám thay vì xanh */
  color: #ffffff;
}
.pos-link:hover {
  background-color: #ffffff;
  color: #000000;
}

/* MAIN WRAPPER */
.main-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* HEADER */
.top-header {
  height: 64px;
  background-color: white;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
}
.header-left h3 {
  margin: 0;
  font-size: 18px;
  color: #1e293b;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}
.admin-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.admin-name {
  font-size: 14px;
  color: #475569;
}
.avatar {
  width: 36px;
  height: 36px;
  background-color: #000000; /* Avatar đen */
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}
.logout-btn {
  padding: 6px 12px;
  background-color: #000000; /* Nút đăng xuất màu đen */
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.2s;
}
.logout-btn:hover {
  background-color: #333333;
}

/* CONTENT AREA */
.content-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

/* FOOTER */
.footer {
  height: 40px;
  background-color: white;
  border-top: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  color: #64748b;
}
</style>