export interface OperationLogItem {
  id: number
  userId?: number
  username?: string
  realName?: string
  moduleName: string
  operationType: string
  businessNo?: string
  content: string
  ip?: string
  createdAt: string
}
