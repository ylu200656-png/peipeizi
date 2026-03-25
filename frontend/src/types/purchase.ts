export interface PurchaseCreateItem {
  medicineId: number | undefined
  batchNo: string
  quantity: number
  purchasePrice: number
  productionDate: string
  expiryDate: string
}

export interface PurchaseCreateRequest {
  supplierId: number | undefined
  remark: string
  items: PurchaseCreateItem[]
}

export interface PurchaseOrderItem {
  id: number
  medicineId: number
  medicineName: string
  batchNo: string
  quantity: number
  purchasePrice: number
  productionDate: string
  expiryDate: string
  subtotal: number
}

export interface PurchaseOrder {
  id: number
  orderNo: string
  supplierId: number
  supplierName: string
  operatorId: number
  operatorName: string
  totalAmount: number
  status: string
  remark?: string
  createdAt: string
  items: PurchaseOrderItem[]
}
