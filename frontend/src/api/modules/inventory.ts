import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  InventoryBatchOption,
  InventoryCheck,
  InventoryCheckCreateRequest,
  InventoryItem,
  InventoryRecordItem,
} from '@/types/inventory'

export function getInventoryList(params?: { keyword?: string }) {
  return http.get<ApiResponse<InventoryItem[]>>('/inventories', { params })
}

export function getAvailableBatches(medicineId: number) {
  return http.get<ApiResponse<InventoryBatchOption[]>>('/inventories/available-batches', {
    params: { medicineId },
  })
}

export function getInventoryRecordList(params?: { keyword?: string; sourceType?: string }) {
  return http.get<ApiResponse<InventoryRecordItem[]>>('/inventories/records', { params })
}

export function getInventoryCheckList(params?: { status?: string }) {
  return http.get<ApiResponse<InventoryCheck[]>>('/inventories/checks', { params })
}

export function getInventoryCheckDetail(id: number) {
  return http.get<ApiResponse<InventoryCheck>>(`/inventories/checks/${id}`)
}

export function createInventoryCheck(data: InventoryCheckCreateRequest) {
  return http.post<ApiResponse<InventoryCheck>>('/inventories/checks', data)
}

export function executeInventoryCheck(id: number) {
  return http.post<ApiResponse<InventoryCheck>>(`/inventories/checks/${id}/execute`)
}
