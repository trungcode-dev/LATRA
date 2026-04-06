<template>
  <div class="dashboard-page">
    <h1>Trang Tổng quan</h1>
    <p class="subtitle">Thống kê hoạt động kinh doanh của cửa hàng</p>
    
    <div class="card-container">
      <div class="card">
        <h3>Tổng Đơn Hàng</h3>
        <p class="number">150</p>
      </div>
      <div class="card">
        <h3>Doanh Thu</h3>
        <p class="number">24.500.000 đ</p>
      </div>
    </div>

    <div class="chart-section">
      <div class="chart-card">
        <h3 class="chart-title">Biểu đồ Doanh thu 7 ngày qua</h3>
        
        <div class="canvas-wrapper">
          <canvas id="revenueChart"></canvas>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import Chart from 'chart.js/auto'; 

onMounted(() => {

  const ctx = document.getElementById('revenueChart');


  new Chart(ctx, {
    type: 'line', // Chọn loại biểu đồ: 'bar' (cột), 'line' (đường), 'pie' (tròn)...
    data: {
      
      labels: ['Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7', 'CN'],
      datasets: [
        {
          label: 'Doanh thu (VNĐ)',
          data: [1500000, 2300000, 1800000, 3200000, 4500000, 6000000, 5200000],
          borderColor: '#000000', 
          backgroundColor: '#000000', 
          pointBackgroundColor: '#ffffff', 
          pointBorderColor: '#000000', 
          pointBorderWidth: 2,
          pointRadius: 5, 
          tension: 0.3
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false, 
      plugins: {
        legend: {
          display: false 
        }
      },
      scales: {
        y: {
          beginAtZero: true 
        }
      }
    }
  });
});
</script>

<style scoped>
.dashboard-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}
.dashboard-page h1 { margin: 0; color: #1e293b; font-size: 24px; }
.subtitle { color: #64748b; margin: 0; font-size: 14px;}

.card-container { display: flex; gap: 24px; }
.card {
  background: white; padding: 24px; border-radius: 12px;
  border: 1px solid #e2e8f0; flex: 1;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
}
.card h3 { margin: 0 0 12px 0; color: #64748b; font-size: 14px; text-transform: uppercase; }
.card .number { font-size: 32px; font-weight: bold; color: #0f172a; margin: 0;}

/* CSS CSS mới cho phần Biểu đồ */
.chart-section {
  display: flex;
  flex-direction: column;
}
.chart-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);
}
.chart-title {
  margin: 0 0 20px 0;
  color: #1e293b;
  font-size: 16px;
}
.canvas-wrapper {
  height: 350px; /* Định hình chiều cao cố định cho biểu đồ */
  width: 100%;
}
</style>