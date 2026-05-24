<template>
  <div class="combo-list">
    <div class="page-header">
      <h2 class="page-title">套餐管理</h2>
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增套餐
      </el-button>
    </div>

    <el-card class="content-card" shadow="hover">
      <el-table :data="comboList" style="width: 100%" v-loading="loading">
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image :src="row.image || 'https://via.placeholder.com/48'" fit="cover" style="width: 48px; height: 48px; border-radius: 6px" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="套餐名称" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{ row }">
            <span style="color: #ff6b6b; font-weight: 600">¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="包含菜品" width="150">
          <template #default="{ row }">
            <span>{{ row.items?.length || 0 }} 种</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '停用' }}
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
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑套餐' : '新增套餐'"
      width="95%"
      max-width="900px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="图片" prop="image">
          <div class="upload-container">
            <div class="image-preview" v-if="form.image">
              <el-image :src="form.image" fit="cover" style="width: 120px; height: 120px; border-radius: 8px" />
              <div class="image-overlay" @click="form.image = ''">
                <el-icon><Delete /></el-icon>
              </div>
            </div>
            <el-upload
              v-else
              class="image-uploader"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
            >
              <el-icon class="upload-icon"><Plus /></el-icon>
              <div class="upload-text">点击上传</div>
            </el-upload>
            <div class="upload-tip">支持 JPG、PNG 格式，建议尺寸 400x400</div>
          </div>
          <el-input v-if="false" v-model="form.image" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入套餐描述" />
        </el-form-item>
        <el-form-item label="包含菜品" prop="items">
          <div class="items-section">
            <div class="items-header">
              <span>菜品列表</span>
              <el-button type="primary" size="small" @click="showDishSelector">
                <el-icon><Plus /></el-icon>
                添加菜品
              </el-button>
            </div>
            <el-table :data="form.items" style="width: 100%; margin-top: 10px" v-if="form.items.length > 0">
              <el-table-column label="图片" width="60">
                <template #default="{ row }">
                  <el-image :src="row.image || 'https://via.placeholder.com/32'" fit="cover" style="width: 32px; height: 32px; border-radius: 4px" />
                </template>
              </el-table-column>
              <el-table-column prop="dishName" label="菜品名称" min-width="100" />
              <el-table-column prop="flavor" label="口味" min-width="80">
                <template #default="{ row }">
                  <span>{{ row.flavor || '默认' }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="price" label="单价" width="80" align="right">
                <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
              </el-table-column>
              <el-table-column prop="quantity" label="数量" width="80">
                <template #default="{ row, $index }">
                  <el-input-number v-model="form.items[$index].quantity" :min="1" style="width: 70px" />
                </template>
              </el-table-column>
              <el-table-column label="小计" width="80" align="right">
                <template #default="{ row }">¥{{ (row.price * row.quantity)?.toFixed(2) }}</template>
              </el-table-column>
              <el-table-column label="操作" width="60">
                <template #default="{ $index }">
                  <el-button type="danger" link size="small" @click="removeItem($index)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div v-else class="empty-tip">
              <el-icon :size="48" style="color: #ccc"><ShoppingBag /></el-icon>
              <p>请添加套餐包含的菜品</p>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="dishSelectorVisible"
      title="选择菜品"
      width="700px"
    >
      <div class="dish-selector">
        <el-input v-model="dishSearchKeyword" placeholder="搜索菜品名称" style="width: 240px; margin-bottom: 16px" clearable />
        <el-table :data="dishList" style="width: 100%" v-loading="dishLoading" @row-click="selectDish">
          <el-table-column label="图片" width="80">
            <template #default="{ row }">
              <el-image :src="row.image || 'https://via.placeholder.com/40'" fit="cover" style="width: 40px; height: 40px; border-radius: 4px" />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="flavors" label="可选口味" width="150">
            <template #default="{ row }">
              <span>{{ row.flavors?.join(', ') || '无' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="price" label="单价" width="100" align="right">
            <template #default="{ row }">¥{{ row.price?.toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </div>
      <template #footer>
        <el-button @click="dishSelectorVisible = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="flavorSelectorVisible"
      title="选择口味"
      width="400px"
    >
      <el-form :model="flavorForm" label-width="80px">
        <el-form-item label="菜品">
          <span>{{ selectedDish?.name }}</span>
        </el-form-item>
        <el-form-item label="口味">
          <el-select v-model="flavorForm.flavor" placeholder="请选择口味" style="width: 100%">
            <el-option label="默认" value="" />
            <el-option v-for="f in selectedDish?.flavors" :key="f" :label="f" :value="f" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="flavorForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="flavorSelectorVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmFlavor">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, ShoppingBag, Delete } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const uploading = ref(false)

const dishSelectorVisible = ref(false)
const flavorSelectorVisible = ref(false)
const dishLoading = ref(false)
const dishSearchKeyword = ref('')

const comboList = ref([])
const dishList = ref([])
const selectedDish = ref(null)

const flavorForm = reactive({
  flavor: '',
  quantity: 1
})

const form = reactive({
  id: null,
  name: '',
  image: '',
  price: 0,
  description: '',
  items: [],
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  items: [{ required: true, message: '请至少添加一个菜品', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await axios.get('/admin/combo')
    if (res.code === 200) {
      comboList.value = res.data?.records || res.data || []
    }
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const loadDishes = async () => {
  dishLoading.value = true
  try {
    const res = await axios.get('/admin/dish', {
      params: {
        page: 1,
        size: 100,
        name: dishSearchKeyword.value
      }
    })
    if (res.code === 200) {
      dishList.value = res.data?.records || res.data || []
    }
  } catch (error) {
    ElMessage.error('加载菜品失败')
  } finally {
    dishLoading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { id: null, name: '', image: '', price: 0, description: '', items: [], status: 1 })
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name,
    image: row.image,
    price: row.price,
    description: row.description,
    items: row.items ? [...row.items] : [],
    status: row.status
  })
  dialogVisible.value = true
}

const beforeImageUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/jpg'
  const isLt5M = file.size / 1024 / 1024 < 5

  if (!isImage) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB!')
    return false
  }
  return true
}

const handleImageUpload = async (options) => {
  const { file } = options
  uploading.value = true
  
  try {
    const formData = new FormData()
    formData.append('file', file)
    
    const res = await axios.post('/admin/common/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    if (res.code === 200) {
      form.image = res.data
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.message || '图片上传失败')
    }
  } catch (error) {
    ElMessage.error('图片上传失败')
    console.error('上传失败:', error)
  } finally {
    uploading.value = false
  }
}

const showDishSelector = () => {
  loadDishes()
  dishSelectorVisible.value = true
}

const selectDish = (row) => {
  if (row.status !== 1) {
    ElMessage.warning('只能选择已上架的菜品')
    return
  }
  selectedDish.value = row
  flavorForm.flavor = ''
  flavorForm.quantity = 1
  dishSelectorVisible.value = false
  flavorSelectorVisible.value = true
}

const confirmFlavor = () => {
  if (!selectedDish.value) return
  
  const existingItem = form.items.find(item => item.dishId === selectedDish.value.id && item.flavor === flavorForm.flavor)
  if (existingItem) {
    existingItem.quantity += flavorForm.quantity
  } else {
    form.items.push({
      dishId: selectedDish.value.id,
      dishName: selectedDish.value.name,
      image: selectedDish.value.image,
      flavor: flavorForm.flavor,
      price: selectedDish.value.price,
      quantity: flavorForm.quantity
    })
  }
  
  flavorSelectorVisible.value = false
  selectedDish.value = null
}

const removeItem = (index) => {
  form.items.splice(index, 1)
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const submitData = {
          ...form,
          items: form.items.map(item => ({
            dishId: item.dishId,
            flavor: item.flavor,
            quantity: item.quantity
          }))
        }
        
        if (isEdit.value) {
          await axios.put(`/admin/combo/${form.id}`, submitData)
          ElMessage.success('编辑成功')
        } else {
          await axios.post('/admin/combo', submitData)
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
    const items = row.items ? row.items.map(item => ({
      dishId: item.dishId,
      flavor: item.flavor,
      quantity: item.quantity
    })) : []
    
    await axios.put(`/admin/combo/${row.id}`, {
      id: row.id,
      name: row.name,
      image: row.image,
      price: row.price,
      description: row.description,
      items: items,
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
    await ElMessageBox.confirm('确定要删除该套餐吗？', '提示', {
      type: 'warning'
    })
    await axios.delete(`/admin/combo/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.combo-list {
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

.items-section {
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 12px;
}

.items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.empty-tip {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #999;
}

.empty-tip p {
  margin-top: 12px;
  margin-bottom: 0;
}

.dish-selector {
  max-height: 400px;
  overflow-y: auto;
}

.upload-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.image-preview {
  position: relative;
  width: 120px;
  height: 120px;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
  border-radius: 8px;
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.image-overlay .el-icon {
  font-size: 24px;
  color: white;
}

.image-uploader {
  width: 120px;
  height: 120px;
  border: 2px dashed #d9d9d9;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.image-uploader:hover {
  border-color: #409eff;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}

.upload-text {
  font-size: 12px;
  color: #8c939d;
  margin-top: 4px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
}
</style>