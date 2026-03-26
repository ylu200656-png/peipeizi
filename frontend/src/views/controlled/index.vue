<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getControlledBatches,
  getControlledMedicineList,
  getControlledOverview,
  getControlledRecords,
  getControlledWarnings,
} from '@/api/modules/controlled'
import { useAuthStore } from '@/store/modules/auth'
import type {
  ControlledBatchItem,
  ControlledMedicineItem,
  ControlledOverview,
  ControlledRecordItem,
  ControlledWarningItem,
} from '@/types/controlled'

const authStore = useAuthStore()
const loading = ref(false)
const detailLoading = ref(false)
const drawerVisible = ref(false)
const activeTab = ref('batches')

const overview = ref<ControlledOverview>({
  medicineCount: 0,
  availableStockTotal: 0,
  openWarningCount: 0,
  expiringBatchCount: 0,
})
const medicineList = ref<ControlledMedicineItem[]>([])
const selectedMedicine = ref<ControlledMedicineItem | null>(null)
const batchList = ref<ControlledBatchItem[]>([])
const recordList = ref<ControlledRecordItem[]>([])
const warningList = ref<ControlledWarningItem[]>([])

const query = reactive({
  keyword: '',
  status: undefined as number | undefined,
})

const canViewControlled = computed(() =>
  ['ADMIN', 'PHARMACY_MANAGER', 'INVENTORY_MANAGER'].some((role) => authStore.roleCodes.includes(role)),
)

async function loadPageData() {
  loading.value = true
  try {
    const [overviewResp, medicineResp] = await Promise.all([
      getControlledOverview(),
      getControlledMedicineList({
        keyword: query.keyword || undefined,
        status: query.status,
      }),
    ])
    overview.value = overviewResp.data.data
    medicineList.value = medicineResp.data.data
  } finally {
    loading.value = false
  }
}

async function openDetail(medicine: ControlledMedicineItem) {
  selectedMedicine.value = medicine
  drawerVisible.value = true
  activeTab.value = 'batches'
  detailLoading.value = true
  try {
    const [batchesResp, recordsResp, warningsResp] = await Promise.all([
      getControlledBatches(medicine.id),
      getControlledRecords(medicine.id, { limit: 20 }),
      getControlledWarnings(medicine.id, { status: 'OPEN' }),
    ])
    batchList.value = batchesResp.data.data
    recordList.value = recordsResp.data.data
    warningList.value = warningsResp.data.data
  } finally {
    detailLoading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  query.status = undefined
  loadPageData()
}

function getStatusTagType(status: number) {
  return status === 1 ? 'success' : 'info'
}

function getWarningTagType(count: number) {
  return count > 0 ? 'danger' : 'success'
}

function getBatchTagType(item: ControlledBatchItem) {
  if (item.expired) return 'danger'
  if (item.expirySoon) return 'warning'
  return 'success'
}

function getRecordTagType(type: string) {
  if (type === 'IN') return 'success'
  if (type === 'OUT') return 'warning'
  return 'info'
}

onMounted(() => {
  if (!canViewControlled.value) {
    ElMessage.error('当前用户无权访问管制药品专页')
    return
  }
  loadPageData()
})
</script>

<template>
  <div v-if="canViewControlled" class="controlled-page">
    <div class="summary-grid">
      <div class="page-card">
        <div class="summary-label">管制药品数</div>
        <div class="summary-value">{{ overview.medicineCount }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">可用库存总量</div>
        <div class="summary-value">{{ overview.availableStockTotal }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">开放预警数</div>
        <div class="summary-value">{{ overview.openWarningCount }}</div>
      </div>
      <div class="page-card">
        <div class="summary-label">临期批次数</div>
        <div class="summary-value">{{ overview.expiringBatchCount }}</div>
      </div>
    </div>

    <div class="page-card">
      <div class="toolbar">
        <div class="section-title">管制药品专页</div>
        <div class="toolbar-actions">
          <el-input v-model="query.keyword" placeholder="按药品编码或名称搜索" clearable />
          <el-select v-model="query.status" placeholder="状态" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
          <el-button type="primary" @click="loadPageData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </div>
      </div>

      <el-table :data="medicineList" v-loading="loading" border>
        <el-table-column prop="medicineCode" label="药品编码" width="140" />
        <el-table-column prop="medicineName" label="药品名称" min-width="180" />
        <el-table-column prop="specification" label="规格" width="140" />
        <el-table-column prop="supplierName" label="供应商" min-width="180" />
        <el-table-column prop="availableStock" label="可用库存" width="100" />
        <el-table-column prop="batchCount" label="批次数" width="90" />
        <el-table-column label="开放预警" width="110">
          <template #default="{ row }">
            <el-tag :type="getWarningTagType(row.openWarningCount)">
              {{ row.openWarningCount }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-drawer
      v-model="drawerVisible"
      :title="selectedMedicine ? `${selectedMedicine.medicineName} / ${selectedMedicine.medicineCode}` : '详情'"
      size="55%"
    >
      <div v-if="selectedMedicine" class="drawer-content" v-loading="detailLoading">
        <div class="detail-summary">
          <div class="detail-item">供应商：{{ selectedMedicine.supplierName || '-' }}</div>
          <div class="detail-item">规格：{{ selectedMedicine.specification || '-' }}</div>
          <div class="detail-item">可用库存：{{ selectedMedicine.availableStock }}</div>
          <div class="detail-item">开放预警：{{ selectedMedicine.openWarningCount }}</div>
        </div>

        <el-tabs v-model="activeTab">
          <el-tab-pane label="库存批次" name="batches">
            <el-table :data="batchList" border>
              <el-table-column prop="batchNo" label="批次号" min-width="160" />
              <el-table-column prop="currentQuantity" label="当前库存" width="100" />
              <el-table-column prop="lockedQuantity" label="锁定库存" width="100" />
              <el-table-column prop="availableQuantity" label="可用库存" width="100" />
              <el-table-column label="批次状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="getBatchTagType(row)">
                    {{ row.expired ? '已过期' : row.expirySoon ? '临期' : '正常' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="productionDate" label="生产日期" width="120" />
              <el-table-column prop="expiryDate" label="有效期" width="120" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="出入库记录" name="records">
            <el-table :data="recordList" border>
              <el-table-column prop="batchNo" label="批次号" min-width="150" />
              <el-table-column label="类型" width="100">
                <template #default="{ row }">
                  <el-tag :type="getRecordTagType(row.changeType)">{{ row.changeType }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="changeQuantity" label="变动数量" width="100" />
              <el-table-column prop="beforeQuantity" label="变动前" width="100" />
              <el-table-column prop="afterQuantity" label="变动后" width="100" />
              <el-table-column prop="sourceType" label="来源" width="110" />
              <el-table-column prop="operatorName" label="操作人" width="120" />
              <el-table-column prop="createdAt" label="时间" min-width="180" />
            </el-table>
          </el-tab-pane>

          <el-tab-pane label="预警记录" name="warnings">
            <el-table :data="warningList" border>
              <el-table-column prop="batchNo" label="批次号" min-width="150" />
              <el-table-column prop="warningType" label="预警类型" width="120" />
              <el-table-column prop="warningLevel" label="级别" width="100" />
              <el-table-column prop="warningMessage" label="预警内容" min-width="260" />
              <el-table-column prop="createdAt" label="时间" min-width="180" />
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.controlled-page {
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

.drawer-content {
  display: grid;
  gap: 16px;
}

.detail-summary {
  display: grid;
  gap: 8px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.detail-item {
  color: #334155;
  font-size: 14px;
}
</style>
