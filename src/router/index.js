import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('../views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('../views/category/CategoryList.vue'),
        meta: { title: '菜品分类', icon: 'Menu' }
      },
      {
        path: 'dish',
        name: 'Dish',
        component: () => import('../views/dish/DishList.vue'),
        meta: { title: '菜品管理', icon: 'Dish' }
      },
      {
        path: 'combo',
        name: 'Combo',
        component: () => import('../views/combo/ComboList.vue'),
        meta: { title: '套餐管理', icon: 'Goods' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../views/order/OrderList.vue'),
        meta: { title: '订单管理', icon: 'List' }
      },
      {
        path: 'employee',
        name: 'Employee',
        component: () => import('../views/employee/EmployeeList.vue'),
        meta: { title: '员工管理', icon: 'User' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    next()
  } else {
    if (token) {
      next()
    } else {
      next('/login')
    }
  }
})

export default router
