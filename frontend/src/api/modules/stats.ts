import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  DashboardOverview,
  StatsInventoryCategoryItem,
  StatsTrendPoint,
  StatsWarningDistributionItem,
} from '@/types/stats'

export function getDashboardOverview() {
  return http.get<ApiResponse<DashboardOverview>>('/stats/overview')
}

export function getTradeTrend(params?: { days?: number }) {
  return http.get<ApiResponse<StatsTrendPoint[]>>('/stats/trend', { params })
}

export function getInventoryCategoryStats() {
  return http.get<ApiResponse<StatsInventoryCategoryItem[]>>('/stats/inventory-category')
}

export function getWarningDistribution() {
  return http.get<ApiResponse<StatsWarningDistributionItem[]>>('/stats/warning-distribution')
}
