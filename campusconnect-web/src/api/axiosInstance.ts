import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios'
import { AUTH_ENDPOINTS, API_BASE_URL } from '../shared/constants/apiConstants'
import { tokenStorage } from '../shared/utils/tokenStorage'

const defaultHeaders = {
  'Content-Type': 'application/json',
}

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: defaultHeaders,
})

const plainAxios = axios.create({
  baseURL: API_BASE_URL,
  headers: defaultHeaders,
})

type RetryConfig = InternalAxiosRequestConfig & {
  _retry?: boolean
  skipAuth?: boolean
  skipRefresh?: boolean
}

let isRefreshing = false
let refreshQueue: Array<{
  resolve: (token: string) => void
  reject: (error: AxiosError) => void
}> = []

const processQueue = (error: AxiosError | null, token: string | null) => {
  refreshQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
      return
    }
    if (token) {
      resolve(token)
    }
  })
  refreshQueue = []
}

axiosInstance.interceptors.request.use((config) => {
  const typedConfig = config as RetryConfig
  if (!typedConfig.skipAuth) {
    const token = tokenStorage.getAccessToken()
    if (token) {
      typedConfig.headers.Authorization = `Bearer ${token}`
    }
  }
  return typedConfig
})

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const config = error.config as RetryConfig | undefined
    if (!config || config.skipRefresh) {
      return Promise.reject(error)
    }

    if (error.response?.status !== 401 || config._retry) {
      return Promise.reject(error)
    }

    const refreshToken = tokenStorage.getRefreshToken()
    if (!refreshToken) {
      tokenStorage.clearTokens()
      window.location.assign('/login')
      return Promise.reject(error)
    }

    if (isRefreshing) {
      return new Promise((resolve, reject) => {
        refreshQueue.push({ resolve, reject })
      }).then((token) => {
        config.headers.Authorization = `Bearer ${token}`
        return axiosInstance(config)
      })
    }

    config._retry = true
    isRefreshing = true

    try {
      const response = await plainAxios.post(AUTH_ENDPOINTS.REFRESH, {
        refreshToken,
      })
      const { accessToken, refreshToken: newRefreshToken } = response.data as {
        accessToken: string
        refreshToken: string
      }

      tokenStorage.setTokens(accessToken, newRefreshToken)
      processQueue(null, accessToken)
      config.headers.Authorization = `Bearer ${accessToken}`
      return axiosInstance(config)
    } catch (refreshError) {
      processQueue(refreshError as AxiosError, null)
      tokenStorage.clearTokens()
      window.location.assign('/login')
      return Promise.reject(refreshError)
    } finally {
      isRefreshing = false
    }
  }
)

export { plainAxios }
export default axiosInstance
