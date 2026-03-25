export interface LoginRequest {
  username: string
  password: string
}

export interface LoginUser {
  id: number
  username: string
  realName: string
  roles: string[]
}

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  user: LoginUser
}
