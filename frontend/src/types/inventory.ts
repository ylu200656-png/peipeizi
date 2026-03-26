export interface InventoryItem {
  id: number
  medicineId: number
  medicineCode: string
  medicineName: string
  specification?: string
  unit?: string
  isControlled: number
  batchNo: string
  currentQuantity: number
  lockedQuantity: number
  availableQuantity: number
  productionDate: string
  expiryDate: string
  lastInboundTime?: string
  lastOutboundTime?: string
  updatedAt?: string
}

export interface InventoryBatchOption {
  medicineId: number
  medicineName: string
  batchNo: string
  availableQuantity: number
  expiryDate: string
  salePrice: number
  isControlled: number
}

export interface InventoryRecordItem {
  id: number
  medicineId: number
  medicineCode: string
  medicineName: string
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

export interface InventoryCheckItemForm {
  medicineId: number | undefined
  batchNo: string
  actualQuantity: number
  reason: string
}

export interface InventoryCheckCreateRequest {
  remark: string
  items: InventoryCheckItemForm[]
}

export interface InventoryCheckItem {
  id: number
  checkId: number
  medicineId: number
  medicineCode: string
  medicineName: string
  batchNo: string
  systemQuantity: number
  actualQuantity: number
  differenceQuantity: number
  reason?: string
}

export interface InventoryCheck {
  id: number
  checkNo: string
  status: 'DRAFT' | 'EXECUTED' | 'CANCELLED'
  remark?: string
  createdBy: number
  createdByName?: string
  executedBy?: number
  executedByName?: string
  createdAt: string
  executedAt?: string
  items: InventoryCheckItem[]
}
