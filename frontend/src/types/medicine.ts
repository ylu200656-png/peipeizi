export interface MedicineItem {
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
  isControlled: number
  status: number
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface MedicineQuery {
  keyword?: string
  categoryId?: number
  supplierId?: number
  isControlled?: number
  status?: number
}

export interface MedicineCategory {
  id: number
  categoryName: string
  categoryCode: string
  remark?: string
}

export interface SupplierItem {
  id: number
  supplierName: string
  contactPerson?: string
  phone?: string
  address?: string
  status: number
}

export interface MedicineForm {
  medicineCode: string
  medicineName: string
  categoryId: number | undefined
  specification: string
  unit: string
  manufacturer: string
  supplierId: number | undefined
  purchasePrice: number
  salePrice: number
  safeStock: number
  expiryWarningDays: number
  isControlled: number
  status: number
  remark: string
}
