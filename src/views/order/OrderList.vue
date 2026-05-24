<template>
  <div class="order-list">
    <div class="page-header">
      <h2 class="page-title">订单管理</h2>
    </div>

    <el-card class="content-card" shadow="hover">
      <div class="search-bar">
        <el-select v-model="searchStatus" placeholder="选择订单状态" clearable style="width: 160px">
          <el-option label="全部" :value="null" />
          <el-option label="已支付" value="PAID" />
          <el-option label="备餐中" value="PREPARING" />
          <el-option label="已出餐" value="SERVED" />
          <el-option label="已取消" value="CANCELLED" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <el-table :data="orderList" style="width: 100%; margin-top: 16px" v-loading="loading">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="tableNumber" label="桌号" width="80">
          <template #default="{ row }">
            <el-tag size="small">{{ row.tableNumber }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="100">
          <template #default="{ row }">
            <span style="color: #ff6b6b; font-weight: 600">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="250" fixed="right">
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
            <el-button 
              v-if="canCancelOrder(row)" 
              type="danger" 
              link 
              size="small" 
              @click="showCancelDialog(row)"
            >
              取消订单
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

    <el-dialog v-model="detailVisible" title="订单详情" width="750px">
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
          <el-descriptions-item label="用户ID" :span="1">{{ currentOrder.userId || '-' }}</el-descriptions-item>
          <el-descriptions-item label="下单时间" :span="1">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因" :span="2">
            {{ currentOrder.cancelReason }}
          </el-descriptions-item>
        </el-descriptions>
        
        <h4 style="margin: 20px 0 12px">订单明细</h4>
        <el-table :data="currentOrder.items || []" style="width: 100%">
          <el-table-column label="图片" width="60">
            <template #default="{ row }">
              <el-image :src="row.image" fit="cover" style="width: 40px; height: 40px; border-radius: 4px" />
            </template>
          </el-table-column>
          <el-table-column prop="dishName" label="菜品名称" />
          <el-table-column prop="flavor" label="口味" width="100" />
          <el-table-column prop="price" label="单价" width="90">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="70" />
          <el-table-column label="小计" width="100">
            <template #default="{ row }">¥{{ (row.price * row.quantity)?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <el-dialog v-model="cancelVisible" title="取消订单" width="500px">
      <el-form :model="cancelForm" label-width="80px">
        <el-form-item label="取消原因">
          <el-input 
            v-model="cancelForm.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请输入取消原因" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="cancelVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmCancel" :loading="cancelLoading">
          确认取消
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
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

const cancelVisible = ref(false)
const cancelLoading = ref(false)
const cancelForm = reactive({
  orderId: null,
  reason: ''
})

const getStatusType = (status) => {
  const types = {
    PAID: 'primary',
    PREPARING: 'info',
    SERVED: 'success',
    CANCELLED: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PAID: '已支付',
    PREPARING: '备餐中',
    SERVED: '已出餐',
    CANCELLED: '已取消'
  }
  return texts[status] || status
}

const getNextStatusText = (status) => {
  const next = {
    PAID: '开始备餐',
    PREPARING: '已出餐'
  }
  return next[status] || ''
}

const canUpdateStatus = (row) => {
  return row.status === 'PAID' || row.status === 'PREPARING'
}

const canCancelOrder = (row) => {
  return row.status === 'PAID' || row.status === 'PREPARING'
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
  const nextStatus = row.status === 'PAID' ? 'PREPARING' : 'SERVED'
  try {
    await axios.put(`/admin/order/${row.id}/status?status=${nextStatus}`)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const showCancelDialog = (row) => {
  cancelForm.orderId = row.id
  cancelForm.reason = ''
  cancelVisible.value = true
}

const confirmCancel = async () => {
  if (!cancelForm.reason.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  
  cancelLoading.value = true
  try {
    await axios.put(`/admin/order/${cancelForm.orderId}/cancel`, {
      cancelReason: cancelForm.reason
    })
    ElMessage.success('订单已取消')
    cancelVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    cancelLoading.value = false
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
