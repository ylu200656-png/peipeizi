<script setup lang="ts">
import { BarChart, LineChart, PieChart } from 'echarts/charts'
import {
  GridComponent,
  LegendComponent,
  TooltipComponent,
} from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import { type ECharts, init, use } from 'echarts/core'
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import {
  getDashboardOverview,
  getInventoryCategoryStats,
  getTradeTrend,
  getWarningDistribution,
} from '@/api/modules/stats'
import type {
  DashboardOverview,
  StatsInventoryCategoryItem,
  StatsTrendPoint,
  StatsWarningDistributionItem,
} from '@/types/stats'

const loading = ref(false)
const trendDays = ref(7)
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
const trendList = ref<StatsTrendPoint[]>([])
const inventoryCategoryList = ref<StatsInventoryCategoryItem[]>([])
const warningDistributionList = ref<StatsWarningDistributionItem[]>([])

const tradeChartRef = ref<HTMLDivElement>()
const inventoryChartRef = ref<HTMLDivElement>()
const warningChartRef = ref<HTMLDivElement>()

let tradeChart: ECharts | null = null
let inventoryChart: ECharts | null = null
let warningChart: ECharts | null = null

use([BarChart, LineChart, PieChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

const summaryCards = computed(() => [
  { label: '药品总数', value: overview.value.medicineCount },
  { label: '供应商数', value: overview.value.supplierCount },
  { label: '库存批次数', value: overview.value.inventoryBatchCount },
  { label: '可用库存总量', value: overview.value.availableStockTotal },
  { label: '开放预警数', value: overview.value.openWarningCount },
  { label: '今日采购金额', value: overview.value.todayPurchaseAmount.toFixed(2) },
  { label: '今日销售金额', value: overview.value.todaySaleAmount.toFixed(2) },
])

async function loadPageData() {
  loading.value = true
  try {
    const [overviewResp, trendResp, inventoryResp, warningResp] = await Promise.all([
      getDashboardOverview(),
      getTradeTrend({ days: trendDays.value }),
      getInventoryCategoryStats(),
      getWarningDistribution(),
    ])

    overview.value = overviewResp.data.data
    trendList.value = trendResp.data.data
    inventoryCategoryList.value = inventoryResp.data.data
    warningDistributionList.value = warningResp.data.data

    await nextTick()
    renderCharts()
  } finally {
    loading.value = false
  }
}

function ensureCharts() {
  if (tradeChartRef.value && !tradeChart) {
    tradeChart = init(tradeChartRef.value)
  }
  if (inventoryChartRef.value && !inventoryChart) {
    inventoryChart = init(inventoryChartRef.value)
  }
  if (warningChartRef.value && !warningChart) {
    warningChart = init(warningChartRef.value)
  }
}

function renderCharts() {
  ensureCharts()
  renderTradeChart()
  renderInventoryChart()
  renderWarningChart()
}

function renderTradeChart() {
  if (!tradeChart) {
    return
  }

  tradeChart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['采购金额', '销售金额'] },
    grid: { left: 48, right: 24, top: 48, bottom: 36 },
    xAxis: {
      type: 'category',
      data: trendList.value.map((item) => item.statDate),
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '{value}',
      },
    },
    series: [
      {
        name: '采购金额',
        type: 'bar',
        barMaxWidth: 24,
        itemStyle: { color: '#60a5fa' },
        data: trendList.value.map((item) => item.purchaseAmount),
      },
      {
        name: '销售金额',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#f97316' },
        areaStyle: { color: 'rgba(249, 115, 22, 0.12)' },
        data: trendList.value.map((item) => item.saleAmount),
      },
    ],
  })
}

function renderInventoryChart() {
  if (!inventoryChart) {
    return
  }

  inventoryChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 48, right: 24, top: 24, bottom: 48 },
    xAxis: {
      type: 'category',
      axisLabel: {
        interval: 0,
        rotate: 20,
      },
      data: inventoryCategoryList.value.map((item) => item.categoryName),
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '可用库存',
        type: 'bar',
        barMaxWidth: 32,
        itemStyle: { color: '#10b981' },
        data: inventoryCategoryList.value.map((item) => item.availableQuantity),
      },
    ],
  })
}

function renderWarningChart() {
  if (!warningChart) {
    return
  }

  warningChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        name: '开放预警',
        type: 'pie',
        radius: ['42%', '72%'],
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2,
        },
        data: warningDistributionList.value.map((item) => ({
          name: item.warningType,
          value: item.totalCount,
        })),
      },
    ],
  })
}

function handleResize() {
  tradeChart?.resize()
  inventoryChart?.resize()
  warningChart?.resize()
}

onMounted(async () => {
  await loadPageData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  tradeChart?.dispose()
  inventoryChart?.dispose()
  warningChart?.dispose()
})
</script>

<template>
  <div class="stats-page">
    <div class="toolbar">
      <div>
        <div class="section-title">统计分析</div>
        <div class="section-subtitle">展示采购、销售、库存结构和开放预警的真实数据</div>
      </div>
      <el-segmented
        v-model="trendDays"
        :options="[
          { label: '近7天', value: 7 },
          { label: '近14天', value: 14 },
          { label: '近30天', value: 30 },
        ]"
        @change="loadPageData"
      />
    </div>

    <div class="summary-grid">
      <div v-for="card in summaryCards" :key="card.label" class="page-card" v-loading="loading">
        <div class="card-label">{{ card.label }}</div>
        <div class="card-value">{{ card.value }}</div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="page-card">
        <div class="chart-title">采购与销售趋势</div>
        <div ref="tradeChartRef" class="chart-box"></div>
      </div>

      <div class="page-card">
        <div class="chart-title">库存分类结构</div>
        <div ref="inventoryChartRef" class="chart-box"></div>
      </div>

      <div class="page-card">
        <div class="chart-title">开放预警分布</div>
        <div ref="warningChartRef" class="chart-box"></div>
      </div>
    </div>

    <div class="page-card">
      <div class="chart-title">最近预警</div>
      <el-table :data="overview.latestWarnings" v-loading="loading" border>
        <el-table-column prop="medicineCode" label="药品编码" width="140" />
        <el-table-column prop="medicineName" label="药品名称" min-width="180" />
        <el-table-column prop="batchNo" label="批次号" min-width="160" />
        <el-table-column prop="warningType" label="预警类型" width="120" />
        <el-table-column prop="warningLevel" label="预警级别" width="120" />
        <el-table-column prop="warningMessage" label="预警内容" min-width="260" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.stats-page {
  display: grid;
  gap: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}

.section-subtitle {
  margin-top: 6px;
  color: #64748b;
}

.summary-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
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

.chart-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: 1.4fr 1fr 1fr;
}

.chart-title {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: 700;
  color: #162033;
}

.chart-box {
  height: 320px;
}

@media (max-width: 1200px) {
  .chart-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .toolbar {
    flex-direction: column;
  }
}
</style>
