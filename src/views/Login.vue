<template>
  <div class="login-container">
    <div class="bg-gradient"></div>
    <div class="login-content">
      <div class="brand-section">
        <div class="brand-logo">
          <svg viewBox="0 0 100 100" class="fish-icon">
            <ellipse cx="50" cy="50" rx="35" ry="25" fill="#FF6B6B"/>
            <ellipse cx="35" cy="45" rx="8" ry="12" fill="#FFFFFF" opacity="0.3"/>
            <circle cx="60" cy="45" r="5" fill="#FFFFFF"/>
            <path d="M75 50 Q85 50 85 50" stroke="#FF6B6B" stroke-width="3" fill="none"/>
            <path d="M15 50 Q5 50 5 50" stroke="#FF6B6B" stroke-width="3" fill="none"/>
            <path d="M45 65 Q50 75 55 65" stroke="#FFFFFF" stroke-width="2" fill="none" opacity="0.5"/>
          </svg>
        </div>
        <h1 class="brand-title">一条鱼云点餐</h1>
        <p class="brand-subtitle">智能餐饮管理系统</p>
        <div class="feature-tags">
          <span class="tag">扫码点餐</span>
          <span class="tag">数据统计</span>
          <span class="tag">订单管理</span>
        </div>
      </div>

      <div class="login-box">
        <div class="login-header">
          <h2>管理员登录</h2>
          <p>欢迎回到一条鱼餐厅后台</p>
        </div>

        <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
              class="input-item"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              class="input-item"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              <span class="btn-text">{{ loading ? '登录中...' : '立即登录' }}</span>
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p>© 2024 一条鱼云点餐 · 让餐饮更智能</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import axios from '../utils/axios'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await axios.post('/auth/login', form)
        if (res.code === 200 && res.data && res.data.token) {
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('user', JSON.stringify(res.data))
          localStorage.setItem('loginTime', Date.now().toString())
          ElMessage.success('登录成功！')
          router.push('/dashboard')
        } else {
          ElMessage.error(res.message || res.msg || '登录失败：数据格式错误')
        }
      } catch (error) {
        console.error('登录错误:', error)
        ElMessage.error('登录失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  overflow: hidden;
  position: relative;
}

.bg-gradient {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #FF6B6B 0%, #4ECDC4 50%, #45B7D1 100%);
  background-size: 400% 400%;
  animation: gradientMove 15s ease infinite;
}

@keyframes gradientMove {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.login-content {
  position: relative;
  z-index: 10;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 60px;
  padding: 40px;
}

.brand-section {
  text-align: center;
  color: white;
}

.brand-logo {
  margin-bottom: 24px;
}

.fish-icon {
  width: 140px;
  height: 140px;
  animation: fishFloat 3s ease-in-out infinite;
}

@keyframes fishFloat {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-12px) rotate(3deg); }
}

.brand-title {
  font-size: 48px;
  font-weight: 700;
  margin: 0 0 10px 0;
  text-shadow: 2px 2px 12px rgba(0, 0, 0, 0.2);
}

.brand-subtitle {
  font-size: 18px;
  opacity: 0.85;
  margin: 0 0 30px 0;
}

.feature-tags {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.tag {
  padding: 8px 20px;
  background: rgba(255, 255, 255, 0.18);
  backdrop-filter: blur(8px);
  border-radius: 30px;
  font-size: 14px;
}

.login-box {
  width: 420px;
  background: white;
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h2 {
  font-size: 28px;
  color: #333;
  margin: 0 0 8px 0;
}

.login-header p {
  color: #888;
  font-size: 14px;
  margin: 0;
}

.login-form {
  margin-bottom: 24px;
}

.input-item {
  border-radius: 12px;
}

.login-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(255, 107, 107, 0.4);
}

.login-footer {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.login-footer p {
  color: #999;
  font-size: 12px;
  margin: 0;
}
</style>