import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { OperationLogItem } from '@/types/operation-log'

export function getOperationLogList(params?: { moduleName?: string; operationType?: string }) {
  return http.get<ApiResponse<OperationLogItem[]>>('/operation-logs', { params })
}
