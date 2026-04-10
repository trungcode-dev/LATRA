<template>
  <div class="imei-manager-container">
    <h2>Nhập Kho IMEI Số Lượng Lớn</h2>
    <p class="subtitle">Thêm hàng loạt mã IMEI cho điện thoại mới nhập kho.</p>

    <div class="card-form">
      <div class="form-group">
        <label>Chọn Phiên bản Điện thoại (Variant):</label>
        <select v-model="selectedVariant">
          <option value="" disabled>-- Bấm để chọn máy --</option>
          
          <optgroup label="Apple iPhone">
            <option value="1">iPhone 15 Pro Max 256GB - Titan Tự Nhiên</option>
            <option value="2">iPhone 14 Pro 128GB - Tím (Deep Purple)</option>
            <option value="3">iPhone 13 128GB - Hồng (Pink)</option>
          </optgroup>

          <optgroup label="Samsung Galaxy">
            <option value="4">Samsung Galaxy S24 Ultra 512GB - Đen</option>
            <option value="5">Samsung Galaxy Z Fold5 256GB - Xanh Icy</option>
          </optgroup>

          <optgroup label="Android Khác">
            <option value="6">Xiaomi 14 Ultra 512GB - Trắng</option>
            <option value="7">OPPO Find X7 Ultra 256GB - Xanh dương</option>
          </optgroup>
        </select>
      </div>

      <div class="form-group">
        <label>Danh sách mã IMEI:</label>
        <p class="hint-text">Mỗi mã 1 dòng, hoặc cách nhau bằng dấu phẩy. Bắt buộc 15 chữ số.</p>
        
        <textarea
          v-model="imeiList"
          rows="8"
          :class="{ 'has-error': invalidImeis.length > 0 }"
          placeholder="Ví dụ:&#10;351234567890123&#10;351234567890124"
        ></textarea>
        
        <div v-if="invalidImeis.length > 0" class="error-message">
          <strong>⚠️ Cảnh báo:</strong> Phát hiện {{ invalidImeis.length }} mã không hợp lệ! 
          <br>
          <span>(Mã sai: {{ invalidImeis.join(', ') }})</span>
        </div>

        <div v-else class="counter" :class="{ 'has-data': parsedImeis.length > 0 }">
          Hệ thống ghi nhận: <strong>{{ parsedImeis.length }}</strong> mã IMEI hợp lệ.
        </div>
      </div>

      <button 
        class="btn-submit" 
        @click="handleSubmit" 
        :disabled="!selectedVariant || parsedImeis.length === 0 || invalidImeis.length > 0">
        Lưu Vào Kho Database
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const selectedVariant = ref('');
const imeiList = ref('');

// BƯỚC 1: Gom tất cả những gì Admin nhập vào thành 1 mảng thô (cắt bỏ khoảng trắng thừa)
const rawImeis = computed(() => {
  if (!imeiList.value.trim()) return [];
  return imeiList.value
    .split(/[\n, ]+/)
    .map(i => i.trim())
    .filter(i => i.length > 0);
});

// BƯỚC 2: Lọc ra danh sách mã ĐÚNG CHUẨN (gồm đúng 15 chữ số)
const parsedImeis = computed(() => {
  return rawImeis.value.filter(i => /^\d{15}$/.test(i));
});

// BƯỚC 3: Lọc ra danh sách mã SAI (chứa chữ, hoặc dài/ngắn hơn 15 số)
const invalidImeis = computed(() => {
  return rawImeis.value.filter(i => !/^\d{15}$/.test(i));
});

// Xử lý gửi dữ liệu
const handleSubmit = () => {
  console.log("ID Máy:", selectedVariant.value);
  console.log("Danh sách mã chuẩn:", parsedImeis.value);
  
  alert(`Thành công! Đã đẩy ${parsedImeis.value.length} mã IMEI lên server.`);
  
  imeiList.value = '';
  selectedVariant.value = '';
};
</script>

<style scoped>
/* (Giữ nguyên các CSS cũ của bạn ở trên...) */
.imei-manager-container { 
  padding: 50px 20px; 
  font-family: 'Segoe UI', Arial, sans-serif; 
  display: flex;
  flex-direction: column;
  align-items: center; /* Bí kíp căn giữa nằm ở đây */
  background-color: #f8f9fa; /* Thêm tí màu nền xám nhạt cho toàn trang để nổi bật form trắng */
  min-height: 100vh;
}

h2 { 
  color: #2c3e50; 
  margin-bottom: 8px; 
  text-align: center; /* Căn giữa tiêu đề */
}

.subtitle { 
  color: #7f8c8d; 
  margin-bottom: 30px; 
  text-align: center; /* Căn giữa câu mô tả */
}

/* Thẻ Form: Thon gọn lại, đổ bóng mượt hơn */
.card-form {
  background: white; 
  padding: 35px 40px; 
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08); /* Đổ bóng xịn xò như các trang web lớn */
  width: 100%;
  max-width: 550px; /* Bóp chiều rộng lại cho form thon gọn hơn */
  text-align: left; /* Giữ chữ bên trong form căn lề trái cho dễ đọc */
}

.form-group { margin-bottom: 20px; }
label { display: block; font-weight: bold; margin-bottom: 8px; color: #34495e; }
.hint-text { font-size: 13px; color: #95a5a6; margin-bottom: 10px; font-style: italic; }

select, textarea {
  width: 100%; padding: 12px; border: 1px solid #bdc3c7;
  border-radius: 6px; font-size: 15px; outline: none; transition: 0.3s;
}

/* CSS cho trạng thái bình thường */
select:focus, textarea:focus { border-color: #3498db; box-shadow: 0 0 5px rgba(52,152,219,0.3); }

/* --- CSS MỚI: TRẠNG THÁI BÁO LỖI CHỮ ĐỎ --- */
textarea.has-error {
  border-color: #e74c3c;
  background-color: #fffaf9;
  box-shadow: 0 0 5px rgba(231, 76, 60, 0.3);
}

.error-message {
  margin-top: 10px; padding: 12px;
  background-color: #fdeded; color: #c0392b;
  border-left: 4px solid #e74c3c; border-radius: 4px;
  font-size: 14px; animation: shake 0.4s ease-in-out;
}

.counter {
  margin-top: 10px; padding: 10px; background: #f8f9fa;
  border-radius: 6px; color: #7f8c8d; font-size: 14px;
}
.counter.has-data { background: #d4edda; color: #155724; font-weight: bold; }

.btn-submit {
  background: #27ae60; color: white; border: none; padding: 14px 24px;
  border-radius: 6px; font-size: 16px; font-weight: bold; cursor: pointer; width: 100%; transition: 0.3s;
}
.btn-submit:hover:not(:disabled) { background: #2196f3; }
.btn-submit:disabled { background: #bdc3c7; cursor: not-allowed; opacity: 0.7; }

/* Hiệu ứng rung nhẹ khi báo lỗi */
@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
}
</style>