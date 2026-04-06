import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 1. Nhúng thư mục router vào

const app = createApp(App)

app.use(router) // 2. Bắt buộc phải có dòng này để kích hoạt <router-view>
app.mount('#app')