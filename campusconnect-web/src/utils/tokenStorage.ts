const ACCESS_TOKEN_KEY = 'cc_access_token'
const REFRESH_TOKEN_KEY = 'cc_refresh_token'

type StorageTarget = 'local' | 'session'

type TokenOptions = {
  persist?: boolean
}

const getTokenFrom = (storage: Storage, key: string) => storage.getItem(key)

const resolveStorage = (): Storage => {
  if (getTokenFrom(localStorage, ACCESS_TOKEN_KEY)) {
    return localStorage
  }
  if (getTokenFrom(sessionStorage, ACCESS_TOKEN_KEY)) {
    return sessionStorage
  }
  return localStorage
}

const resolveStorageTarget = (persist?: boolean): Storage => {
  if (persist === undefined) {
    return resolveStorage()
  }
  return persist ? localStorage : sessionStorage
}

const clearOtherStorage = (target: Storage) => {
  const other = target === localStorage ? sessionStorage : localStorage
  other.removeItem(ACCESS_TOKEN_KEY)
  other.removeItem(REFRESH_TOKEN_KEY)
}

export const tokenStorage = {
  getAccessToken: () =>
    getTokenFrom(localStorage, ACCESS_TOKEN_KEY) ||
    getTokenFrom(sessionStorage, ACCESS_TOKEN_KEY),
  getRefreshToken: () =>
    getTokenFrom(localStorage, REFRESH_TOKEN_KEY) ||
    getTokenFrom(sessionStorage, REFRESH_TOKEN_KEY),
  setTokens: (accessToken: string, refreshToken: string, options?: TokenOptions) => {
    const target = resolveStorageTarget(options?.persist)
    clearOtherStorage(target)
    target.setItem(ACCESS_TOKEN_KEY, accessToken)
    target.setItem(REFRESH_TOKEN_KEY, refreshToken)
  },
  setAccessToken: (accessToken: string) => {
    const target = resolveStorage()
    target.setItem(ACCESS_TOKEN_KEY, accessToken)
  },
  clearTokens: () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    sessionStorage.removeItem(ACCESS_TOKEN_KEY)
    sessionStorage.removeItem(REFRESH_TOKEN_KEY)
  },
  isAuthenticated: () => Boolean(tokenStorage.getAccessToken()),
  getStorageTarget: (): StorageTarget =>
    getTokenFrom(localStorage, ACCESS_TOKEN_KEY) ? 'local' : 'session',
}
