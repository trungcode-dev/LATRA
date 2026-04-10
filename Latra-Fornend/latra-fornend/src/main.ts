import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 1. Nhập cái dây cáp Router vào

const app = createApp(App)

app.use(router) // 2. Cắm phích điện! (Báo cho Vue biết là sẽ dùng Router)

app.mount('#app')