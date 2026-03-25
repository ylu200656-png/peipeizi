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
}

export interface WarningSummary {
  openTotal: number
  lowStockCount: number
  expirySoonCount: number
  expiredCount: number
}
