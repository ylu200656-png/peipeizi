<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: '123456',
})

async function handleLogin() {
  loading.value = true
  try {
    await authStore.loginAction({
      username: form.username,
      password: form.password,
    })
    ElMessage.success('登录成功')
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : '/'
    await router.replace(redirect)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-shell">
    <div class="login-panel">
      <div class="login-header">
        <div class="login-badge">YAOJIE</div>
        <h1>药捷药品管理系统</h1>
        <p>输入系统账号后进入药品管理后台。</p>
      </div>

      <el-form label-position="top" @submit.prevent="handleLogin">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>

        <el-button type="primary" class="login-button" :loading="loading" @click="handleLogin">
          登录系统
        </el-button>
      </el-form>

      <div class="login-tip">
        测试账号可使用 `admin / 123456`
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
}

.login-panel {
  width: min(440px, 100%);
  padding: 32px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.96);
  border: 1px solid #dbe4f0;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
}

.login-header {
  margin-bottom: 24px;
}

.login-badge {
  display: inline-block;
  margin-bottom: 12px;
  padding: 6px 10px;
  border-radius: 999px;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 1px;
}

.login-header h1 {
  margin: 0 0 10px;
  font-size: 30px;
  color: #162033;
}

.login-header p {
  margin: 0;
  color: #64748b;
}

.login-button {
  width: 100%;
  margin-top: 8px;
}

.login-tip {
  margin-top: 16px;
  color: #64748b;
  font-size: 13px;
}
</style>
