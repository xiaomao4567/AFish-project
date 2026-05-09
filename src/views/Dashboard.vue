<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <el-icon :size="30"><Dish /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.dishCount }}</div>
            <div class="stat-label">菜品数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <el-icon :size="30"><Goods /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.comboCount }}</div>
            <div class="stat-label">套餐数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <el-icon :size="30"><List /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.orderCount }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
            <el-icon :size="30"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.employeeCount }}</div>
            <div class="stat-label">员工数量</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="welcome-card">
      <h2>欢迎使用餐饮管理系统</h2>
      <p>当前时间：{{ currentTime }}</p>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import axios from '../utils/axios'

const currentTime = ref('')
const stats = reactive({
  dishCount: 0,
  comboCount: 0,
  orderCount: 0,
  employeeCount: 0
})

let timer = null

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const loadStats = async () => {
  try {
    const [dishRes, comboRes, orderRes, employeeRes] = await Promise.all([
      axios.get('/admin/dish', { params: { page: 1, size: 1 } }),
      axios.get('/admin/combo', { params: { page: 1, size: 1 } }),
      axios.get('/admin/order', { params: { page: 1, size: 1 } }),
      axios.get('/admin/employee', { params: { page: 1, size: 1 } })
    ])
    
    stats.dishCount = dishRes.data?.total || 0
    stats.comboCount = comboRes.data?.total || 0
    stats.orderCount = orderRes.data?.total || 0
    stats.employeeCount = employeeRes.data?.total || 0
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadStats()
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #999;
  margin-top: 5px;
}

.welcome-card {
  margin-top: 20px;
  text-align: center;
  padding: 40px;
}

.welcome-card h2 {
  color: #333;
  margin-bottom: 10px;
}

.welcome-card p {
  color: #666;
}
</style>
