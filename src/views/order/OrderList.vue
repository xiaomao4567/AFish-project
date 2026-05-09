<template>
  <div class="order-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-box">
            <el-select v-model="searchForm.status" placeholder="订单状态" clearable style="width: 150px; margin-right: 10px;">
              <el-option label="待支付" value="待支付" />
              <el-option label="已支付" value="已支付" />
              <el-option label="待出餐" value="待出餐" />
              <el-option label="已出餐" value="已出餐" />
              <el-option label="已完成" value="已完成" />
            </el-select>
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="tableNumber" label="桌号" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            <span style="color: #f56c6c;">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === '已支付'" type="success" link @click="handleStatus(row.id, '待出餐')">接单</el-button>
            <el-button v-if="row.status === '待出餐'" type="warning" link @click="handleStatus(row.id, '已出餐')">出餐</el-button>
            <el-button v-if="row.status === '已出餐'" type="info" link @click="handleStatus(row.id, '已完成')">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="searchForm.page"
        v-model:page-size="searchForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detailData.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="桌号">{{ detailData.tableNumber }}</el-descriptions-item>
        <el-descriptions-item label="总金额">¥{{ detailData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <h4 style="margin: 20px 0 10px;">订单菜品</h4>
      <el-table :data="detailData.items" border size="small">
        <el-table-column prop="dishName" label="菜品名称" />
        <el-table-column prop="flavor" label="口味" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '../../utils/axios'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const detailVisible = ref(false)
const detailData = ref({})

const searchForm = reactive({
  status: '',
  page: 1,
  size: 10
})

const getStatusType = (status) => {
  const map = {
    '待支付': 'info',
    '已支付': 'warning',
    '待出餐': 'primary',
    '已出餐': 'success',
    '已完成': 'success'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/admin/order', { params: searchForm })
    if (res.code === 200) {
      tableData.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleDetail = async (row) => {
  try {
    const res = await axios.get(`/admin/order/${row.id}`)
    if (res.code === 200) {
      detailData.value = res.data
      detailVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const handleStatus = async (id, status) => {
  try {
    await axios.put(`/admin/order/${id}/status`, null, { params: { status } })
    ElMessage.success('状态更新成功')
    loadData()
  } catch (error) {
    ElMessage.error('状态更新失败')
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

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-box {
  display: flex;
  align-items: center;
}
</style>
