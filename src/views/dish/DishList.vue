<template>
  <div class="dish-list">
    <div class="page-header">
      <h2 class="page-title">菜品管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增菜品
      </el-button>
    </div>

    <el-card class="content-card" shadow="hover">
      <div class="search-bar">
        <el-input v-model="searchKeyword" placeholder="搜索菜品名称" style="width: 240px" clearable />
        <el-select v-model="searchCategoryId" placeholder="选择分类" clearable style="width: 160px; margin-left: 12px">
          <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-button type="primary" style="margin-left: 12px" @click="loadData">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
      </div>

      <el-table :data="dishList" style="width: 100%; margin-top: 16px" v-loading="loading">
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image :src="row.image || 'https://via.placeholder.com/48'" fit="cover" style="width: 48px; height: 48px; border-radius: 6px" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="flavors" label="口味" width="150" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span style="color: #ff6b6b; font-weight: 600">¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button type="success" link size="small" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑菜品' : '新增菜品'"
      width="600px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <el-input v-model="form.image" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="口味" prop="flavors">
          <el-input v-model="form.flavors" placeholder="多种口味用逗号分隔" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入菜品描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">上架</el-radio>
            <el-radio :value="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const searchKeyword = ref('')
const searchCategoryId = ref(null)
const page = ref(1)
const size = ref(10)
const total = ref(0)

const dishList = ref([])
const categoryList = ref([])

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  image: '',
  price: 0,
  flavors: '',
  description: '',
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
}

const loadCategories = async () => {
  try {
    const res = await axios.get('/admin/category')
    if (res.code === 200) {
      categoryList.value = res.data || []
    }
  } catch (error) {
    console.error('加载分类失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/admin/dish', {
      params: {
        page: page.value,
        size: size.value,
        name: searchKeyword.value,
        categoryId: searchCategoryId.value
      }
    })
    if (res.code === 200) {
      dishList.value = res.data?.records || []
      total.value = res.data?.total || 0
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    name: '',
    categoryId: null,
    image: '',
    price: 0,
    flavors: '',
    description: '',
    status: 1
  })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  if (Array.isArray(row.flavors)) {
    form.flavors = row.flavors.join(',')
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = { ...form }
        if (Array.isArray(submitData.flavors)) {
          submitData.flavors = submitData.flavors.join(',')
        }
        if (isEdit.value) {
          await axios.put(`/admin/dish/${form.id}`, submitData)
          ElMessage.success('编辑成功')
        } else {
          await axios.post('/admin/dish', submitData)
          ElMessage.success('添加成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const toggleStatus = async (row) => {
  try {
    await axios.put(`/admin/dish/${row.id}`, {
      id: row.id,
      name: row.name,
      categoryId: row.categoryId,
      image: row.image,
      price: row.price,
      flavors: Array.isArray(row.flavors) ? row.flavors.join(',') : row.flavors,
      description: row.description,
      status: row.status === 1 ? 0 : 1
    })
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该菜品吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/admin/dish/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
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

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
