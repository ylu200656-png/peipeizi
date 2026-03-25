import http from '@/api/http'
import type { ApiResponse } from '@/types/api'
import type { LoginRequest, LoginResponse, LoginUser } from '@/types/auth'

export function login(data: LoginRequest) {
  return http.post<ApiResponse<LoginResponse>>('/auth/login', data)
}

export function getCurrentUser() {
  return http.get<ApiResponse<LoginUser>>('/auth/me')
}
