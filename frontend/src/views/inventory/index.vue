<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getInventoryList } from '@/api/modules/inventory'
import type { InventoryItem } from '@/types/inventory'

const loading = ref(false)
const inventoryList = ref<InventoryItem[]>([])
const query = reactive({
  keyword: '',
})

async function loadInventoryList() {
  loading.value = true
  try {
    const { data } = await getInventoryList({
      keyword: query.keyword || undefined,
    })
    inventoryList.value = data.data
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.keyword = ''
  loadInventoryList()
}

function getExpiryTagType(expiryDate: string) {
  const current = new Date().toISOString().slice(0, 10)
  if (expiryDate < current) return 'danger'
  return 'success'
}

onMounted(() => {
  loadInventoryList()
})
</script>

<template>
  <div class="page-card inventory-page">
    <div class="toolbar">
      <div class="section-title">库存列表</div>
      <div class="toolbar-actions">
        <el-input v-model="query.keyword" placeholder="按药品名称、编码、批次搜索" clearable />
        <el-button type="primary" @click="loadInventoryList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
    </div>

    <el-table :data="inventoryList" v-loading="loading" border>
      <el-table-column prop="medicineCode" label="药品编码" width="140" />
      <el-table-column prop="medicineName" label="药品名称" min-width="180" />
      <el-table-column prop="batchNo" label="批次号" min-width="160" />
      <el-table-column prop="currentQuantity" label="当前库存" width="100" />
      <el-table-column prop="lockedQuantity" label="锁定库存" width="100" />
      <el-table-column prop="availableQuantity" label="可用库存" width="100" />
      <el-table-column label="管制药品" width="100">
        <template #default="{ row }">
          <el-tag :type="row.isControlled === 1 ? 'danger' : 'info'">
            {{ row.isControlled === 1 ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="productionDate" label="生产日期" width="120" />
      <el-table-column label="有效期" width="130">
        <template #default="{ row }">
          <el-tag :type="getExpiryTagType(row.expiryDate)">
            {{ row.expiryDate }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastInboundTime" label="最近入库" min-width="170" />
      <el-table-column prop="lastOutboundTime" label="最近出库" min-width="170" />
      <el-table-column prop="updatedAt" label="更新时间" min-width="170" />
    </el-table>
  </div>
</template>

<style scoped>
.inventory-page {
  display: grid;
  gap: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.toolbar-actions {
  display: flex;
  gap: 12px;
  min-width: 480px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}
</style>
