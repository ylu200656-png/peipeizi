import { defineStore } from 'pinia'
import { getCurrentUser, login } from '@/api/modules/auth'
import type { LoginRequest, LoginUser } from '@/types/auth'

interface AuthState {
  token: string
  user: LoginUser | null
  initialized: boolean
}

const TOKEN_KEY = 'accessToken'
const USER_KEY = 'currentUser'

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
    initialized: false,
  }),

  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    roleCodes: (state) => state.user?.roles || [],
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem(TOKEN_KEY, token)
    },

    setUser(user: LoginUser | null) {
      this.user = user
      if (user) {
        localStorage.setItem(USER_KEY, JSON.stringify(user))
      } else {
        localStorage.removeItem(USER_KEY)
      }
    },

    async loginAction(payload: LoginRequest) {
      const { data } = await login(payload)
      this.setToken(data.data.accessToken)
      this.setUser(data.data.user)
      this.initialized = true
      return data.data
    },

    async fetchCurrentUser() {
      const { data } = await getCurrentUser()
      this.setUser(data.data)
      return data.data
    },

    async bootstrap() {
      if (!this.token) {
        this.initialized = true
        return
      }
      if (this.user) {
        this.initialized = true
        return
      }
      try {
        await this.fetchCurrentUser()
      } catch {
        this.logout()
      } finally {
        this.initialized = true
      }
    },

    logout() {
      this.token = ''
      this.user = null
      this.initialized = true
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
  },
})
