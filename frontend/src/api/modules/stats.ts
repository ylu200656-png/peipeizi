import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { DashboardOverview } from '@/types/stats'

export function getDashboardOverview() {
  return http.get<ApiResponse<DashboardOverview>>('/stats/overview')
}
