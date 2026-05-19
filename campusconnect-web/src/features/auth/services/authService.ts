import axios from 'axios'
import { authApi } from '../../../api/authApi'
import { tokenStorage } from '../../../shared/utils/tokenStorage'
import type {
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  ResetPasswordRequest,
} from '../types/authTypes'

const NETWORK_ERROR_MESSAGE =
  'Sunucuya bağlanılamadı. API Gateway çalışıyor mu kontrol edin.'

const getErrorMessage = (error: unknown) => {
  if (axios.isAxiosError(error)) {
    if (error.code === 'ERR_NETWORK') {
      return NETWORK_ERROR_MESSAGE
    }

    const message =
      (error.response?.data as { message?: string })?.message ||
      'Bir hata oluştu. Lütfen tekrar deneyin.'

    return message
  }

  return 'Bir hata oluştu. Lütfen tekrar deneyin.'
}

export const authService = {
  login: async (payload: LoginRequest) => {
    try {
      const response = await authApi.login(payload)
      const data = response.data as LoginResponse
      tokenStorage.setTokens(data.accessToken, data.refreshToken)
      return data
    } catch (error) {
      throw new Error(getErrorMessage(error))
    }
  },
  register: async (payload: RegisterRequest) => {
    try {
      await authApi.register(payload)
    } catch (error) {
      throw new Error(getErrorMessage(error))
    }
  },
  forgotPassword: async (payload: ForgotPasswordRequest) => {
    try {
      await authApi.forgotPassword(payload)
    } catch (error) {
      throw new Error(getErrorMessage(error))
    }
  },
  resetPassword: async (payload: ResetPasswordRequest) => {
    try {
      await authApi.resetPassword(payload)
    } catch (error) {
      throw new Error(getErrorMessage(error))
    }
  },
  logout: async () => {
    try {
      await authApi.logout()
    } catch {
      // no-op
    } finally {
      tokenStorage.clearTokens()
    }
  },
}
