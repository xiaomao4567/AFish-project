<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="260px" class="aside">
        <div class="logo-area">
          <div class="logo-icon">
            <svg viewBox="0 0 60 60" class="logo-svg">
              <ellipse cx="30" cy="30" rx="20" ry="14" fill="#FF6B6B"/>
              <circle cx="38" cy="28" r="3" fill="#FFFFFF"/>
              <path d="M45 30 Q50 30 50 30" stroke="#FF6B6B" stroke-width="2" fill="none"/>
              <path d="M15 30 Q10 30 10 30" stroke="#FF6B6B" stroke-width="2" fill="none"/>
            </svg>
          </div>
          <div class="logo-text">
            <h1>一条鱼餐厅</h1>
            <p>后台管理系统</p>
          </div>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="main-menu"
          background-color="#1f2937"
          text-color="#a0aec0"
          active-text-color="#fff"
          router
        >
          <el-menu-item v-for="item in menuList" :key="item.path" :index="item.path">
            <el-icon :size="20" class="menu-icon">
              <component :is="item.icon" />
            </el-icon>
            <span class="menu-text">{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
        
        <div class="aside-bottom">
          <div class="version-info">
            <el-icon :size="14"><Setting /></el-icon>
            <span>v1.0.0</span>
          </div>
        </div>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/" class="breadcrumb">
              <el-breadcrumb-item :to="{ path: '/' }">
                <el-icon :size="14"><HomeFilled /></el-icon>
                <span>首页</span>
              </el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <div class="header-actions">
              <el-button type="text" class="action-btn" @click="refresh">
                <el-icon :size="20"><Refresh /></el-icon>
              </el-button>
              <el-button type="text" class="action-btn">
                <el-icon :size="20"><Bell /></el-icon>
              </el-button>
            </div>
            
            <el-dropdown @command="handleCommand" class="user-dropdown">
              <span class="user-info">
                <el-avatar :size="38" class="user-avatar">
                  <el-icon :size="20"><User /></el-icon>
                </el-avatar>
                <div class="user-detail">
                  <span class="username">{{ user?.realName || '管理员' }}</span>
                  <span class="user-role">{{ (user?.role || '').toLowerCase() === 'admin' ? '系统管理员' : '普通员工' }}</span>
                </div>
                <el-icon :size="14" class="arrow-icon"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    <span>个人信息</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    <span>系统设置</span>
                  </el-dropdown-item>
                  <el-dropdown-divider />
                  <el-dropdown-item command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  HomeFilled,
  Menu,
  Dish,
  Goods,
  List,
  User,
  Refresh,
  Bell,
  ArrowDown,
  Setting,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const allMenuList = [
  { path: '/dashboard', title: '数据看板', icon: HomeFilled, roles: ['admin', 'staff'] },
  { path: '/category', title: '菜品分类', icon: Menu, roles: ['admin'] },
  { path: '/dish', title: '菜品管理', icon: Dish, roles: ['admin'] },
  { path: '/combo', title: '套餐管理', icon: Goods, roles: ['admin'] },
  { path: '/order', title: '订单管理', icon: List, roles: ['admin', 'staff'] },
  { path: '/employee', title: '员工管理', icon: User, roles: ['admin'] }
]

const menuList = computed(() => {
  const userRole = (user.value?.role || 'admin').toLowerCase()
  return allMenuList.filter(item => item.roles.includes(userRole))
})

const activeMenu = computed(() => {
  return route.path === '/' ? '/dashboard' : route.path
})

const currentTitle = computed(() => {
  const path = route.path === '/' ? '/dashboard' : route.path
  const item = allMenuList.find(m => m.path === path)
  return item?.title || ''
})

const refresh = () => {
  router.go(0)
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('退出成功！')
      router.push('/login')
    } catch {
      
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
}

.aside {
  background: linear-gradient(180deg, #1f2937 0%, #111827 100%);
  display: flex;
  flex-direction: column;
  box-shadow: 2px 0 20px rgba(0, 0, 0, 0.15);
}

.logo-area {
  display: flex;
  align-items: center;
  padding: 24px 20px;
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2) 0%, rgba(78, 205, 196, 0.2) 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  width: 50px;
  height: 50px;
  margin-right: 14px;
}

.logo-svg {
  width: 100%;
  height: 100%;
}

.logo-text h1 {
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  margin: 0;
}

.logo-text p {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.5);
  margin: 3px 0 0 0;
}

.main-menu {
  flex: 1;
  border-right: none;
  padding: 12px 0;
}

.main-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 16px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.main-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.1);
}

.main-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 100%);
}

.menu-icon {
  margin-right: 12px;
}

.menu-text {
  font-size: 14px;
}

.aside-bottom {
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.version-info {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.35);
  font-size: 12px;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 28px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: 64px;
}

.header-left {
  flex: 1;
}

.breadcrumb :deep(.el-breadcrumb__item) {
  font-size: 14px;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child) {
  color: #333;
  font-weight: 500;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 24px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  transition: all 0.3s ease;
  position: relative;
}

.action-btn:hover {
  background: #f5f5f5;
}

.action-btn .badge {
  position: absolute;
  top: 4px;
  right: 4px;
  min-width: 18px;
  height: 18px;
  padding: 0 6px;
  font-size: 10px;
  line-height: 18px;
  border-radius: 10px;
  background: #FF6B6B;
  color: #fff;
}

.user-dropdown {
  cursor: pointer;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 10px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-avatar {
  margin-right: 12px;
  background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 100%);
}

.user-detail {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.user-role {
  font-size: 12px;
  color: #999;
}

.arrow-icon {
  margin-left: 10px;
  color: #999;
}

.main {
  background: #f3f4f6;
  padding: 24px;
  overflow-y: auto;
}
</style>
