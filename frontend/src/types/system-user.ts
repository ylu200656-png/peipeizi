export interface RoleItem {
  id: number
  roleCode: string
  roleName: string
}

export interface UserItem {
  id: number
  username: string
  realName: string
  status: number
  createdAt: string
  updatedAt: string
  roleIds: number[]
  roleCodes: string[]
  roleNames: string[]
}
