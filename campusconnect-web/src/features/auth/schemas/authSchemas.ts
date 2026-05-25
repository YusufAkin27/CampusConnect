import { z } from 'zod'

const passwordRule = z
  .string()
  .min(6, 'Şifre en az 6 karakter olmalı.')

export const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Kullanıcı adı veya e-posta zorunludur.'),
  password: z.string().min(1, 'Şifre zorunludur.'),
  rememberMe: z.boolean().optional(),
})

export const registerSchema = z
  .object({
    username: z.string().min(3, 'Kullanıcı adı en az 3 karakter olmalı.'),
    email: z.string().email('Geçerli bir e-posta girin.'),
    password: passwordRule,
    confirmPassword: z.string().min(1, 'Şifre tekrar zorunludur.'),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: 'Şifreler aynı olmalı.',
    path: ['confirmPassword'],
  })

export const forgotPasswordSchema = z.object({
  email: z.string().email('Geçerli bir e-posta girin.'),
})

export const resetPasswordSchema = z
  .object({
    newPassword: passwordRule,
    confirmPassword: z.string().min(1, 'Şifre tekrar zorunludur.'),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Şifreler aynı olmalı.',
    path: ['confirmPassword'],
  })

export type LoginFormValues = z.infer<typeof loginSchema>
export type RegisterFormValues = z.infer<typeof registerSchema>
export type ForgotPasswordFormValues = z.infer<typeof forgotPasswordSchema>
export type ResetPasswordFormValues = z.infer<typeof resetPasswordSchema>
