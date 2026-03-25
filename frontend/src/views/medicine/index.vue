<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  createMedicine,
  getMedicineCategories,
  getMedicineList,
  getSuppliers,
  updateMedicine,
} from '@/api/modules/medicine'
import { useAuthStore } from '@/store/modules/auth'
import type {
  MedicineCategory,
  MedicineForm,
  MedicineItem,
  SupplierItem,
} from '@/types/medicine'

const authStore = useAuthStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref<number | null>(null)

const medicines = ref<MedicineItem[]>([])
const categories = ref<MedicineCategory[]>([])
const suppliers = ref<SupplierItem[]>([])

const queryForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
  supplierId: undefined as number | undefined,
  isControlled: undefined as number | undefined,
  status: undefined as number | undefined,
})

const defaultForm = (): MedicineForm => ({
  medicineCode: '',
  medicineName: '',
  categoryId: undefined,
  specification: '',
  unit: '盒',
  manufacturer: '',
  supplierId: undefined,
  purchasePrice: 0,
  salePrice: 0,
  safeStock: 0,
  expiryWarningDays: 30,
  isControlled: 0,
  status: 1,
  remark: '',
})

const form = reactive<MedicineForm>(defaultForm())

const canEdit = computed(() =>
  authStore.roleCodes.some((role) => ['ADMIN', 'PHARMACY_MANAGER', 'INVENTORY_MANAGER'].includes(role)),
)

async function loadOptions() {
  const [categoryResp, supplierResp] = await Promise.all([
    getMedicineCategories(),
    getSuppliers(),
  ])
  categories.value = categoryResp.data.data
  suppliers.value = supplierResp.data.data
}

async function loadList() {
  loading.value = true
  try {
    const { data } = await getMedicineList({
      keyword: queryForm.keyword || undefined,
      categoryId: queryForm.categoryId,
      supplierId: queryForm.supplierId,
      isControlled: queryForm.isControlled,
      status: queryForm.status,
    })
    medicines.value = data.data
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  queryForm.keyword = ''
  queryForm.categoryId = undefined
  queryForm.supplierId = undefined
  queryForm.isControlled = undefined
  queryForm.status = undefined
  loadList()
}

function openCreateDialog() {
  Object.assign(form, defaultForm())
  currentId.value = null
  isEdit.value = false
  dialogVisible.value = true
}

function openEditDialog(row: MedicineItem) {
  Object.assign(form, {
    medicineCode: row.medicineCode,
    medicineName: row.medicineName,
    categoryId: row.categoryId,
    specification: row.specification || '',
    unit: row.unit,
    manufacturer: row.manufacturer || '',
    supplierId: row.supplierId,
    purchasePrice: Number(row.purchasePrice),
    salePrice: Number(row.salePrice),
    safeStock: row.safeStock,
    expiryWarningDays: row.expiryWarningDays,
    isControlled: row.isControlled,
    status: row.status,
    remark: row.remark || '',
  })
  currentId.value = row.id
  isEdit.value = true
  dialogVisible.value = true
}

async function handleSubmit() {
  submitLoading.value = true
  try {
    if (isEdit.value && currentId.value) {
      await updateMedicine(currentId.value, form)
      ElMessage.success('药品更新成功')
    } else {
      await createMedicine(form)
      ElMessage.success('药品新增成功')
    }
    dialogVisible.value = false
    await loadList()
  } finally {
    submitLoading.value = false
  }
}

onMounted(async () => {
  await loadOptions()
  await loadList()
})
</script>

<template>
  <div class="medicine-page">
    <div class="page-card">
      <el-form :inline="true" class="query-bar">
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="药品名称 / 编码" clearable />
        </el-form-item>

        <el-form-item label="分类">
          <el-select v-model="queryForm.categoryId" placeholder="全部" clearable style="width: 180px">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="供应商">
          <el-select v-model="queryForm.supplierId" placeholder="全部" clearable style="width: 220px">
            <el-option
              v-for="item in suppliers"
              :key="item.id"
              :label="item.supplierName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="管制药品">
          <el-select v-model="queryForm.isControlled" placeholder="全部" clearable style="width: 140px">
            <el-option label="普通药品" :value="0" />
            <el-option label="管制药品" :value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button v-if="canEdit" type="success" @click="openCreateDialog">新增药品</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="page-card">
      <el-table :data="medicines" v-loading="loading" border>
        <el-table-column prop="medicineCode" label="药品编码" min-width="140" />
        <el-table-column prop="medicineName" label="药品名称" min-width="160" />
        <el-table-column prop="categoryName" label="分类" min-width="120" />
        <el-table-column prop="supplierName" label="供应商" min-width="180" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="purchasePrice" label="进价" width="100" />
        <el-table-column prop="salePrice" label="售价" width="100" />
        <el-table-column prop="safeStock" label="安全库存" width="100" />
        <el-table-column prop="expiryWarningDays" label="临期阈值" width="100" />
        <el-table-column label="管制" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isControlled ? 'danger' : 'info'">
              {{ row.isControlled ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'warning'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canEdit" link type="primary" @click="openEditDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑药品' : '新增药品'" width="760px">
      <el-form label-width="110px" class="medicine-form">
        <el-form-item label="药品编码">
          <el-input v-model="form.medicineCode" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="药品名称">
          <el-input v-model="form.medicineName" />
        </el-form-item>
        <el-form-item label="药品分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.categoryName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格">
          <el-input v-model="form.specification" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input v-model="form.manufacturer" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="form.supplierId" placeholder="请选择供应商" clearable>
            <el-option
              v-for="item in suppliers"
              :key="item.id"
              :label="item.supplierName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="进价">
          <el-input-number v-model="form.purchasePrice" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="售价">
          <el-input-number v-model="form.salePrice" :min="0" :precision="2" :step="1" />
        </el-form-item>
        <el-form-item label="安全库存">
          <el-input-number v-model="form.safeStock" :min="0" />
        </el-form-item>
        <el-form-item label="临期阈值">
          <el-input-number v-model="form.expiryWarningDays" :min="1" />
        </el-form-item>
        <el-form-item label="管制药品">
          <el-radio-group v-model="form.isControlled">
            <el-radio :value="0">否</el-radio>
            <el-radio :value="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.medicine-page {
  display: grid;
  gap: 16px;
}

.query-bar {
  display: flex;
  flex-wrap: wrap;
}

.medicine-form {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 4px 12px;
}

.medicine-form :deep(.el-form-item:last-child) {
  grid-column: 1 / -1;
}
</style>
