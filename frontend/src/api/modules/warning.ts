import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { WarningRecord, WarningSummary } from '@/types/warning'

export function getWarningList(params?: { warningType?: string; status?: string }) {
  return http.get<ApiResponse<WarningRecord[]>>('/warnings', { params })
}

export function getWarningSummary() {
  return http.get<ApiResponse<WarningSummary>>('/warnings/summary')
}
