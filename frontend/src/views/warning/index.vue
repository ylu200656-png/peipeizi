<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getWarningList, getWarningSummary } from '@/api/modules/warning'
import type { WarningRecord, WarningSummary } from '@/types/warning'

const loading = ref(false)
const warningList = ref<WarningRecord[]>([])
const summary = ref<WarningSummary>({
  openTotal: 0,
  lowStockCount: 0,
  expirySoonCount: 0,
  expiredCount: 0,
})

const query = reactive({
  warningType: '',
  status: 'OPEN',
})

const typeLabelMap: Record<string, string> = {
  LOW_STOCK: '低库存',
  EXPIRY_SOON: '临期',
  EXPIRED: '过期',
}

const levelTypeMap: Record<string, string> = {
  INFO: 'info',
  WARN: 'warning',
  CRITICAL: 'danger',
}

async function loadWarningData() {
  loading.value = true
  try {
    const [listResp, summaryResp] = await Promise.all([
      getWarningList({
        warningType: query.warningType || undefined,
        status: query.status || undefined,
      }),
      getWarningSummary(),
    ])
    warningList.value = listResp.data.data
    summary.value = summaryResp.data.data
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.warningType = ''
  query.status = 'OPEN'
  loadWarningData()
}

function getWarningTypeLabel(type: string) {
  return typeLabelMap[type] || type
}

function getLevelTagType(level: string) {
  return levelTypeMap[level] || 'info'
}

onMounted(() => {
  loadWarningData()
})
</script>

<template>
  <div class="warning-page">
    <div class="summary-grid">
      <div class="page-card">
        <div class="summary-label">开放预警</div>
        <div class="summary-value">{{ summary.openTotal }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">低库存</div>
        <div class="summary-value">{{ summary.lowStockCount }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">临期</div>
        <div class="summary-value">{{ summary.expirySoonCount }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">过期</div>
        <div class="summary-value">{{ summary.expiredCount }}</div>
      </div>
    </div>

    <div class="page-card">
      <div class="toolbar">
        <div class="section-title">预警中心</div>
        <div class="toolbar-actions">
          <el-select v-model="query.warningType" placeholder="预警类型" clearable>
            <el-option label="低库存" value="LOW_STOCK" />
            <el-option label="临期" value="EXPIRY_SOON" />
            <el-option label="过期" value="EXPIRED" />
          </el-select>
          <el-select v-model="query.status" placeholder="状态" clearable>
            <el-option label="开放" value="OPEN" />
            <el-option label="已处理" value="RESOLVED" />
          </el-select>
          <el-button type="primary" @click="loadWarningData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </div>

      <el-table :data="warningList" v-loading="loading" border>
        <el-table-column prop="medicineCode" label="药品编码" width="140" />
        <el-table-column prop="medicineName" label="药品名称" min-width="180" />
        <el-table-column prop="batchNo" label="批次号" min-width="140" />
        <el-table-column label="预警类型" width="110">
          <template #default="{ row }">
            {{ getWarningTypeLabel(row.warningType) }}
          </template>
        </el-table-column>
        <el-table-column label="级别" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.warningLevel)">
              {{ row.warningLevel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="warningMessage" label="预警内容" min-width="260" />
        <el-table-column prop="status" label="状态" width="100" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.warning-page {
  display: grid;
  gap: 16px;
}

.summary-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
}

.summary-label {
  color: #64748b;
  margin-bottom: 10px;
}

.summary-value {
  font-size: 28px;
  font-weight: 700;
  color: #162033;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  min-width: 520px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}
</style>
