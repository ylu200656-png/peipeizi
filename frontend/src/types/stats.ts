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

export interface StatsTrendPoint {
  statDate: string
  purchaseAmount: number
  saleAmount: number
  purchaseOrderCount: number
  saleOrderCount: number
}

export interface StatsInventoryCategoryItem {
  categoryId: number
  categoryName: string
  availableQuantity: number
  batchCount: number
}

export interface StatsWarningDistributionItem {
  warningType: string
  totalCount: number
}
