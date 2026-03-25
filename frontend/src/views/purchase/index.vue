<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getMedicineList, getSuppliers } from '@/api/modules/medicine'
import { createPurchase, getPurchaseList } from '@/api/modules/purchase'
import type { MedicineItem, SupplierItem } from '@/types/medicine'
import type { PurchaseCreateRequest, PurchaseOrder } from '@/types/purchase'

const loading = ref(false)
const submitLoading = ref(false)
const medicines = ref<MedicineItem[]>([])
const suppliers = ref<SupplierItem[]>([])
const purchaseOrders = ref<PurchaseOrder[]>([])

const form = reactive<PurchaseCreateRequest>({
  supplierId: undefined,
  remark: '',
  items: [
    {
      medicineId: undefined,
      batchNo: '',
      quantity: 1,
      purchasePrice: 0,
      productionDate: '',
      expiryDate: '',
    },
  ],
})

const totalAmount = computed(() =>
  form.items.reduce((sum, item) => sum + Number(item.purchasePrice || 0) * Number(item.quantity || 0), 0),
)

async function loadOptions() {
  const [medicineResp, supplierResp] = await Promise.all([
    getMedicineList({ status: 1 }),
    getSuppliers(),
  ])
  medicines.value = medicineResp.data.data
  suppliers.value = supplierResp.data.data
}

async function loadPurchaseOrders() {
  loading.value = true
  try {
    const { data } = await getPurchaseList()
    purchaseOrders.value = data.data
  } finally {
    loading.value = false
  }
}

function addItem() {
  form.items.push({
    medicineId: undefined,
    batchNo: '',
    quantity: 1,
    purchasePrice: 0,
    productionDate: '',
    expiryDate: '',
  })
}

function removeItem(index: number) {
  if (form.items.length === 1) return
  form.items.splice(index, 1)
}

function resetForm() {
  form.supplierId = undefined
  form.remark = ''
  form.items = [
    {
      medicineId: undefined,
      batchNo: '',
      quantity: 1,
      purchasePrice: 0,
      productionDate: '',
      expiryDate: '',
    },
  ]
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    await createPurchase(form)
    ElMessage.success('入库单创建成功')
    resetForm()
    await loadPurchaseOrders()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadPurchaseOrders()
})
</script>

<template>
  <div class="purchase-page">
    <div class="page-card">
      <div class="section-title">新建入库单</div>

      <el-form label-width="110px" class="purchase-form">
        <el-form-item label="供应商">
          <el-select v-model="form.supplierId" placeholder="请选择供应商">
            <el-option
              v-for="item in suppliers"
              :key="item.id"
              :label="item.supplierName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="form.remark" placeholder="可填写本次入库说明" />
        </el-form-item>
      </el-form>

      <div class="item-toolbar">
        <div class="section-subtitle">入库明细</div>
        <el-button type="primary" plain @click="addItem">新增明细</el-button>
      </div>

      <div v-for="(item, index) in form.items" :key="index" class="item-card">
        <div class="item-header">
          <span>明细 {{ index + 1 }}</span>
          <el-button link type="danger" @click="removeItem(index)">删除</el-button>
        </div>

        <div class="item-grid">
          <el-form-item label="药品">
            <el-select v-model="item.medicineId" placeholder="请选择药品">
              <el-option
                v-for="medicine in medicines"
                :key="medicine.id"
                :label="`${medicine.medicineName} (${medicine.medicineCode})`"
                :value="medicine.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="批次号">
            <el-input v-model="item.batchNo" placeholder="请输入批次号" />
          </el-form-item>

          <el-form-item label="数量">
            <el-input-number v-model="item.quantity" :min="1" />
          </el-form-item>

          <el-form-item label="进价">
            <el-input-number v-model="item.purchasePrice" :min="0" :precision="2" />
          </el-form-item>

          <el-form-item label="生产日期">
            <el-date-picker
              v-model="item.productionDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择生产日期"
            />
          </el-form-item>

          <el-form-item label="有效期">
            <el-date-picker
              v-model="item.expiryDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择有效期"
            />
          </el-form-item>
        </div>
      </div>

      <div class="submit-bar">
        <div class="total-amount">合计金额：{{ totalAmount.toFixed(2) }}</div>
        <el-button type="success" :loading="submitLoading" @click="handleSubmit">提交入库</el-button>
      </div>
    </div>

    <div class="page-card">
      <div class="section-title">入库记录</div>
      <el-table :data="purchaseOrders" v-loading="loading" border>
        <el-table-column prop="orderNo" label="入库单号" min-width="170" />
        <el-table-column prop="supplierName" label="供应商" min-width="180" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="totalAmount" label="总金额" width="120" />
        <el-table-column prop="status" label="状态" width="120" />
        <el-table-column prop="createdAt" label="创建时间" min-width="180" />
      </el-table>
    </div>
  </div>
</template>

<style scoped>
.purchase-page {
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

.purchase-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 12px;
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
  color: #0f766e;
}
</style>
