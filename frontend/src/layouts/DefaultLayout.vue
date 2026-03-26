<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  CONTROLLED_VIEW_ROLES,
  INVENTORY_VIEW_ROLES,
  LOG_VIEW_ROLES,
  MEDICINE_MANAGE_ROLES,
  PURCHASE_MANAGE_ROLES,
  USER_MANAGE_ROLES,
  WARNING_MANAGE_ROLES,
} from '@/constants/permission'
import { useAuthStore } from '@/store/modules/auth'
import { hasAnyRole } from '@/utils/permission'

const router = useRouter()
const authStore = useAuthStore()

const menuItems = [
  { path: '/', label: '首页' },
  { path: '/medicine', label: '药品档案', allowedRoles: MEDICINE_MANAGE_ROLES },
  { path: '/purchase', label: '采购入库', allowedRoles: PURCHASE_MANAGE_ROLES },
  { path: '/sale', label: '销售出库', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER', 'SALES_CLERK'] },
  { path: '/inventory', label: '库存管理', allowedRoles: INVENTORY_VIEW_ROLES },
  { path: '/warning', label: '预警中心', allowedRoles: WARNING_MANAGE_ROLES },
  { path: '/controlled', label: '管制药品', allowedRoles: CONTROLLED_VIEW_ROLES },
  { path: '/stats', label: '统计分析' },
  { path: '/users', label: '用户管理', allowedRoles: USER_MANAGE_ROLES },
  { path: '/logs', label: '操作日志', allowedRoles: LOG_VIEW_ROLES },
]

const visibleMenuItems = computed(() =>
  menuItems.filter((item) => !item.allowedRoles || hasAnyRole(authStore.roleCodes, item.allowedRoles)),
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
