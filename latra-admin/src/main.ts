import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// Lưu ý: Đã xóa dòng import style.css của bước Tailwind cũ đi

const app = createApp(App)
app.use(router)
app.mount('#app')