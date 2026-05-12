<template>
  <div class="order-list">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
    </div>

    <el-card class="content-card" shadow="hover">
      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="选择订单状态" clearable style="width: 160px">
          <el-option label="全部" :value="null" />
          <el-option label="待支付" value="pending" />
          <el-option label="已支付" value="paid" />
          <el-option label="备餐中" value="preparing" />
          <el-option label="已完成" value="finished" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <el-table :data="orderList" style="width: 100%; margin-top: 16px" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="tableNumber" label="桌号" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.tableNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span style="color: #ff6b6b; font-weight: 600">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showDetail(row)">
              详情
            </el-button>
            <el-button 
              v-if="canUpdateStatus(row)" 
              type="success" 
              link 
              size="small" 
              @click="updateStatus(row)"
            >
              {{ getNextStatusText(row.status) }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="700px">
      <div v-if="currentOrder">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="桌号">{{ currentOrder.tableNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span style="color: #ff6b6b; font-weight: 600">¥{{ currentOrder.totalAmount?.toFixed(2) }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 12px">订单明细</h4>
        <el-table :data="currentOrder.items || []" style="width: 100%">
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column prop="taste" label="口味" width="120" />
          <el-table-column prop="price" label="单价" width="100">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column label="小计" width="120">
            <template #default="{ row }">¥{{ (row.price * row.quantity)?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)

const searchStatus = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const orderList = ref([])
const user = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const isAdmin = computed(() => user.value?.role === 'admin')

const getStatusType = (status) => {
  const types = {
    pending: 'warning',
    paid: 'primary',
    preparing: 'info',
    finished: 'success'
  }
  return types[status] || 'info'
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

const getNextStatusText = (status) => {
  const next = {
    paid: '开始备餐',
    preparing: '完成备餐'
  }
  return next[status] || ''
}

const canUpdateStatus = (row) => {
  return row.status === 'paid' || row.status === 'preparing'
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    if (searchStatus.value) {
      params.status = searchStatus.value
    }
    const res = await axios.get('/admin/order', { params })
    if (res.code === 200) {
      orderList.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const showDetail = async (row) => {
  try {
    const res = await axios.get(`/admin/order/${row.id}`)
    if (res.code === 200) {
      currentOrder.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const updateStatus = async (row) => {
  const nextStatus = row.status === 'paid' ? 'preparing' : 'finished'
  try {
    await axios.put(`/admin/order/${row.id}`, { status: nextStatus })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.order-list {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.content-card {
  border-radius: 12px;
  border: none;
}

.search-bar {
  display: flex;
  align-items: center;
}
</style>
