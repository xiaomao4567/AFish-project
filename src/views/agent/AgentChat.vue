<template>
  <div class="agent-chat-container">
    <div class="chat-header">
      <div class="header-left">
        <el-icon :size="24" class="robot-icon"><ChatDotRound /></el-icon>
        <div class="title-info">
          <h2>一条鱼AI客服</h2>
          <p>智能点餐助手 · 随时为您服务</p>
        </div>
      </div>
      <div class="header-right">
        <el-tag type="success" class="status-tag">在线</el-tag>
      </div>
    </div>

    <div class="chat-messages" ref="messagesRef">
      <div v-if="messages.length === 0" class="empty-state">
        <el-icon :size="64" class="empty-icon"><ChatDotRound /></el-icon>
        <h3>您好！</h3>
        <p>我是一条鱼AI客服，有什么可以帮助您的？</p>
        <div class="quick-questions">
          <div 
            v-for="(question, index) in quickQuestions" 
            :key="index"
            class="quick-question"
            @click="sendQuickQuestion(question)"
          >
            {{ question }}
          </div>
        </div>
      </div>
      <div v-else>
        <div 
          v-for="message in messages" 
          :key="message.id"
          :class="['message-item', message.role === 'user' ? 'user-message' : 'agent-message']"
        >
          <div class="message-avatar">
            <el-icon v-if="message.role === 'agent'"><ChatDotRound /></el-icon>
            <el-icon v-else><User /></el-icon>
          </div>
          <div class="message-content">
            <div class="message-text">{{ message.content }}</div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>
      </div>
      <div v-if="loading" class="message-item agent-message">
        <div class="message-avatar">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="chat-input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="输入您的问题..."
          @keydown.enter.prevent="handleEnter"
          resize="none"
          class="chat-input"
        />
        <el-button 
          type="primary" 
          :icon="Promotion"
          @click="sendMessage"
          :loading="loading"
          class="send-button"
        >
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, User, Promotion } from '@element-plus/icons-vue'
import axios from '../../utils/axios'

const messagesRef = ref(null)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const sessionId = ref('')

const quickQuestions = [
  '推荐一下招牌菜品',
  '今天有什么优惠活动？',
  '如何下单流程是什么样的？',
  '营业时间是多久？'
]

const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

const sendQuickQuestion = (question) => {
  inputMessage.value = question
  sendMessage()
}

const handleEnter = (event) => {
  if (!event.shiftKey) {
    sendMessage()
  }
}

const sendMessage = async () => {
  const message = inputMessage.value.trim()
  if (!message) {
    return
  }

  const userMessage = {
    id: Date.now(),
    role: 'user',
    content: message,
    timestamp: new Date()
  }
  messages.value.push(userMessage)
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const requestData = {
    message: message
    }
    if (sessionId.value) {
      requestData.sessionId = sessionId.value
    }

    const response = await axios.post('/agent', requestData)
    
    const agentMessage = {
      id: Date.now() + 1,
      role: 'agent',
      content: response.response || '抱歉，我暂时无法回答您的问题。',
      timestamp: new Date()
    }
    
    if (response.sessionId) {
      sessionId.value = response.sessionId
    }
    
    messages.value.push(agentMessage)
  } catch (error) {
    console.error('发送消息失败:', error)
    ElMessage.error('发送消息失败，请稍后重试')
    const errorMessage = {
      id: Date.now() + 1,
      role: 'agent',
      content: '抱歉，网络连接出现问题，请稍后再试。',
      timestamp: new Date()
    }
    messages.value.push(errorMessage)
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

onMounted(() => {
  scrollToBottom()
})
</script>

<style scoped>
.agent-chat-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
  border-radius: 12px;
  overflow: hidden;
}

.chat-header {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.robot-icon {
  color: #fff;
}

.title-info h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #fff;
}

.title-info p {
  margin: 4px 0 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.8);
}

.status-tag {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  text-align: center;
}

.empty-icon {
  color: #FF6B6B;
  margin-bottom: 16px;
}

.empty-state h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #333;
}

.empty-state p {
  margin: 0 0 24px 0;
  color: #666;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  justify-content: center;
  max-width: 500px;
}

.quick-question {
  padding: 10px 20px;
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-question:hover {
  border-color: #FF6B6B;
  color: #FF6B6B;
  background: rgba(255, 107, 107, 0.1);
}

.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.user-message {
  flex-direction: row-reverse;
}

.agent-message {
  flex-direction: row;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.agent-message .message-avatar {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  color: #fff;
}

.user-message .message-avatar {
  background: #4ECDC4;
  color: #fff;
}

.message-content {
  max-width: 70%;
}

.message-text {
  padding: 12px 16px;
  border-radius: 12px;
  font-size: 15px;
  line-height: 1.6;
  word-wrap: break-word;
}

.agent-message .message-text {
  background: #fff;
  color: #333;
  border-top-left-radius: 4px;
}

.user-message .message-text {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  color: #fff;
  border-top-right-radius: 4px;
}

.message-time {
  font-size: 12px;
  color: #999;
  margin-top: 6px;
  padding: 0 4px;
}

.typing-indicator {
  padding: 16px;
  background: #fff;
  border-radius: 12px;
  border-top-left-radius: 4px;
}

.typing-indicator span {
  display: inline-block;
  width: 8px;
  height: 8px;
  background: #999;
  border-radius: 50%;
  margin-right: 4px;
  animation: typing 1.4s infinite;
  animation-fill-mode: both;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0% {
    opacity: 0.4;
  }
  20% {
    opacity: 1;
  }
  100% {
    opacity: 0.4;
  }
}

.chat-input-area {
  background: #fff;
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-textarea__inner) {
  border-radius: 20px;
  resize: none;
}

.send-button {
  height: 76px;
  border-radius: 20px;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%);
  border: none;
  padding: 0 24px;
}
</style>
