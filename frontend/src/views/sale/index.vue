<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMedicineList } from '@/api/modules/medicine'
import { getAvailableBatches } from '@/api/modules/inventory'
import { createSale, getSaleList } from '@/api/modules/sale'
import type { InventoryBatchOption } from '@/types/inventory'
import type { MedicineItem } from '@/types/medicine'
import type { SaleCreateRequest, SaleOrder } from '@/types/sale'

const loading = ref(false)
const submitLoading = ref(false)
const medicines = ref<MedicineItem[]>([])
const saleOrders = ref<SaleOrder[]>([])
const batchOptionsMap = reactive<Record<number, InventoryBatchOption[]>>({})

const form = reactive<SaleCreateRequest>({
  remark: '',
  items: [
    {
      medicineId: undefined,
      batchNo: '',
      quantity: 1,
    },
  ],
})

const medicineMap = computed(() =>
  medicines.value.reduce<Record<number, MedicineItem>>((acc, item) => {
    acc[item.id] = item
    return acc
  }, {}),
)

const totalAmount = computed(() =>
  form.items.reduce((sum, item) => {
    if (!item.medicineId) return sum
    const batch = getSelectedBatch(item.medicineId, item.batchNo)
    const unitPrice = Number(batch?.salePrice ?? medicineMap.value[item.medicineId]?.salePrice ?? 0)
    return sum + unitPrice * Number(item.quantity || 0)
  }, 0),
)

async function loadMedicines() {
  const { data } = await getMedicineList({ status: 1 })
  medicines.value = data.data
}

async function loadSaleOrders() {
  loading.value = true
  try {
    const { data } = await getSaleList()
    saleOrders.value = data.data
  } finally {
    loading.value = false
  }
}

async function handleMedicineChange(index: number) {
  const item = form.items[index]
  item.batchNo = ''
  if (!item.medicineId || batchOptionsMap[item.medicineId]) {
    return
  }

  const { data } = await getAvailableBatches(item.medicineId)
  batchOptionsMap[item.medicineId] = data.data
}

function getSelectedBatch(medicineId: number, batchNo: string) {
  return batchOptionsMap[medicineId]?.find((item) => item.batchNo === batchNo)
}

function addItem() {
  form.items.push({
    medicineId: undefined,
    batchNo: '',
    quantity: 1,
  })
}

function removeItem(index: number) {
  if (form.items.length === 1) return
  form.items.splice(index, 1)
}

function resetForm() {
  form.remark = ''
  form.items = [
    {
      medicineId: undefined,
      batchNo: '',
      quantity: 1,
    },
  ]
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    await createSale(form)
    ElMessage.success('销售出库成功')
    resetForm()
    await loadSaleOrders()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadMedicines()
  await loadSaleOrders()
})
</script>

<template>
  <div class="sale-page">
    <div class="page-card">
      <div class="section-title">新建销售出库单</div>

      <el-form label-width="110px" class="sale-form">
        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="可填写本次销售说明" />
        </el-form-item>
      </el-form>

      <div class="item-toolbar">
        <div class="section-subtitle">出库明细</div>
        <el-button type="primary" plain @click="addItem">新增明细</el-button>
      </div>

      <div v-for="(item, index) in form.items" :key="index" class="item-card">
        <div class="item-header">
          <span>明细 {{ index + 1 }}</span>
          <el-button link type="danger" @click="removeItem(index)">删除</el-button>
        </div>

        <div class="item-grid">
          <el-form-item label="药品">
            <el-select
              v-model="item.medicineId"
              placeholder="请选择药品"
              @change="handleMedicineChange(index)"
            >
              <el-option
                v-for="medicine in medicines"
                :key="medicine.id"
                :label="`${medicine.medicineName} (${medicine.medicineCode})`"
                :value="medicine.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="批次">
            <el-select
              v-model="item.batchNo"
              placeholder="请选择可售批次"
              :disabled="!item.medicineId"
            >
              <el-option
                v-for="batch in batchOptionsMap[item.medicineId || 0] || []"
                :key="batch.batchNo"
                :label="`${batch.batchNo} / 可售 ${batch.availableQuantity} / 到期 ${batch.expiryDate}`"
                :value="batch.batchNo"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="数量">
            <el-input-number v-model="item.quantity" :min="1" />
          </el-form-item>

          <el-form-item label="单价">
            <el-input
              :model-value="getSelectedBatch(item.medicineId || 0, item.batchNo)?.salePrice ?? medicineMap[item.medicineId || 0]?.salePrice ?? 0"
              disabled
            />
          </el-form-item>
        </div>
      </div>

      <div class="submit-bar">
        <div class="total-amount">合计金额：{{ totalAmount.toFixed(2) }}</div>
        <el-button type="success" :loading="submitLoading" @click="handleSubmit">提交销售单</el-button>
      </div>
    </div>

    <div class="page-card">
      <div class="section-title">销售记录</div>
      <el-table :data="saleOrders" v-loading="loading" border>
        <el-table-column prop="orderNo" label="销售单号" min-width="180" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.sale-page {
  display: grid;
  gap: 16px;
}

.section-title {
  margin-bottom: 18px;
  font-size: 20px;
  font-weight: 700;
  color: #162033;
}

.section-subtitle {
  font-size: 16px;
  font-weight: 700;
  color: #162033;
}

.sale-form {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
}

.item-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 18px 0 12px;
}

.item-card {
  margin-bottom: 12px;
  padding: 16px;
  border-radius: 14px;
  border: 1px solid #dbe4f0;
  background: #f8fbff;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 700;
}

.item-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 12px;
}

.submit-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 18px;
}

.total-amount {
  font-size: 18px;
  font-weight: 700;
  color: #b45309;
}
</style>
