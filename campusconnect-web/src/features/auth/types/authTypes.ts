export type LoginRequest = {
  usernameOrEmail: string
  password: string
  rememberMe?: boolean
}

export type LoginResponse = {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export type RegisterRequest = {
  firstName: string
  lastName: string
  username: string
  email: string
  password: string
  university: string
  department: string
  grade: string
}

export type ForgotPasswordRequest = {
  email: string
}

export type ResetPasswordRequest = {
  token: string
  newPassword: string
}
