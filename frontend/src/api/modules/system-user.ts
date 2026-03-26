import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type {
  CreateUserRequest,
  ResetPasswordRequest,
  RoleItem,
  UpdateUserStatusRequest,
  UserItem,
} from '@/types/system-user'

export function getUserList() {
  return http.get<ApiResponse<UserItem[]>>('/users')
}

export function getRoleList() {
  return http.get<ApiResponse<RoleItem[]>>('/roles')
}

export function createUser(payload: CreateUserRequest) {
  return http.post<ApiResponse<UserItem>>('/users', payload)
}

export function assignUserRoles(userId: number, roleIds: number[]) {
  return http.put<ApiResponse<UserItem>>(`/users/${userId}/roles`, { roleIds })
}

export function updateUserStatus(userId: number, payload: UpdateUserStatusRequest) {
  return http.put<ApiResponse<UserItem>>(`/users/${userId}/status`, payload)
}

export function resetUserPassword(userId: number, payload: ResetPasswordRequest) {
  return http.put<ApiResponse<null>>(`/users/${userId}/reset-password`, payload)
}
