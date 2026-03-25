import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { InventoryBatchOption, InventoryItem } from '@/types/inventory'

export function getInventoryList(params?: { keyword?: string }) {
  return http.get<ApiResponse<InventoryItem[]>>('/inventories', { params })
}

export function getAvailableBatches(medicineId: number) {
  return http.get<ApiResponse<InventoryBatchOption[]>>('/inventories/available-batches', {
    params: { medicineId },
  })
}
