export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.trim() || 'http://localhost:8080'
export const AUTH_BASE_PATH = '/v1/api/auth'
export const CONTRACT_BASE_PATH = '/v1/api/contracts'
export const ADMIN_CONTRACT_BASE_PATH = '/v1/api/admin/contracts'

export const AUTH_ENDPOINTS = {
  REGISTER: `${AUTH_BASE_PATH}/register`,
  LOGIN: `${AUTH_BASE_PATH}/login`,
  FORGOT_PASSWORD: `${AUTH_BASE_PATH}/forgot-password`,
  RESET_PASSWORD: `${AUTH_BASE_PATH}/reset-password`,
  REFRESH: `${AUTH_BASE_PATH}/refresh-token`,
  LOGOUT: `${AUTH_BASE_PATH}/logout`,
  ME: `${AUTH_BASE_PATH}/me`,
}

export const CONTRACT_ENDPOINTS = {
  ACTIVE: `${CONTRACT_BASE_PATH}/active`,
  REQUIRED: `${CONTRACT_BASE_PATH}/required`,
  VALIDATE_REQUIRED: `${CONTRACT_BASE_PATH}/validate-required`,
  ACCEPT: `${CONTRACT_BASE_PATH}/accept`,
  ADMIN_DETAIL: (contractId: string) =>
    `${ADMIN_CONTRACT_BASE_PATH}/${contractId}`,
}
