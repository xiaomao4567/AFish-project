// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { requiresAuth: false }
    },
    {
        path: '/',
        component: () => import('../views/Layout.vue'),
        meta: { requiresAuth: true },
        children: [
            {
                path: '',  // 空路径，默认显示首页
                name: 'Dashboard',
                component: () => import('../views/Dashboard.vue'),
                meta: { title: '首页', icon: 'HomeFilled' }
            },
            {
                path: 'dashboard',
                name: 'Dashboard',
                component: () => import('../views/Dashboard.vue'),
                meta: { title: '数据看板', icon: 'HomeFilled' }
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

// 路由守卫
router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

    console.log('=== 路由守卫 ===')
    console.log('目标路径:', to.path)
    console.log('需要登录:', requiresAuth)
    console.log('token存在:', !!token)

    if (requiresAuth && !token) {
        // 需要登录但未登录，跳转登录页
        console.log('未登录，跳转登录页')
        next('/login')
    } else if (to.path === '/login' && token) {
        // 已登录访问登录页，跳转首页
        console.log('已登录，跳转首页')
        next('/')
    } else {
        console.log('放行')
        next()
    }
})

export default router