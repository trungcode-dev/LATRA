import { createRouter, createWebHistory } from 'vue-router'
import Checkout from '../views/Checkout.vue'

// 1. Nhúng các giao diện vào đây
import WarrantyCheck from '../views/WarrantyCheck.vue'
// (Sau này bạn Huy/Đan code xong trang Chủ, trang Giỏ hàng thì nhúng tiếp vào đây)

// 2. Khai báo các đường link
const routes = [
  {
    path: '/tra-cuu-bao-hanh',
    name: 'WarrantyCheck',
    component: WarrantyCheck
  },
  { path: '/thanh-toan', name: 'Checkout', component: Checkout }
]

// 3. Khởi tạo bộ định tuyến
const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router