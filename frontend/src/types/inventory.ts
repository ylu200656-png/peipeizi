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
