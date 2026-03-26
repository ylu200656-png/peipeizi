import type { WarningRecord } from '@/types/warning'

export interface ControlledOverview {
  medicineCount: number
  availableStockTotal: number
  openWarningCount: number
  expiringBatchCount: number
}

export interface ControlledMedicineItem {
  id: number
  medicineCode: string
  medicineName: string
  categoryId: number
  categoryName?: string
  specification?: string
  unit: string
  manufacturer?: string
  supplierId?: number
  supplierName?: string
  purchasePrice: number
  salePrice: number
  safeStock: number
  expiryWarningDays: number
  status: number
  remark?: string
  availableStock: number
  batchCount: number
  openWarningCount: number
}

export interface ControlledBatchItem {
  batchNo: string
  currentQuantity: number
  lockedQuantity: number
  availableQuantity: number
  productionDate: string
  expiryDate: string
  lastInboundTime?: string
  lastOutboundTime?: string
  expired: boolean
  expirySoon: boolean
}

export interface ControlledRecordItem {
  id: number
  batchNo: string
  changeType: 'IN' | 'OUT' | 'ADJUST'
  changeQuantity: number
  beforeQuantity: number
  afterQuantity: number
  sourceType: 'PURCHASE' | 'SALE' | 'CHECK' | 'INIT'
  sourceId?: number
  operatorId?: number
  operatorName?: string
  remark?: string
  createdAt: string
}

export type ControlledWarningItem = WarningRecord
