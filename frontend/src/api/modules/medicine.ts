import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  MedicineCategory,
  MedicineCategoryForm,
  MedicineForm,
  MedicineItem,
  MedicineQuery,
  SupplierForm,
  SupplierItem,
} from '@/types/medicine'

export function getMedicineList(params?: MedicineQuery) {
  return http.get<ApiResponse<MedicineItem[]>>('/medicines', { params })
}

export function getMedicineById(id: number) {
  return http.get<ApiResponse<MedicineItem>>(`/medicines/${id}`)
}

export function createMedicine(data: MedicineForm) {
  return http.post<ApiResponse<MedicineItem>>('/medicines', data)
}

export function updateMedicine(id: number, data: MedicineForm) {
  return http.put<ApiResponse<MedicineItem>>(`/medicines/${id}`, data)
}

export function getMedicineCategories() {
  return http.get<ApiResponse<MedicineCategory[]>>('/medicines/categories')
}

export function getSuppliers() {
  return http.get<ApiResponse<SupplierItem[]>>('/medicines/suppliers')
}

export function getCategoryManageList() {
  return http.get<ApiResponse<MedicineCategory[]>>('/medicine-categories')
}

export function createCategory(data: MedicineCategoryForm) {
  return http.post<ApiResponse<MedicineCategory>>('/medicine-categories', data)
}

export function updateCategory(id: number, data: MedicineCategoryForm) {
  return http.put<ApiResponse<MedicineCategory>>(`/medicine-categories/${id}`, data)
}

export function getSupplierManageList() {
  return http.get<ApiResponse<SupplierItem[]>>('/suppliers')
}

export function createSupplier(data: SupplierForm) {
  return http.post<ApiResponse<SupplierItem>>('/suppliers', data)
}

export function updateSupplier(id: number, data: SupplierForm) {
  return http.put<ApiResponse<SupplierItem>>(`/suppliers/${id}`, data)
}
