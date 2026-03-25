<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { getOperationLogList } from '@/api/modules/operation-log'
import type { OperationLogItem } from '@/types/operation-log'

const loading = ref(false)
const logList = ref<OperationLogItem[]>([])
const query = reactive({
  moduleName: '',
  operationType: '',
})

async function loadLogList() {
  loading.value = true
  try {
    const { data } = await getOperationLogList({
      moduleName: query.moduleName || undefined,
      operationType: query.operationType || undefined,
    })
    logList.value = data.data
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.moduleName = ''
  query.operationType = ''
  loadLogList()
}

onMounted(() => {
  loadLogList()
})
</script>

<template>
  <div class="page-card log-page">
    <div class="toolbar">
      <div class="section-title">操作日志</div>
      <div class="toolbar-actions">
        <el-select v-model="query.moduleName" placeholder="模块" clearable>
          <el-option label="采购" value="PURCHASE" />
          <el-option label="销售" value="SALE" />
        </el-select>
        <el-select v-model="query.operationType" placeholder="操作类型" clearable>
          <el-option label="创建" value="CREATE" />
          <el-option label="更新" value="UPDATE" />
        </el-select>
        <el-button type="primary" @click="loadLogList">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </div>
    </div>

    <el-table :data="logList" v-loading="loading" border>
      <el-table-column prop="realName" label="操作人" width="140" />
      <el-table-column prop="moduleName" label="模块" width="120" />
      <el-table-column prop="operationType" label="操作类型" width="120" />
      <el-table-column prop="businessNo" label="业务单号" min-width="180" />
      <el-table-column prop="content" label="内容" min-width="220" />
      <el-table-column prop="ip" label="IP" width="140" />
      <el-table-column prop="createdAt" label="时间" min-width="180" />
    </el-table>
  </div>
</template>

<style scoped>
.log-page {
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
  min-width: 520px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}
</style>
