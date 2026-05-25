import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { authService } from '../features/auth/services/authService'
import { tokenStorage } from '../utils/tokenStorage'
import type { AuthResponse, LoginRequest, RegisterRequest, UserAuthResponse } from '../features/auth/types/authTypes'

type AuthContextValue = {
  user: UserAuthResponse | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (payload: LoginRequest) => Promise<AuthResponse>
  register: (payload: RegisterRequest) => Promise<AuthResponse>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

type AuthProviderProps = {
  children: ReactNode
}

export const AuthProvider = ({ children }: AuthProviderProps) => {
  const [user, setUser] = useState<UserAuthResponse | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  const bootstrapAuth = useCallback(async () => {
    if (!tokenStorage.getAccessToken()) {
      setIsLoading(false)
      return
    }

    try {
      const currentUser = (await authService.getCurrentUser()) as UserAuthResponse
      setUser(currentUser)
    } catch {
      await authService.logout()
      setUser(null)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    bootstrapAuth()
  }, [bootstrapAuth])

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await authService.login(payload)
    setUser(response.user)
    return response
  }, [])

  const register = useCallback(async (payload: RegisterRequest) => {
    const response = await authService.register(payload)
    setUser(response.user)
    return response
  }, [])

  const logout = useCallback(async () => {
    await authService.logout()
    setUser(null)
  }, [])

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: Boolean(user) || tokenStorage.isAuthenticated(),
      isLoading,
      login,
      register,
      logout,
    }),
    [user, isLoading, login, register, logout]
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider')
  }
  return context
}
