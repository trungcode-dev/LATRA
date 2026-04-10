<template>
  <div class="warranty-check-container">
    <h2>Tra Cứu Thông Tin Bảo Hành</h2>
    <p>Vui lòng nhập Số điện thoại hoặc IMEI máy để tra cứu.</p>

    <div class="search-box">
      <input 
        v-model="searchQuery" 
        type="text" 
        placeholder="Nhập SĐT hoặc IMEI..." 
        @keyup.enter="handleCheckWarranty"
      />
      <button :disabled="isLoading" @click="handleCheckWarranty">
        {{ isLoading ? 'Đang tra cứu...' : 'Tra cứu ngay' }}
      </button>
    </div>

    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-if="warrantyResult" class="result-card">
      <h3>Kết quả tra cứu:</h3>
      <ul>
        <li><strong>Sản phẩm:</strong> {{ warrantyResult.productName }}</li>
        <li><strong>IMEI:</strong> {{ warrantyResult.imei }}</li>
        <li><strong>Ngày mua (Kích hoạt):</strong> {{ warrantyResult.startDate }}</li>
        <li><strong>Hạn bảo hành:</strong> {{ warrantyResult.endDate }}</li>
        <li>
          <strong>Trạng thái:</strong> 
          <span :class="warrantyResult.status === 'Còn hạn' ? 'text-green' : 'text-red'">
            {{ warrantyResult.status }}
          </span>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
// import axios from 'axios'; // Mở comment dòng này khi gọi API thật

// Các biến trạng thái (State)
const searchQuery = ref('');
const isLoading = ref(false);
const errorMessage = ref('');
const warrantyResult = ref(null);

// Hàm xử lý tra cứu
const handleCheckWarranty = async () => {
  // Validate cơ bản
  if (!searchQuery.value.trim()) {
    errorMessage.value = "Vui lòng không để trống ô nhập liệu!";
    warrantyResult.value = null;
    return;
  }

  isLoading.value = true;
  errorMessage.value = '';
  warrantyResult.value = null;

  try {
    /* === CODE GỌI API THẬT (Chờ Hiếu M3 làm xong API thì dùng đoạn này) ===
      const response = await axios.get(`http://localhost:8080/api/warranty/check?query=${searchQuery.value}`);
      warrantyResult.value = response.data;
    */

    // === CODE GIẢ LẬP (Mock Data) ĐỂ BẠN LÀM UI TRƯỚC ===
    await new Promise(resolve => setTimeout(resolve, 1000)); // Giả vờ đợi mạng 1 giây
    
    // Giả lập logic: Nếu nhập "123" thì báo lỗi, khác thì thành công
    if (searchQuery.value === '123') {
      throw new Error("Không tìm thấy thông tin bảo hành cho số này.");
    }

    // Trả về dữ liệu giả
    warrantyResult.value = {
      productName: "iPhone 15 Pro Max 256GB - Titan Tự Nhiên",
      imei: searchQuery.value.length > 10 ? searchQuery.value : "351234567890123",
      startDate: "10/04/2025",
      endDate: "10/04/2026",
      status: "Còn hạn"
    };

  } catch (error) {
    errorMessage.value = error.message || "Có lỗi xảy ra khi kết nối máy chủ.";
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Khung chứa tổng thể: Căn giữa, tạo viền bóng như một cái thẻ (Card) */
.warranty-check-container {
  max-width: 550px;
  margin: 60px auto;
  padding: 40px 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
  text-align: center;
}

.warranty-check-container h2 {
  color: #2c3e50;
  margin-bottom: 8px;
  font-size: 24px;
  font-weight: 700;
}

.warranty-check-container p {
  color: #7f8c8d;
  margin-bottom: 30px;
  font-size: 15px;
}

/* Khu vực thanh tìm kiếm và nút bấm */
.search-box {
  display: flex;
  gap: 12px;
  margin-bottom: 25px;
}

.search-box input {
  flex: 1;
  padding: 14px 18px;
  border: 1.5px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  outline: none;
  transition: all 0.3s ease;
}

.search-box input:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.2);
}

.search-box button {
  padding: 14px 24px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  white-space: nowrap;
}

.search-box button:hover {
  background-color: #2980b9;
}

.search-box button:disabled {
  background-color: #95a5a6;
  cursor: not-allowed;
}

/* Thẻ kết quả trả về */
.result-card {
  background-color: #f8f9fa;
  border: 1px solid #edf2f7;
  padding: 25px;
  border-radius: 10px;
  text-align: left;
  animation: fadeIn 0.4s ease-in-out;
}

.result-card h3 {
  margin-top: 0;
  color: #2c3e50;
  border-bottom: 2px dashed #cbd5e1;
  padding-bottom: 12px;
  margin-bottom: 18px;
  font-size: 18px;
}

.result-card ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.result-card li {
  margin-bottom: 12px;
  font-size: 15px;
  color: #4a5568;
  display: flex;
  justify-content: space-between;
}

.result-card li strong {
  color: #2d3748;
}

/* Màu sắc trạng thái */
.text-green { color: #10b981; font-weight: 700; background: #d1fae5; padding: 2px 8px; border-radius: 4px;}
.text-red { color: #ef4444; font-weight: 700; background: #fee2e2; padding: 2px 8px; border-radius: 4px;}
.error-message { color: #ef4444; margin-bottom: 20px; font-weight: 500; text-align: left; padding-left: 5px;}

/* Hiệu ứng xuất hiện mượt mà */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>