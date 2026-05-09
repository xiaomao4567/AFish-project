<template>
  <div class="layout-container">
    <el-container>
      <el-aside width="220px" class="aside">
        <div class="logo">
          <h2>餐饮管理系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item v-for="item in menuList" :key="item.path" :index="'/' + item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" icon="User" />
                <span class="username">{{ user?.realName || '用户' }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
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
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()

const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const menuList = [
  { path: 'dashboard', title: '首页', icon: 'HomeFilled' },
  { path: 'category', title: '菜品分类', icon: 'Menu' },
  { path: 'dish', title: '菜品管理', icon: 'Dish' },
  { path: 'combo', title: '套餐管理', icon: 'Goods' },
  { path: 'order', title: '订单管理', icon: 'List' },
  { path: 'employee', title: '员工管理', icon: 'User' }
]

const activeMenu = computed(() => {
  return route.path
})

const currentTitle = computed(() => {
  const item = menuList.find(m => '/' + m.path === route.path)
  return item?.title || ''
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      ElMessage.success('退出成功')
      router.push('/login')
    })
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
}

.menu {
  border-right: none;
}

.header {
  background: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  color: #333;
}

.main {
  background: #f0f2f5;
  padding: 20px;
}
</style>
