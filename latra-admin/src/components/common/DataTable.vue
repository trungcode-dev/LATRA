<template>
  <div class="table-card">
    <table class="data-table">
      <thead>
        <tr>
          <th v-for="(col, index) in columns" :key="index" :class="col.align ? 'text-' + col.align : ''">
            {{ col.label }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in data" :key="item.id">
          <td v-for="(col, index) in columns" :key="index" :class="col.align ? 'text-' + col.align : ''">
            <slot :name="col.key" :item="item">
              {{ item[col.key] }}
            </slot>
          </td>
        </tr>
        <tr v-if="data.length === 0">
          <td :colspan="columns.length" class="text-center empty-state">
            Không có dữ liệu.
          </td>
        </tr>
      </tbody>
    </table>

    <div class="pagination-container" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 1" 
        @click="$emit('page-change', currentPage - 1)"
      >
        ❮ Trước
      </button>
      
      <span class="page-info">Trang <b>{{ currentPage }}</b> / {{ totalPages }}</span>
      
      <button 
        class="page-btn" 
        :disabled="currentPage === totalPages" 
        @click="$emit('page-change', currentPage + 1)"
      >
        Sau ❯
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  columns: { type: Array, required: true },
  data: { type: Array, required: true },
  // Nhận thêm 2 thông số phân trang từ trang cha
  currentPage: { type: Number, default: 1 },
  totalPages: { type: Number, default: 1 }
});

// Báo cho trang cha biết khi người dùng bấm chuyển trang
defineEmits(['page-change']);
</script>

<style scoped>
/* Code CSS cũ của bảng giữ nguyên */
.table-card { background: white; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); border: 1px solid #e2e8f0; overflow: hidden; }
.data-table { width: 100%; border-collapse: collapse; text-align: left; }
.data-table th, .data-table td { padding: 16px; border-bottom: 1px solid #e2e8f0; font-size: 14px; color: #334155; }
.data-table th { background-color: #f8fafc; font-weight: bold; color: #64748b; text-transform: uppercase; font-size: 12px; }
.data-table tbody tr:hover { background-color: #f8fafc; }
.text-center { text-align: center; }
.text-right { text-align: right; }
.empty-state { padding: 40px; color: #94a3b8; }

/* CSS cho thanh phân trang mới */
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background-color: white;
  border-top: 1px solid #e2e8f0;
}
.page-btn {
  padding: 8px 16px;
  background-color: #f1f5f9;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  cursor: pointer;
  font-weight: bold;
  color: #334155;
  transition: all 0.2s;
}
.page-btn:hover:not(:disabled) {
  background-color: #e2e8f0;
}
.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.page-info {
  font-size: 14px;
  color: #64748b;
}
</style>