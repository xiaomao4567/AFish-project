<template>
  <div class="dish-list">
    <el-card>
      <template #header>
        <div class="card-header">
          <div class="search-box">
            <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable style="width: 150px; margin-right: 10px;">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
            <el-input v-model="searchForm.name" placeholder="菜品名称" clearable style="width: 200px; margin-right: 10px;" />
            <el-button type="primary" @click="loadData">搜索</el-button>
          </div>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增菜品
          </el-button>
        </div>
      </template>
      
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%;">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
const categories = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品')
const formRef = ref(null)
const editId = ref(null)

const searchForm = reactive({
  categoryId: null,
  name: '',
  page: 1,
  size: 10
})

const form = reactive({
  name: '',
  categoryId: null,
  price: 0,
  description: '',
  sortOrder: 0
})

const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await axios.get('/category')
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败')
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/admin/dish', { params: searchForm })
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
  dialogTitle.value = '新增菜品'
  editId.value = null
  form.name = ''
  form.categoryId = null
  form.price = 0
  form.description = ''
  form.sortOrder = 0
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  editId.value = row.id
  form.name = row.name
  form.categoryId = row.categoryId
  form.price = row.price
  form.description = row.description
  form.sortOrder = row.sortOrder
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (editId.value) {
          await axios.put(`/admin/dish/${editId.value}`, form)
          ElMessage.success('修改成功')
        } else {
          await axios.post('/admin/dish', form)
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
  ElMessageBox.confirm('确定要删除该菜品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await axios.delete(`/admin/dish/${id}`)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await axios.put(`/admin/dish/${row.id}/status`, null, { params: { status: row.status } })
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    row.status = row.status === 1 ? 0 : 1
  }
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.dish-list {
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
