export type LoginRequest = {
  usernameOrEmail: string
  password: string
  rememberMe?: boolean
}

export type UserAuthResponse = {
  id: number
  username: string
  email: string
  role: string
}

export type AuthResponse = {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: UserAuthResponse
}

export type RegisterRequest = {
  username: string
  email: string
  password: string
}

export type ForgotPasswordRequest = {
  email: string
}

export type ResetPasswordRequest = {
  token: string
  newPassword: string
}

export type LogoutRequest = {
  refreshToken: string
}
