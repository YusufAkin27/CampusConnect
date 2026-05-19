import { useMemo, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '../components/AuthLayout'
import AuthInput from '../components/AuthInput'
import SubmitButton from '../components/SubmitButton'
import { resetPasswordSchema, type ResetPasswordFormValues } from '../schemas/authSchemas'
import { authService } from '../services/authService'

const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams()
  const token = useMemo(() => searchParams.get('token') || '', [searchParams])
  const navigate = useNavigate()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<ResetPasswordFormValues>({
    resolver: zodResolver(resetPasswordSchema),
  })

  const onSubmit = handleSubmit(async (values) => {
    setErrorMessage(null)
    setSuccessMessage(null)

    if (!token) {
      setErrorMessage('Geçersiz şifre sıfırlama bağlantısı.')
      return
    }

    try {
      await authService.resetPassword({
        token,
        newPassword: values.newPassword,
      })
      setSuccessMessage('Şifre başarıyla güncellendi. Girişe yönlendiriliyorsun.')
      setTimeout(() => navigate('/login'), 1200)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Bir hata oluştu.')
    }
  })

  return (
    <AuthLayout
      title="Şifreni yenile"
      subtitle="Güçlü bir şifre belirleyerek hesabını koru."
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
          label="Yeni şifre"
          name="newPassword"
          register={register}
          error={errors.newPassword?.message}
          placeholder="••••••••"
          autoComplete="new-password"
          isPassword
        />
        <AuthInput
          label="Şifre tekrar"
          name="confirmPassword"
          register={register}
          error={errors.confirmPassword?.message}
          placeholder="••••••••"
          autoComplete="new-password"
          isPassword
        />

        <SubmitButton isLoading={isSubmitting}>Şifreyi güncelle</SubmitButton>

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

export default ResetPasswordPage
