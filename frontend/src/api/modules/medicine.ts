import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  MedicineCategory,
  MedicineForm,
  MedicineItem,
  MedicineQuery,
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
