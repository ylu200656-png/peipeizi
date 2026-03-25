import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { RoleItem, UserItem } from '@/types/system-user'

export function getUserList() {
  return http.get<ApiResponse<UserItem[]>>('/users')
}

export function getRoleList() {
  return http.get<ApiResponse<RoleItem[]>>('/roles')
}

export function assignUserRoles(userId: number, roleIds: number[]) {
  return http.put<ApiResponse<UserItem>>(`/users/${userId}/roles`, { roleIds })
}
