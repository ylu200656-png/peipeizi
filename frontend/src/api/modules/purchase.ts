import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { PurchaseCreateRequest, PurchaseOrder } from '@/types/purchase'

export function getPurchaseList() {
  return http.get<ApiResponse<PurchaseOrder[]>>('/purchases')
}

export function createPurchase(data: PurchaseCreateRequest) {
  return http.post<ApiResponse<PurchaseOrder>>('/purchases', data)
}
