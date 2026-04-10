<template>
  <div class="checkout-container">
    <h2>Thanh Toán Đơn Hàng</h2>
    
    <div class="payment-section">
      <h3>Chọn phương thức thanh toán</h3>
      
      <div class="payment-options">
        <label class="payment-card" :class="{ active: paymentMethod === 'COD' }">
          <input type="radio" value="COD" v-model="paymentMethod">
          <div class="payment-info">
            <span class="payment-title">Thanh toán khi nhận hàng (COD)</span>
            <span class="payment-desc">Trả tiền mặt khi giao hàng tận nơi</span>
          </div>
        </label>

        <label class="payment-card" :class="{ active: paymentMethod === 'VNPAY' }">
          <input type="radio" value="VNPAY" v-model="paymentMethod">
          <div class="payment-info">
            <span class="payment-title">Thanh toán trực tuyến VNPAY</span>
            <span class="payment-desc">Quét mã QR, Thẻ ATM, Visa/Mastercard</span>
          </div>
        </label>
      </div>

      <button class="btn-order" @click="handleCheckout">
        Xác Nhận Đặt Hàng
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// Biến lưu trữ lựa chọn của khách (Mặc định là COD)
const paymentMethod = ref('COD'); 

// Hàm xử lý khi bấm nút Đặt Hàng
const handleCheckout = () => {
  if (paymentMethod.value === 'VNPAY') {
    alert("Đang chuyển hướng sang cổng thanh toán VNPAY...");
    // Sau này ghép API của Đan (M1), Đan sẽ đưa cho bạn 1 cái link, bạn dùng lệnh:
    // window.location.href = link_vnpay;
  } else {
    alert("Đặt hàng COD thành công! Chúng tôi sẽ giao hàng sớm nhất.");
  }
};
</script>

<style scoped>
.checkout-container { max-width: 600px; margin: 40px auto; font-family: sans-serif; }
.payment-section { margin-top: 30px; padding: 20px; background: #fff; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); }
.payment-section h3 { margin-bottom: 20px; color: #333; font-size: 18px; border-bottom: 2px solid #eee; padding-bottom: 10px; }
.payment-options { display: flex; flex-direction: column; gap: 15px; margin-bottom: 25px; }

/* CSS cho từng Thẻ lựa chọn */
.payment-card {
  display: flex; align-items: center; gap: 15px; padding: 15px 20px;
  border: 2px solid #e0e0e0; border-radius: 8px; cursor: pointer; transition: all 0.3s ease;
}
.payment-card:hover { border-color: #3498db; background-color: #f8fbff; }
.payment-card.active { border-color: #3498db; background-color: #eff6ff; }
.payment-card input[type="radio"] { transform: scale(1.2); accent-color: #3498db; }
.payment-info { display: flex; flex-direction: column; }
.payment-title { font-weight: bold; font-size: 16px; color: #2c3e50; }
.payment-desc { font-size: 13px; color: #7f8c8d; margin-top: 4px; }

/* Nút Đặt hàng */
.btn-order {
  width: 100%; padding: 15px; background-color: #e74c3c; color: white; border: none;
  border-radius: 8px; font-size: 18px; font-weight: bold; cursor: pointer; transition: 0.3s;
}
.btn-order:hover { background-color: #c0392b; }
</style>