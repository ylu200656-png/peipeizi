import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { SaleCreateRequest, SaleOrder } from '@/types/sale'

export function getSaleList() {
  return http.get<ApiResponse<SaleOrder[]>>('/sales')
}

export function createSale(data: SaleCreateRequest) {
  return http.post<ApiResponse<SaleOrder>>('/sales', data)
}
