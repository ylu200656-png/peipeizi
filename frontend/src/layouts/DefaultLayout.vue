<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/store/modules/auth'

const router = useRouter()
const authStore = useAuthStore()

const menuItems = [
  { path: '/', label: '首页' },
  { path: '/medicine', label: '药品档案' },
  { path: '/purchase', label: '采购入库' },
  { path: '/sale', label: '销售出库', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER', 'SALES_CLERK'] },
  { path: '/inventory', label: '库存管理' },
  { path: '/warning', label: '预警中心' },
  { path: '/controlled', label: '管制药品', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER', 'INVENTORY_MANAGER'] },
  { path: '/stats', label: '统计分析' },
  { path: '/users', label: '用户管理', allowedRoles: ['ADMIN'] },
  { path: '/logs', label: '操作日志', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER'] },
]

const visibleMenuItems = computed(() =>
  menuItems.filter((item) => {
    if (!item.allowedRoles?.length) {
      return true
    }
    return item.allowedRoles.some((role) => authStore.roleCodes.includes(role))
  }),
)

const userName = computed(() => authStore.user?.realName || authStore.user?.username || '未登录')

async function handleLogout() {
  await ElMessageBox.confirm('确认退出当前登录状态吗？', '退出登录', {
    type: 'warning',
  })
  authStore.logout()
  await router.push('/login')
}
</script>

<template>
  <div class="layout-shell">
    <aside class="layout-aside">
      <div class="brand">药捷</div>
      <nav class="menu">
        <RouterLink
          v-for="item in visibleMenuItems"
          :key="item.path"
          :to="item.path"
          class="menu-item"
        >
          {{ item.label }}
        </RouterLink>
      </nav>
    </aside>

    <main class="layout-main">
      <div class="layout-toolbar">
        <div class="toolbar-title">{{ $route.meta?.title || '药捷药品管理系统' }}</div>
        <div class="toolbar-user">
          <span>{{ userName }}</span>
          <button class="logout-button" type="button" @click="handleLogout">退出</button>
        </div>
      </div>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.layout-shell {
  display: grid;
  grid-template-columns: 240px 1fr;
  min-height: 100vh;
}

.layout-aside {
  padding: 24px 18px;
  background: linear-gradient(180deg, #0f172a, #162033);
  color: #f8fafc;
}

.brand {
  margin-bottom: 24px;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 2px;
}

.menu {
  display: grid;
  gap: 10px;
}

.menu-item {
  padding: 10px 12px;
  border-radius: 10px;
  color: rgba(248, 250, 252, 0.88);
}

.menu-item.router-link-active {
  background: rgba(59, 130, 246, 0.22);
  color: #ffffff;
}

.layout-main {
  padding: 24px;
}

.layout-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 18px;
}

.toolbar-title {
  font-size: 24px;
  font-weight: 700;
  color: #162033;
}

.toolbar-user {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #475569;
}

.logout-button {
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  background: #dbeafe;
  color: #1d4ed8;
  cursor: pointer;
}
</style>
