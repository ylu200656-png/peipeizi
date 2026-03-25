export interface SaleCreateItem {
  medicineId: number | undefined
  batchNo: string
  quantity: number
}

export interface SaleCreateRequest {
  remark: string
  items: SaleCreateItem[]
}

export interface SaleOrderItem {
  id: number
  medicineId: number
  medicineName: string
  batchNo: string
  quantity: number
  salePrice: number
  subtotal: number
}

export interface SaleOrder {
  id: number
  orderNo: string
  operatorId: number
  operatorName: string
  totalAmount: number
  status: string
  remark?: string
  createdAt: string
  items: SaleOrderItem[]
}
