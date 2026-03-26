export interface WarningRecord {
  id: number
  medicineId: number
  medicineCode: string
  medicineName: string
  batchNo?: string
  warningType: string
  warningLevel: string
  warningMessage: string
  status: string
  createdAt: string
  handledAt?: string
  handledBy?: number
  handlerName?: string
  handleRemark?: string
}

export interface WarningSummary {
  openTotal: number
  lowStockCount: number
  expirySoonCount: number
  expiredCount: number
}

export interface WarningResolveRequest {
  handleRemark?: string
}
