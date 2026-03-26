import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  ControlledBatchItem,
  ControlledMedicineItem,
  ControlledOverview,
  ControlledRecordItem,
  ControlledWarningItem,
} from '@/types/controlled'

export function getControlledOverview() {
  return http.get<ApiResponse<ControlledOverview>>('/controlled/overview')
}

export function getControlledMedicineList(params?: { keyword?: string; status?: number }) {
  return http.get<ApiResponse<ControlledMedicineItem[]>>('/controlled/medicines', { params })
}

export function getControlledBatches(medicineId: number) {
  return http.get<ApiResponse<ControlledBatchItem[]>>(`/controlled/medicines/${medicineId}/batches`)
}

export function getControlledRecords(medicineId: number, params?: { limit?: number }) {
  return http.get<ApiResponse<ControlledRecordItem[]>>(`/controlled/medicines/${medicineId}/records`, { params })
}

export function getControlledWarnings(medicineId: number, params?: { status?: string }) {
  return http.get<ApiResponse<ControlledWarningItem[]>>(`/controlled/medicines/${medicineId}/warnings`, { params })
}
