<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { getDashboardOverview } from '@/api/modules/stats'
import { useAuthStore } from '@/store/modules/auth'
import type { DashboardOverview } from '@/types/stats'

const authStore = useAuthStore()
const loading = ref(false)
const overview = ref<DashboardOverview>({
  medicineCount: 0,
  supplierCount: 0,
  inventoryBatchCount: 0,
  availableStockTotal: 0,
  openWarningCount: 0,
  todayPurchaseAmount: 0,
  todaySaleAmount: 0,
  latestWarnings: [],
})

const cards = computed(() => [
  { label: '当前登录用户', value: authStore.user?.realName || authStore.user?.username || '-' },
  { label: '药品总数', value: overview.value.medicineCount },
  { label: '供应商数量', value: overview.value.supplierCount },
  { label: '库存批次数', value: overview.value.inventoryBatchCount },
  { label: '可用库存总量', value: overview.value.availableStockTotal },
  { label: '开放预警数', value: overview.value.openWarningCount },
  { label: '今日采购金额', value: overview.value.todayPurchaseAmount.toFixed(2) },
  { label: '今日销售金额', value: overview.value.todaySaleAmount.toFixed(2) },
])

async function loadOverview() {
  loading.value = true
  try {
    const { data } = await getDashboardOverview()
    overview.value = data.data
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadOverview()
})
</script>

<template>
  <div class="dashboard-page">
    <div class="dashboard-grid">
      <div v-for="card in cards" :key="card.label" class="page-card" v-loading="loading">
        <div class="card-label">{{ card.label }}</div>
        <div class="card-value">{{ card.value }}</div>
      </div>
    </div>

    <div class="page-card">
      <div class="section-title">最近预警</div>
      <el-table :data="overview.latestWarnings" v-loading="loading" border>
        <el-table-column prop="medicineCode" label="药品编码" width="140" />
        <el-table-column prop="medicineName" label="药品名称" min-width="180" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column prop="warningLevel" label="级别" width="100" />
        <el-table-column prop="warningMessage" label="预警内容" min-width="260" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.dashboard-page {
  display: grid;
  gap: 16px;
}

.dashboard-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
}

.card-label {
  color: #64748b;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: 700;
  color: #162033;
}

.section-title {
  margin-bottom: 16px;
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}
</style>
