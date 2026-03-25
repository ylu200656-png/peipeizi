import type { WarningRecord } from '@/types/warning'

export interface DashboardOverview {
  medicineCount: number
  supplierCount: number
  inventoryBatchCount: number
  availableStockTotal: number
  openWarningCount: number
  todayPurchaseAmount: number
  todaySaleAmount: number
  latestWarnings: WarningRecord[]
}
