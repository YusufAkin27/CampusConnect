import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '../components/AuthLayout'
import AuthInput from '../components/AuthInput'
import SubmitButton from '../components/SubmitButton'
import { loginSchema, type LoginFormValues } from '../schemas/authSchemas'
import { useAuth } from '../../../context/AuthContext'

const LoginPage = () => {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      rememberMe: true,
    },
  })

  const onSubmit = handleSubmit(async (values) => {
    setErrorMessage(null)
    try {
      await login(values)
      navigate('/home')
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Bir hata oluştu.')
    }
  })

  return (
    <AuthLayout
      title="Giriş Yap"
      subtitle="Kampüs deneyiminize güvenle devam edin."
    >
      <form onSubmit={onSubmit} className="space-y-5">
        {errorMessage ? (
          <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
            {errorMessage}
          </div>
        ) : null}

        <AuthInput
          label="Kullanıcı adı veya e-posta"
          name="usernameOrEmail"
          register={register}
          error={errors.usernameOrEmail?.message}
          placeholder="yusufakin"
          autoComplete="username"
        />
        <AuthInput
          label="Şifre"
          name="password"
          register={register}
          error={errors.password?.message}
          placeholder="••••••••"
          autoComplete="current-password"
          isPassword
        />

        <div className="flex items-center justify-between text-sm text-slate-600">
          <label className="flex items-center gap-2">
            <input
              type="checkbox"
              className="h-4 w-4 rounded border-slate-300 text-slate-900"
              {...register('rememberMe')}
            />
            Beni hatırla
          </label>
          <Link
            to="/forgot-password"
            className="font-medium text-slate-900 transition hover:text-slate-700"
          >
            Şifremi unuttum
          </Link>
        </div>

        <SubmitButton isLoading={isSubmitting}>Giriş yap</SubmitButton>

        <p className="text-center text-sm text-slate-600">
          Hesabın yok mu?{' '}
          <Link
            to="/register"
            className="font-semibold text-slate-900 hover:text-slate-700"
          >
            Kayıt ol
          </Link>
        </p>
      </form>
    </AuthLayout>
  )
}

export default LoginPage
