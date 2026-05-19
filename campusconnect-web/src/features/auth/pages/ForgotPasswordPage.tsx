import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '../components/AuthLayout'
import AuthInput from '../components/AuthInput'
import SubmitButton from '../components/SubmitButton'
import {
  forgotPasswordSchema,
  type ForgotPasswordFormValues,
} from '../schemas/authSchemas'
import { authService } from '../services/authService'

const ForgotPasswordPage = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ForgotPasswordFormValues>({
    resolver: zodResolver(forgotPasswordSchema),
  })

  const onSubmit = handleSubmit(async (values) => {
    setErrorMessage(null)
    setSuccessMessage(null)
    try {
      await authService.forgotPassword(values)
      setSuccessMessage(
        'Şifre sıfırlama bağlantısı e-posta adresinize gönderildi.'
      )
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Bir hata oluştu.')
    }
  })

  return (
    <AuthLayout
      title="Şifreni mi unuttun?"
      subtitle="E-posta adresini gir, şifre sıfırlama bağlantısını gönderelim."
    >
      <form onSubmit={onSubmit} className="space-y-5">
        {errorMessage ? (
          <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
            {errorMessage}
          </div>
        ) : null}
        {successMessage ? (
          <div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
            {successMessage}
          </div>
        ) : null}

        <AuthInput
          label="E-posta"
          name="email"
          register={register}
          error={errors.email?.message}
          placeholder="yusuf@example.com"
          autoComplete="email"
        />

        <SubmitButton isLoading={isSubmitting}>
          Şifre sıfırlama bağlantısı gönder
        </SubmitButton>

        <p className="text-center text-sm text-slate-600">
          <Link
            to="/login"
            className="font-semibold text-slate-900 hover:text-slate-700"
          >
            Giriş sayfasına dön
          </Link>
        </p>
      </form>
    </AuthLayout>
  )
}

export default ForgotPasswordPage
