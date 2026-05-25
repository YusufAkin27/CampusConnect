import { authApi } from '../../../api/authApi'
import { tokenStorage } from '../../../shared/utils/tokenStorage'
import { getApiErrorMessage } from '../../../utils/apiError'
import type {
  ForgotPasswordRequest,
  LoginRequest,
  AuthResponse,
  RegisterRequest,
  ResetPasswordRequest,
} from '../types/authTypes'

export const authService = {
  login: async (payload: LoginRequest) => {
    try {
      const response = await authApi.login(payload)
      const data = response.data as AuthResponse
      tokenStorage.setTokens(data.accessToken, data.refreshToken, {
        persist: payload.rememberMe ?? true,
      })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  register: async (payload: RegisterRequest) => {
    try {
      const response = await authApi.register(payload)
      const data = response.data as AuthResponse
      tokenStorage.setTokens(data.accessToken, data.refreshToken, {
        persist: true,
      })
      return data
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  forgotPassword: async (payload: ForgotPasswordRequest) => {
    try {
      await authApi.forgotPassword(payload)
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  resetPassword: async (payload: ResetPasswordRequest) => {
    try {
      await authApi.resetPassword(payload)
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  getCurrentUser: async () => {
    try {
      const response = await authApi.me()
      return response.data
    } catch (error) {
      throw new Error(getApiErrorMessage(error))
    }
  },
  logout: async () => {
    try {
      const refreshToken = tokenStorage.getRefreshToken()
      if (refreshToken) {
        await authApi.logout({ refreshToken })
      }
    } catch {
      // no-op
    } finally {
      tokenStorage.clearTokens()
    }
  },
}
