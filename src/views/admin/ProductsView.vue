<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <h1>Quản lý Sản phẩm</h1>
        <p class="subtitle">Danh sách tất cả sản phẩm trong kho</p>
      </div>
      <button class="btn btn-primary" @click="openAddModal">
        <span>+</span> Thêm sản phẩm
      </button>
    </div>

    <DataTable 
  :columns="tableColumns" 
  :data="paginatedProducts"
  :currentPage="currentPage"
  :totalPages="totalPages"
  @page-change="changePage"
>
      
      <template #image>
        <div class="img-placeholder">📱</div>
      </template>

      <template #name="{ item }">
        <span class="font-bold">{{ item.name }}</span>
      </template>

      <template #price="{ item }">
        <span class="text-blue">{{ formatPrice(item.price) }} đ</span>
      </template>

      <template #stock="{ item }">
        <span :class="['badge', item.stock > 10 ? 'badge-success' : 'badge-warning']">
          {{ item.stock }}
        </span>
      </template>

      <template #actions="{ item }">
        <div class="action-cell">
          <button class="btn-icon text-orange" @click="openEditModal(item)" title="Sửa">✏️</button>
          <button class="btn-icon text-red" @click="deleteProduct(item.id)" title="Xóa">🗑️</button>
        </div>
      </template>

    </DataTable>

    <div class="modal-overlay" v-if="isModalOpen" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditing ? 'Sửa sản phẩm' : 'Thêm sản phẩm mới' }}</h2>
          <button class="close-btn" @click="closeModal">✖</button>
        </div>
        
        <form @submit.prevent="saveProduct" class="modal-form">
          <div class="form-group">
            <label>Tên sản phẩm</label>
            <input type="text" v-model="formData.name" required placeholder="Nhập tên sản phẩm...">
          </div>
          <div class="form-group">
            <label>Danh mục</label>
            <input type="text" v-model="formData.category" required placeholder="Ví dụ: Điện thoại, Phụ kiện...">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Giá bán (VNĐ)</label>
              <input type="number" v-model="formData.price" required min="0">
            </div>
            <div class="form-group">
              <label>Số lượng tồn</label>
              <input type="number" v-model="formData.stock" required min="0">
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-outline" @click="closeModal">Hủy</button>
            <button type="submit" class="btn btn-primary">Lưu thông tin</button>
          </div>
        </form>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'; // Đã thêm computed ở đây
import DataTable from '../../components/common/DataTable.vue';

// Định nghĩa các cột cho Bảng
const tableColumns = ref([
  { key: 'id', label: 'ID' },
  { key: 'image', label: 'Hình ảnh' },
  { key: 'name', label: 'Tên sản phẩm' },
  { key: 'category', label: 'Danh mục' },
  { key: 'price', label: 'Giá bán' },
  { key: 'stock', label: 'Tồn kho' },
  { key: 'actions', label: 'Hành động', align: 'center' }
]);

// Danh sách sản phẩm (Đã thêm nhiều sản phẩm để test phân trang)
const products = ref([
  { id: 1, name: 'iPhone 17 Pro Max', category: 'Điện thoại', price: 34990000, stock: 15 },
  { id: 2, name: 'Ốp lưng Magsafe Ultra', category: 'Phụ kiện', price: 289000, stock: 99 },
  { id: 3, name: 'Galaxy S24 Ultra AI', category: 'Điện thoại', price: 31990000, stock: 8 },
  { id: 4, name: 'Củ sạc 20W Fast Charge', category: 'Phụ kiện', price: 450000, stock: 200 },
  { id: 5, name: 'Kính cường lực KingKong', category: 'Phụ kiện', price: 150000, stock: 100 },
  { id: 6, name: 'Tai nghe AirPods Pro 3', category: 'Phụ kiện', price: 6500000, stock: 30 },
  { id: 7, name: 'Cáp sạc C to C Bọc dù', category: 'Phụ kiện', price: 120000, stock: 50 },
]);

// ==========================================
// LOGIC PHÂN TRANG (MỚI THÊM)
// ==========================================
const currentPage = ref(1);
const itemsPerPage = 5; // Số sản phẩm hiển thị trên 1 trang

// Tính tổng số trang
const totalPages = computed(() => {
  return Math.ceil(products.value.length / itemsPerPage);
});

// Cắt mảng sản phẩm ra để hiển thị đúng trang hiện tại
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return products.value.slice(start, end);
});

// Xử lý khi bấm nút "Trước" / "Sau"
const changePage = (newPage) => {
  if (newPage >= 1 && newPage <= totalPages.value) {
    currentPage.value = newPage;
  }
};
// ==========================================

const formatPrice = (value) => new Intl.NumberFormat('vi-VN').format(value);

/* Logic Modal giữ nguyên của bạn */
const isModalOpen = ref(false);
const isEditing = ref(false);
const formData = ref({ id: null, name: '', category: '', price: 0, stock: 0 });

const openAddModal = () => { isEditing.value = false; formData.value = { id: null, name: '', category: '', price: 0, stock: 0 }; isModalOpen.value = true; };
const openEditModal = (product) => { isEditing.value = true; formData.value = { ...product }; isModalOpen.value = true; };
const closeModal = () => { isModalOpen.value = false; };

const saveProduct = () => {
  if (isEditing.value) {
    const index = products.value.findIndex(p => p.id === formData.value.id);
    if (index !== -1) products.value[index] = { ...formData.value };
  } else {
    const newId = products.value.length > 0 ? Math.max(...products.value.map(p => p.id)) + 1 : 1;
    products.value.push({ ...formData.value, id: newId });
  }
  closeModal();
};

const deleteProduct = (id) => {
  if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
    products.value = products.value.filter(p => p.id !== id);
    
    // Mẹo UX: Nếu xóa hết sản phẩm ở trang cuối thì tự lùi về trang trước
    if (paginatedProducts.value.length === 0 && currentPage.value > 1) {
      currentPage.value--;
    }
  }
};
</script>

<style scoped>
/* Chỉ còn lại CSS của Layout trang và Modal, CSS của Bảng đã chuyển sang file DataTable.vue */
.page-container { display: flex; flex-direction: column; gap: 24px; }
.page-header { display: flex; justify-content: space-between; align-items: center; }
.page-header h1 { font-size: 24px; color: #1e293b; margin: 0 0 4px 0; }
.subtitle { color: #64748b; font-size: 14px; margin: 0; }
.btn { padding: 10px 16px; border-radius: 8px; font-size: 14px; font-weight: bold; cursor: pointer; border: none; display: flex; align-items: center; gap: 8px; }
.btn-primary { background-color: #000000; color: white; }
.btn-primary:hover { background-color: #333333; }
.btn-outline { background-color: white; border: 1px solid #cbd5e1; color: #475569; }
.btn-outline:hover { background-color: #f8fafc; }

/* CSS riêng cho ruột của Bảng ở trang này */
.img-placeholder { width: 40px; height: 40px; background-color: #eff6ff; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 20px; }
.font-bold { font-weight: bold; color: #0f172a; }
.text-blue { color: #2563eb; font-weight: bold; }
.badge { padding: 4px 8px; border-radius: 50px; font-size: 12px; font-weight: bold; }
.badge-success { background-color: #dcfce7; color: #16a34a; }
.badge-warning { background-color: #fef08a; color: #ca8a04; }
.action-cell { display: flex; gap: 12px; justify-content: center; }
.btn-icon { background: none; border: none; cursor: pointer; font-size: 16px; padding: 4px; border-radius: 4px; }
.btn-icon:hover { background-color: #f1f5f9; }

/* Modal CSS */
.modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-content { background: white; width: 100%; max-width: 500px; border-radius: 12px; box-shadow: 0 20px 25px -5px rgba(0,0,0,0.1); overflow: hidden; }
.modal-header { padding: 20px 24px; border-bottom: 1px solid #e2e8f0; display: flex; justify-content: space-between; align-items: center; }
.modal-header h2 { margin: 0; font-size: 18px; color: #0f172a; }
.close-btn { background: none; border: none; font-size: 20px; cursor: pointer; color: #94a3b8; }
.modal-form { padding: 24px; display: flex; flex-direction: column; gap: 16px; }
.form-group label { display: block; font-size: 14px; font-weight: bold; color: #334155; margin-bottom: 8px; }
.form-group input { width: 100%; padding: 10px 12px; border: 1px solid #cbd5e1; border-radius: 8px; font-size: 14px; outline: none; box-sizing: border-box; }
.form-group input:focus { border-color: #000000; }
.form-row { display: flex; gap: 16px; }
.form-row .form-group { flex: 1; }
.modal-footer { margin-top: 8px; display: flex; justify-content: flex-end; gap: 12px; }
</style>