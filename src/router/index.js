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
            },
            {
                path: 'agent',
                name: 'Agent',
                component: () => import('../views/agent/AgentChat.vue'),
                meta: { title: 'AI客服', icon: 'ChatDotRound' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

const ONE_DAY_MS = 24 * 60 * 60 * 1000

const checkLoginExpired = () => {
    const loginTime = localStorage.getItem('loginTime')
    if (!loginTime) {
        return true
    }
    const now = Date.now()
    const diff = now - parseInt(loginTime)
    return diff > ONE_DAY_MS
}

const clearAuthData = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('loginTime')
}

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

    if (requiresAuth && !token) {
        next('/login')
    } else if (requiresAuth && token) {
        if (checkLoginExpired()) {
            clearAuthData()
            next('/login')
        } else {
            next()
        }
    } else if (to.path === '/login' && token) {
        if (checkLoginExpired()) {
            clearAuthData()
            next()
        } else {
            next('/')
        }
    } else {
        next()
    }
})

export default router