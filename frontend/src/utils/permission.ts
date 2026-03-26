import { computed } from 'vue'
import { useAuthStore } from '@/store/modules/auth'

export function hasAnyRole(roleCodes: string[], allowedRoles: readonly string[]) {
  return allowedRoles.some((role) => roleCodes.includes(role))
}

export function usePermission() {
  const authStore = useAuthStore()

  const roleCodes = computed(() => authStore.roleCodes)
  const canAccess = (allowedRoles: readonly string[]) => hasAnyRole(roleCodes.value, allowedRoles)

  return {
    roleCodes,
    canAccess,
  }
}
