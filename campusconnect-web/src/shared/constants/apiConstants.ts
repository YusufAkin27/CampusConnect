export const API_BASE_URL = 'http://localhost:8080'
export const AUTH_BASE_PATH = '/v1/api/auth'

export const AUTH_ENDPOINTS = {
  REGISTER: `${AUTH_BASE_PATH}/register`,
  LOGIN: `${AUTH_BASE_PATH}/login`,
  FORGOT_PASSWORD: `${AUTH_BASE_PATH}/forgot-password`,
  RESET_PASSWORD: `${AUTH_BASE_PATH}/reset-password`,
  REFRESH: `${AUTH_BASE_PATH}/refresh-token`,
  LOGOUT: `${AUTH_BASE_PATH}/logout`,
}
