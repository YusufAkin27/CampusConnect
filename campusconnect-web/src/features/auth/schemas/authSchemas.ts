import { z } from 'zod'

const strongPassword = z
  .string()
  .min(8, 'Şifre en az 8 karakter olmalı.')
  .regex(/[A-Z]/, 'Şifre en az bir büyük harf içermeli.')
  .regex(/[a-z]/, 'Şifre en az bir küçük harf içermeli.')
  .regex(/[0-9]/, 'Şifre en az bir rakam içermeli.')

export const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Kullanıcı adı veya e-posta zorunludur.'),
  password: z.string().min(1, 'Şifre zorunludur.'),
  rememberMe: z.boolean().optional(),
})

export const registerSchema = z
  .object({
    firstName: z.string().min(1, 'Ad zorunludur.'),
    lastName: z.string().min(1, 'Soyad zorunludur.'),
    username: z.string().min(3, 'Kullanıcı adı en az 3 karakter olmalı.'),
    email: z.string().email('Geçerli bir e-posta girin.'),
    password: strongPassword,
    confirmPassword: z.string().min(1, 'Şifre tekrar zorunludur.'),
    university: z.string().min(1, 'Üniversite zorunludur.'),
    department: z.string().min(1, 'Bölüm zorunludur.'),
    grade: z.string().min(1, 'Sınıf seçilmelidir.'),
    termsAccepted: z.literal(true, {
      errorMap: () => ({ message: 'Koşulları kabul etmelisiniz.' }),
    }),
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
    newPassword: strongPassword,
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
