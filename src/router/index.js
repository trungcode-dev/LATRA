import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/admin/dashboard'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/auth/LoginView.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/auth/RegisterView.vue')
    },
    {
      path: '/pos',
      name: 'POS',
      component: () => import('../views/pos/POSView.vue'),
      meta: { requiresAuth: true } 
    },
    {
      path: '/admin',
      component: () => import('../layouts/AdminLayout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          redirect: '/admin/dashboard'
        },
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('../views/admin/DashboardView.vue')
        },
        {
          path: 'products',
          name: 'Products',
          component: () => import('../views/admin/ProductsView.vue')
        }
      ]
    }
  ]
})


router.beforeEach((to, from) => {
  const token = localStorage.getItem('jwt_token');
  const role = localStorage.getItem('user_role'); 


  if (to.meta.requiresAuth && !token) {
    return '/login';
  } 
  else if ((to.path === '/login' || to.path === '/register') && token) {
    if (role === 'ROLE_STAFF') {
      return '/pos';
    } 
    else {
      return '/admin/dashboard';
    }
  } 
})

export default router