import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/modules/auth'
import { pinia } from '@/store'
import { staticRoutes } from './modules/static'

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes,
})

router.beforeEach(async (to) => {
  const authStore = useAuthStore(pinia)

  if (!authStore.initialized) {
    await authStore.bootstrap()
  }

  if (to.meta?.public && authStore.isLoggedIn && to.path === '/login') {
    return '/'
  }

  if (to.meta?.requiresAuth && !authStore.isLoggedIn) {
    return `/login?redirect=${encodeURIComponent(to.fullPath)}`
  }

  const allowedRoles = to.matched
    .map((record) => record.meta?.allowedRoles as string[] | undefined)
    .find((roles) => roles && roles.length > 0)

  if (allowedRoles && !allowedRoles.some((role) => authStore.roleCodes.includes(role))) {
    return '/'
  }

  return true
})

export default router
