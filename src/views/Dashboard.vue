<template>
  <div class="dashboard">
    <div class="welcome-card">
      <div class="welcome-info">
        <h1>欢迎回来，{{ user?.realName || '管理员' }}！</h1>
        <p>今天是 {{ currentDate }}</p>
      </div>
      <div class="welcome-stats">
        <div class="stat-item">
          <span class="stat-label">今日营收</span>
          <span class="stat-value">¥{{ todayRevenue.toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card dish-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="32"><Dish /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ stats.dishCount }}</div>
              <div class="card-label">菜品数量</div>

            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card combo-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="32"><Goods /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ stats.comboCount }}</div>
              <div class="card-label">套餐数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card order-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="32"><List /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ stats.orderCount }}</div>
              <div class="card-label">今日订单</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card employee-card" shadow="hover">
          <div class="card-content">
            <div class="card-icon">
              <el-icon :size="32"><User /></el-icon>
            </div>
            <div class="card-info">
              <div class="card-value">{{ stats.employeeCount }}</div>
              <div class="card-label">员工数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="content-row">
      <el-col :span="16">
        <el-card class="main-card" shadow="hover">
          <div class="card-header">
            <h3 class="card-title">订单状态统计</h3>
            <div class="card-tabs">
              <el-button
                v-for="tab in tabs"
                :key="tab.value"
                :class="['tab-btn', { active: activeTab === tab.value }]"
                @click="activeTab = tab.value"
              >
                {{ tab.label }}
              </el-button>
            </div>
          </div>
          <div class="order-status-grid">
            <div class="status-item pending">
              <div class="status-icon">
                <el-icon :size="24"><Clock /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-count">{{ orderStats.pending }}</div>
                <div class="status-name">待支付</div>
              </div>
              <div class="status-bar">
                <div class="bar-fill" :style="{ width: getStatusPercent(orderStats.pending) + '%' }"></div>
              </div>
            </div>
            <div class="status-item paid">
              <div class="status-icon">
                <el-icon :size="24"><Wallet /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-count">{{ orderStats.paid }}</div>
                <div class="status-name">已支付</div>
              </div>
              <div class="status-bar">
                <div class="bar-fill" :style="{ width: getStatusPercent(orderStats.paid) + '%' }"></div>
              </div>
            </div>
            <div class="status-item preparing">
              <div class="status-icon">
                <el-icon :size="24"><Timer /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-count">{{ orderStats.preparing }}</div>
                <div class="status-name">备餐中</div>
              </div>
              <div class="status-bar">
                <div class="bar-fill" :style="{ width: getStatusPercent(orderStats.preparing) + '%' }"></div>
              </div>
            </div>
            <div class="status-item finished">
              <div class="status-icon">
                <el-icon :size="24"><CircleCheck /></el-icon>
              </div>
              <div class="status-info">
                <div class="status-count">{{ orderStats.finished }}</div>
                <div class="status-name">已完成</div>
              </div>
              <div class="status-bar">
                <div class="bar-fill" :style="{ width: getStatusPercent(orderStats.finished) + '%' }"></div>
              </div>
            </div>
          </div>
          <div class="recent-orders">
            <h4 class="section-title">最近订单</h4>
            <el-table :data="recentOrders" style="width: 100%" :show-header="false" class="order-table">
              <el-table-column prop="orderNo" width="160" />
              <el-table-column prop="tableNumber" width="100">
                <template #default="{ row }">
                  <el-tag size="small">桌号 {{ row.tableNumber }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="totalAmount" width="120">
                <template #default="{ row }">
                  <span class="amount">¥{{ row.totalAmount.toFixed(2) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="status">
                <template #default="{ row }">
                  <el-tag :type="getStatusTagType(row.status)" size="small">
                    {{ getStatusText(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" width="100" align="right" />
            </el-table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="side-card quick-actions" shadow="hover">
          <h3 class="card-title">快捷操作</h3>
          <div class="action-grid">
            <div class="action-item" @click="goTo('/category')">
              <div class="action-icon category-icon">
                <el-icon :size="28"><Menu /></el-icon>
              </div>
              <span class="action-text">菜品分类</span>
            </div>
            <div class="action-item" @click="goTo('/dish')">
              <div class="action-icon dish-icon">
                <el-icon :size="28"><Dish /></el-icon>
              </div>
              <span class="action-text">菜品管理</span>
            </div>
            <div class="action-item" @click="goTo('/combo')">
              <div class="action-icon combo-icon">
                <el-icon :size="28"><Goods /></el-icon>
              </div>
              <span class="action-text">套餐管理</span>
            </div>
            <div class="action-item" @click="goTo('/order')">
              <div class="action-icon order-icon">
                <el-icon :size="28"><List /></el-icon>
              </div>
              <span class="action-text">订单管理</span>
            </div>
          </div>
        </el-card>

        <el-card class="side-card revenue-card" shadow="hover">
          <h3 class="card-title">今日营收明细</h3>
          <div class="revenue-list">
            <div class="revenue-item">
              <span class="revenue-label">菜品收入</span>
              <span class="revenue-value">¥{{ dishRevenue.toFixed(2) }}</span>
            </div>
            <div class="revenue-item">
              <span class="revenue-label">套餐收入</span>
              <span class="revenue-value">¥{{ comboRevenue.toFixed(2) }}</span>
            </div>
            <div class="revenue-divider"></div>
            <div class="revenue-item total">
              <span class="revenue-label">总计</span>
              <span class="revenue-value">¥{{ todayRevenue.toFixed(2) }}</span>
            </div>
          </div>
        </el-card>

        <el-card class="side-card tips-card" shadow="hover">
          <h3 class="card-title">温馨提示</h3>
          <div class="tips-content">
            <div class="tip-item">
              <el-icon :size="16" class="tip-icon"><Sunny /></el-icon>
              <span>今日有 {{ orderStats.pending }} 笔订单待处理</span>
            </div>


          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '../utils/axios'
import {
  Dish, Goods, List, User, TrendCharts, Minus,
  Clock, Wallet, Timer, CircleCheck,
  Menu, Sunny
} from '@element-plus/icons-vue'

const router = useRouter()
const currentDate = ref('')
const activeTab = ref('today')

const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const stats = reactive({
  dishCount: 48,
  comboCount: 12,
  orderCount: 36,
  employeeCount: 8
})

const orderStats = reactive({
  pending: 3,
  paid: 12,
  preparing: 8,
  finished: 13
})

const todayRevenue = ref(4130.5)
const dishRevenue = ref(2850.5)
const comboRevenue = ref(1280)

const recentOrders = ref([
  { orderNo: 'OF20240108001', tableNumber: 3, totalAmount: 156, status: 'paid', createTime: '14:30' },
  { orderNo: 'OF20240108002', tableNumber: 8, totalAmount: 288, status: 'preparing', createTime: '14:25' },
  { orderNo: 'OF20240108003', tableNumber: 12, totalAmount: 98, status: 'finished', createTime: '14:15' },
  { orderNo: 'OF20240108004', tableNumber: 5, totalAmount: 176, status: 'pending', createTime: '14:10' },
  { orderNo: 'OF20240108005', tableNumber: 2, totalAmount: 320, status: 'paid', createTime: '14:05' }
])

const tabs = [
  { label: '今日', value: 'today' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' }
]

const updateDate = () => {
  const now = new Date()
  currentDate.value = now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
}

const getStatusPercent = (count) => {
  const total = orderStats.pending + orderStats.paid + orderStats.preparing + orderStats.finished
  if (total === 0) return 0
  return Math.round((count / total) * 100)
}

const getStatusTagType = (status) => {
  const types = {
    pending: 'warning',
    paid: 'primary',
    preparing: 'info',
    finished: 'success'
  }
  return types[status] || 'default'
}

const getStatusText = (status) => {
  const texts = {
    pending: '待支付',
    paid: '已支付',
    preparing: '备餐中',
    finished: '已完成'
  }
  return texts[status] || status
}

const goTo = (path) => {
  router.push(path)
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

const loadOrderStats = async () => {
  try {
    const res = await axios.get('/admin/order')
    const orders = res.data?.records || res.data || []
    
    orderStats.pending = orders.filter(o => o.status === 'pending').length
    orderStats.paid = orders.filter(o => o.status === 'paid').length
    orderStats.preparing = orders.filter(o => o.status === 'preparing').length
    orderStats.finished = orders.filter(o => o.status === 'finished').length
    
    recentOrders.value = orders.slice(0, 5).map(order => ({
      orderNo: order.orderNo,
      tableNumber: order.tableNumber,
      totalAmount: order.totalAmount,
      status: order.status,
      createTime: order.createTime ? order.createTime.substring(11, 16) : ''
    }))
  } catch (error) {
    console.error('加载订单数据失败', error)
  }
}

const loadRevenue = async () => {
  try {
    const res = await axios.get('/admin/order')
    const orders = res.data?.records || res.data || []
    const todayOrders = orders.filter(o => o.status === 'paid' || o.status === 'finished')
    
    todayRevenue.value = todayOrders.reduce((sum, o) => sum + (o.totalAmount || 0), 0)
    dishRevenue.value = todayRevenue.value * 0.69
    comboRevenue.value = todayRevenue.value * 0.31
  } catch (error) {
    console.error('加载营收数据失败', error)
  }
}

onMounted(() => {
  updateDate()
  loadStats()
  loadOrderStats()
  loadRevenue()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.welcome-card {
  background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 100%);
  border-radius: 16px;
  padding: 28px 36px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
}

.welcome-info h1 {
  font-size: 26px;
  font-weight: 700;
  margin: 0 0 8px 0;
}

.welcome-info p {
  font-size: 14px;
  opacity: 0.85;
  margin: 0;
}

.welcome-stats {
  text-align: right;
}

.welcome-stats .stat-item {
  display: block;
}

.welcome-stats .stat-label {
  font-size: 13px;
  opacity: 0.8;
  display: block;
}

.welcome-stats .stat-value {
  font-size: 30px;
  font-weight: 700;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  border: none;
}

.stat-card .card-content {
  display: flex;
  align-items: center;
  padding: 8px;
}

.dish-card .card-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.combo-card .card-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.order-card .card-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.employee-card .card-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-icon {
  width: 64px;
  height: 64px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-right: 16px;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
}

.card-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.card-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 6px;
  font-size: 12px;
}

.card-trend.positive {
  color: #67c23a;
}

.card-trend.neutral {
  color: #909399;
}

.content-row {
  margin-bottom: 20px;
}

.main-card {
  border-radius: 12px;
  border: none;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.card-tabs {
  display: flex;
  gap: 8px;
}

.tab-btn {
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 13px;
  border: none;
  background: #f5f5f5;
  color: #666;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background: #e8e8e8;
}

.tab-btn.active {
  background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 100%);
  color: #fff;
}

.order-status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.status-item {
  padding: 20px;
  border-radius: 12px;
  background: #f8fafc;
}

.status-item.pending {
  border-left: 4px solid #e6a23c;
}

.status-item.paid {
  border-left: 4px solid #409eff;
}

.status-item.preparing {
  border-left: 4px solid #909399;
}

.status-item.finished {
  border-left: 4px solid #67c23a;
}

.status-item.pending .status-icon {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.status-item.paid .status-icon {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.status-item.preparing .status-icon {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.status-item.finished .status-icon {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.status-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}

.status-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.status-count {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.status-name {
  font-size: 13px;
  color: #999;
}

.status-bar {
  height: 6px;
  background: #e8e8e8;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #FF6B6B 0%, #4ECDC4 100%);
  transition: width 0.5s ease;
}

.recent-orders {
  margin-top: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 16px 0;
}

.order-table {
  font-size: 13px;
}

.order-table :deep(.el-table__row) {
  height: 48px;
}

.amount {
  font-weight: 600;
  color: #333;
}

.side-card {
  border-radius: 12px;
  border: none;
  margin-bottom: 20px;
}

.quick-actions .card-content {
  padding-bottom: 0;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px 16px;
  border-radius: 12px;
  background: #f8fafc;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
  color: white;
}

.category-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.dish-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.combo-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.order-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.action-text {
  font-size: 13px;
  color: #333;
}

.revenue-list {
  padding: 8px 0;
}

.revenue-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
}

.revenue-item .revenue-label {
  font-size: 14px;
  color: #666;
}

.revenue-item .revenue-value {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.revenue-item.total .revenue-label {
  font-weight: 600;
  color: #333;
}

.revenue-item.total .revenue-value {
  font-size: 20px;
  color: #FF6B6B;
}

.revenue-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 8px 0;
}

.tips-card {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.05) 0%, rgba(78, 205, 196, 0.05) 100%);
  border: 1px solid rgba(255, 107, 107, 0.1);
}

.tips-content {
  padding: 8px 0;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 0;
  font-size: 13px;
  color: #666;
}

.tip-icon {
  color: #FF6B6B;
}
</style>
