<template>
  <div class="combo-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>套餐管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增套餐
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="套餐名称" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="包含菜品" prop="items">
          <el-select v-model="form.items" multiple placeholder="请选择菜品" style="width: 100%;">
            <el-option v-for="item in dishes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from '../../utils/axios'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const dishes = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增套餐')
const formRef = ref(null)
const editId = ref(null)

const searchForm = reactive({
  page: 1,
  size: 10
})

const form = reactive({
  name: '',
  price: 0,
  description: '',
  items: []
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadDishes = async () => {
  try {
    const res = await axios.get('/admin/dish', { params: { page: 1, size: 100 } })
    if (res.code === 200) {
      dishes.value = res.data?.records || []
    }
  } catch (error) {
    console.error('加载菜品失败')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/admin/combo', { params: searchForm })
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

const handleAdd = () => {
  dialogTitle.value = '新增套餐'
  editId.value = null
  form.name = ''
  form.price = 0
  form.description = ''
  form.items = []
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑套餐'
  editId.value = row.id
  form.name = row.name
  form.price = row.price
  form.description = row.description
  try {
    const res = await axios.get(`/admin/combo/${row.id}`)
    if (res.code === 200) {
      form.items = res.data?.items?.map(item => item.dishId) || []
    }
  } catch (error) {
    form.items = []
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const submitData = {
          name: form.name,
          price: form.price,
          description: form.description,
          items: form.items.map(dishId => ({
            dishId: dishId,
            quantity: 1
          }))
        }
        if (editId.value) {
          await axios.put(`/admin/combo/${editId.value}`, submitData)
          ElMessage.success('修改成功')
        } else {
          await axios.post('/admin/combo', submitData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该套餐吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/admin/combo/${id}`)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

onMounted(() => {
  loadDishes()
  loadData()
})
</script>

<style scoped>
.combo-list {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>