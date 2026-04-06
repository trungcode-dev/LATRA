<template>
  <div class="pos-layout">
    <header class="header">
      <div class="header-left">
        <div class="logo">
          LATRA
        </div>
        
        <nav class="nav-menu">
          <button class="nav-item active"><span>🛒</span> Bán hàng</button>
          
          <router-link v-if="isAdmin" to="/admin/dashboard" class="nav-item" style="text-decoration: none;">
            <span>📊</span> Về trang Quản trị
          </router-link>
          
          <button class="nav-item"><span>🧾</span> Hóa đơn</button>
          <button class="nav-item"><span>📦</span> Sản phẩm</button>
        </nav>
      </div>
      <div class="header-right">
        <div class="time"><span>🕒</span> {{ currentTime }}</div>
        <button class="notification">
          <span>🔔</span>
          <span class="badge"></span>
        </button>
        <div class="avatar" @click="handleLogout" style="cursor: pointer;" title="Đăng xuất">AD</div>
      </div>
    </header>

    <main class="main-content">
      
      <section class="product-section">
        <div class="section-header">
          <div>
            <h1>Điểm bán hàng</h1>
            <p>Chọn sản phẩm để thêm vào giỏ hàng</p>
          </div>
          <div class="search-bar">
            <input type="text" placeholder="🔍 Tìm sản phẩm, SKU...">
          </div>
        </div>

        <div class="product-grid">
          <div class="product-card" v-for="product in products" :key="product.id">
            <div class="stock-badge">Tồn: {{ product.stock }}</div>
            <div class="product-info">
              <div class="product-image">📱</div>
              <h3>{{ product.name }}</h3>
              <p>{{ product.category }}</p>
            </div>
            <div class="product-action">
              <span class="price">{{ formatPrice(product.price) }} đ</span>
              <button class="add-btn">+</button>
            </div>
          </div>
        </div>
      </section>

      <aside class="cart-sidebar">
        <div class="cart-header">
          <h2><span>🛍️</span> HÓA ĐƠN MỚI</h2>
          <button class="customer-type">Khách Online</button>
        </div>

        <div class="cart-empty">
          <div class="empty-icon">🛒</div>
          <p>Giỏ hàng đang trống</p>
        </div>

        <div class="cart-footer">
          <div class="summary-line">
            <span>Tổng tiền hàng (0):</span>
            <span>0 đ</span>
          </div>
          <div class="summary-line discount">
            <span>Voucher giảm giá:</span>
            <span>- 0 đ</span>
          </div>
          <div class="total-line">
            <span>THANH TOÁN</span>
            <span class="total-price">0 đ</span>
          </div>
          <button class="checkout-btn">💳 TẠO ĐƠN HÀNG</button>
        </div>
      </aside>

    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const currentTime = ref('');
let timer = null;

const updateClock = () => {
  const now = new Date();
  
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  
  const day = String(now.getDate()).padStart(2, '0');
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const year = now.getFullYear();

  currentTime.value = `${hours}:${minutes}:${seconds} - ${day}/${month}/${year}`;
};

onMounted(() => {
  updateClock(); 
  timer = setInterval(updateClock, 1000);
  
  const role = localStorage.getItem('user_role');
  if (role === 'ROLE_ADMIN') {
    isAdmin.value = true;
  }
});

onUnmounted(() => {
  clearInterval(timer);
});

const products = ref([
  { id: 1, name: 'iPhone 17 Pro Max', category: 'Điện thoại • Đen Titan', price: 34990000, stock: 15 },
  { id: 2, name: 'Ốp lưng Magsafe Ultra', category: 'Phụ kiện • Trong suốt', price: 289000, stock: 99 },
  { id: 3, name: 'Củ sạc 20W Fast Charge', category: 'Phụ kiện • Trắng', price: 450000, stock: 200 },
  { id: 4, name: 'Kính cường lực KingKong', category: 'Phụ kiện • Trong suốt', price: 150000, stock: 100 },
  { id: 5, name: 'Galaxy S24 Ultra AI', category: 'Điện thoại • Xám Titan', price: 31990000, stock: 8 },
]);

const formatPrice = (value) => {
  return new Intl.NumberFormat('vi-VN').format(value);
};


const isAdmin = ref(false);
onMounted(() => {
  const role = localStorage.getItem('user_role');
  if (role === 'ROLE_ADMIN') {
    isAdmin.value = true;
  }
});

const handleLogout = () => {
  localStorage.removeItem('jwt_token'); 
  localStorage.removeItem('user_role'); 
  router.push('/login'); 
};
</script>

<style scoped>

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

.pos-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #f8fafc;
  font-family: Arial, sans-serif;
}


.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 64px;
  background-color: #ffffff;
  padding: 0 24px;
  border-bottom: 1px solid #e2e8f0;
}
.header-left, .header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}
.logo {
  font-size: 24px;
  font-weight: 900;
  color: #000000;
  letter-spacing: 1px; 
}
.logo-icon {
  width: 32px;
  height: 32px;
  background-color: #4f46e5;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
}
.nav-menu {
  display: flex;
  gap: 8px;
}
.nav-item {
  padding: 8px 16px;
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 14px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
}
.nav-item.active {
  background-color: #f1f5f9;
  color: #000000;
  font-weight: bold;
}
.time {
  border: 1px solid #e2e8f0;
  padding: 6px 16px;
  border-radius: 50px;
  background-color: #f8fafc;
  font-size: 14px;
  color: #475569;
}
.notification {
  position: relative;
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
}
.badge {
  position: absolute;
  top: 0;
  right: 0;
  width: 8px;
  height: 8px;
  background-color: #ef4444;
  border-radius: 50%;
}
.avatar {
  width: 36px;
  height: 36px;
  background-color: #4f46e5;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

/* MAIN CONTENT CSS */
.main-content {
  display: flex;
  flex: 1;
  gap: 24px;
  padding: 24px;
  overflow: hidden;
}

/* CỘT TRÁI - SẢN PHẨM */
.product-section {
  flex: 1;
  background-color: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}
.section-header h1 {
  font-size: 24px;
  color: #1e293b;
}
.section-header p {
  color: #64748b;
  font-size: 14px;
  margin-top: 4px;
}
.search-bar input {
  width: 300px;
  padding: 10px 16px 10px 40px;
  border: 1px solid #e2e8f0;
  border-radius: 50px;
  background-color: #f8fafc;
  outline: none;
}

/* Lưới Sản Phẩm */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}
.product-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 16px;
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 180px;
  transition: box-shadow 0.2s;
}
.product-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.stock-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background-color: #ecfdf5;
  color: #10b981;
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
  font-weight: bold;
}
.product-image {
  width: 48px;
  height: 64px;
  background-color: #eff6ff;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  font-size: 24px;
}
.product-info h3 {
  font-size: 14px;
  color: #1e293b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.product-info p {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 4px;
  margin-bottom: 16px;
}
.product-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}
.price {
  font-weight: bold;
  font-size: 16px;
  color: #0f172a;
}
.add-btn {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: white;
  color: #000000;
  border: none;
  font-size: 18px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* CỘT PHẢI - GIỎ HÀNG */
.cart-sidebar {
  width: 380px;
  background-color: white;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  display: flex;
  flex-direction: column;
}
.cart-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e2e8f0;
  background-color: #f8fafc;
  border-radius: 12px 12px 0 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.cart-header h2 {
  font-size: 16px;
  color: #1e293b;
}
.customer-type {
  padding: 4px 12px;
  font-size: 12px;
  background-color: #e2e8f0;
  border: none;
  border-radius: 50px;
  color: #475569;
  font-weight: bold;
}
.cart-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
}
.empty-icon {
  font-size: 48px;
  opacity: 0.3;
  margin-bottom: 12px;
}
.cart-footer {
  padding: 20px;
  border-top: 1px solid #e2e8f0;
  background-color: #f8fafc;
  border-radius: 0 0 12px 12px;
}
.summary-line {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #64748b;
  margin-bottom: 8px;
}
.summary-line.discount {
  color: #6366f1;
}
.total-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0 24px 0;
}
.total-line span:first-child {
  font-size: 12px;
  font-weight: bold;
  color: #94a3b8;
}
.total-price {
  font-size: 24px;
  font-weight: 900;
  color: #0f172a;
}
.checkout-btn {
  width: 100%;
  padding: 14px;
  background-color: #000000;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}
.checkout-btn:hover {
  background-color: #333333;
}
.add-btn:hover {
  background-color: #000000; 
  color: white;
  border-color: #000000;
}
</style>