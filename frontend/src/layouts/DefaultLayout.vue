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
  { path: '/medicine', label: '药品与主数据', allowedRoles: MEDICINE_MANAGE_ROLES },
  { path: '/purchase', label: '采购入库', allowedRoles: PURCHASE_MANAGE_ROLES },
  { path: '/sale', label: '销售出库', allowedRoles: ['ADMIN', 'PHARMACY_MANAGER', 'SALES_CLERK'] },
  { path: '/inventory', label: '库存与盘点', allowedRoles: INVENTORY_VIEW_ROLES },
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
      <div class="brand">
        <span class="brand-mark">药</span>
        <div>
          <div class="brand-title">药捷</div>
          <div class="brand-subtitle">医药进销存中台</div>
        </div>
      </div>
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
        <div>
          <div class="toolbar-caption">YAOJIE CONSOLE</div>
          <div class="toolbar-title">{{ $route.meta?.title || '药捷药品管理系统' }}</div>
        </div>
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
  grid-template-columns: 264px 1fr;
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(251, 191, 36, 0.12), transparent 34%),
    linear-gradient(180deg, #f7fafc 0%, #eef4fb 100%);
}

.layout-aside {
  padding: 24px 18px;
  background:
    linear-gradient(180deg, rgba(15, 23, 42, 0.98), rgba(17, 24, 39, 0.96)),
    linear-gradient(120deg, rgba(59, 130, 246, 0.12), transparent);
  color: #f8fafc;
  border-right: 1px solid rgba(148, 163, 184, 0.16);
}

.brand {
  display: flex;
  gap: 14px;
  align-items: center;
  margin-bottom: 26px;
}

.brand-mark {
  display: grid;
  place-items: center;
  width: 44px;
  height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f59e0b, #f97316);
  color: #fff7ed;
  font-size: 22px;
  font-weight: 700;
  box-shadow: 0 10px 24px rgba(249, 115, 22, 0.25);
}

.brand-title {
  font-size: 22px;
  font-weight: 800;
  letter-spacing: 2px;
}

.brand-subtitle {
  margin-top: 3px;
  color: rgba(226, 232, 240, 0.7);
  font-size: 12px;
}

.menu {
  display: grid;
  gap: 10px;
}

.menu-item {
  padding: 11px 14px;
  border-radius: 12px;
  color: rgba(248, 250, 252, 0.84);
  transition: all 0.2s ease;
}

.menu-item:hover {
  background: rgba(59, 130, 246, 0.14);
  color: #ffffff;
}

.menu-item.router-link-active {
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.24), rgba(14, 165, 233, 0.16));
  color: #ffffff;
  box-shadow: inset 0 0 0 1px rgba(96, 165, 250, 0.16);
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

.toolbar-caption {
  font-size: 11px;
  letter-spacing: 1.8px;
  color: #64748b;
}

.toolbar-title {
  margin-top: 4px;
  font-size: 28px;
  font-weight: 800;
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
  border-radius: 999px;
  padding: 8px 14px;
  background: #dbeafe;
  color: #1d4ed8;
  cursor: pointer;
  transition: background 0.2s ease;
}

.logout-button:hover {
  background: #bfdbfe;
}
</style>
