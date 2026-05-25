import axiosInstance from './axiosInstance'
import { AUTH_ENDPOINTS } from '../shared/constants/apiConstants'
import type {
  ForgotPasswordRequest,
  LoginRequest,
  LogoutRequest,
  RegisterRequest,
  ResetPasswordRequest,
} from '../features/auth/types/authTypes'

export const authApi = {
  register: (payload: RegisterRequest) =>
    axiosInstance.post(AUTH_ENDPOINTS.REGISTER, payload),
  login: (payload: LoginRequest) =>
    axiosInstance.post(AUTH_ENDPOINTS.LOGIN, payload),
  forgotPassword: (payload: ForgotPasswordRequest) =>
    axiosInstance.post(AUTH_ENDPOINTS.FORGOT_PASSWORD, payload),
  resetPassword: (payload: ResetPasswordRequest) =>
    axiosInstance.post(AUTH_ENDPOINTS.RESET_PASSWORD, payload),
  logout: (payload: LogoutRequest) =>
    axiosInstance.post(AUTH_ENDPOINTS.LOGOUT, payload),
  me: () => axiosInstance.get(AUTH_ENDPOINTS.ME),
}
