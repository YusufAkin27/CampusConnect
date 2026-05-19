import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '../components/AuthLayout'
import AuthInput from '../components/AuthInput'
import SubmitButton from '../components/SubmitButton'
import { registerSchema, type RegisterFormValues } from '../schemas/authSchemas'
import { authService } from '../services/authService'

const gradeOptions = [
  { value: '', label: 'Sınıf seçin' },
  { value: 'FIRST', label: '1. Sınıf' },
  { value: 'SECOND', label: '2. Sınıf' },
  { value: 'THIRD', label: '3. Sınıf' },
  { value: 'FOURTH', label: '4. Sınıf' },
  { value: 'MASTER', label: 'Yüksek Lisans' },
  { value: 'PHD', label: 'Doktora' },
]

const RegisterPage = () => {
  const navigate = useNavigate()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = handleSubmit(async (values) => {
    setErrorMessage(null)
    setSuccessMessage(null)
    try {
      const { confirmPassword, termsAccepted, ...payload } = values
      await authService.register(payload)
      setSuccessMessage('Kayıt başarılı. Giriş sayfasına yönlendiriliyorsunuz...')
      setTimeout(() => navigate('/login'), 1200)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Bir hata oluştu.')
    }
  })

  return (
    <AuthLayout
      title="Hesap Oluştur"
      subtitle="CampusConnect'e katıl ve kampüs topluluğunu keşfet."
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

        <div className="grid gap-4 md:grid-cols-2">
          <AuthInput
            label="Ad"
            name="firstName"
            register={register}
            error={errors.firstName?.message}
            placeholder="Yusuf"
            autoComplete="given-name"
          />
          <AuthInput
            label="Soyad"
            name="lastName"
            register={register}
            error={errors.lastName?.message}
            placeholder="Akin"
            autoComplete="family-name"
          />
        </div>

        <AuthInput
            label="Kullanıcı adı"
          name="username"
          register={register}
          error={errors.username?.message}
          placeholder="yusufakin"
          autoComplete="username"
        />

        <AuthInput
          label="E-posta"
          name="email"
          register={register}
          error={errors.email?.message}
          placeholder="yusuf@example.com"
          autoComplete="email"
        />

        <div className="grid gap-4 md:grid-cols-2">
          <AuthInput
            label="Şifre"
            name="password"
            register={register}
            error={errors.password?.message}
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
        </div>

        <AuthInput
          label="Üniversite"
          name="university"
          register={register}
          error={errors.university?.message}
          placeholder="Bingol Universitesi"
        />

        <AuthInput
          label="Bölüm"
          name="department"
          register={register}
          error={errors.department?.message}
          placeholder="Bilgisayar Muhendisligi"
        />

        <div>
          <label className="text-sm font-medium text-slate-700" htmlFor="grade">
            Sınıf
          </label>
          <select
            id="grade"
            className="mt-2 w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200"
            {...register('grade')}
          >
            {gradeOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
          {errors.grade?.message ? (
            <p className="mt-2 text-xs text-red-500">{errors.grade.message}</p>
          ) : null}
        </div>

        <label className="flex items-start gap-2 text-sm text-slate-600">
          <input
            type="checkbox"
            className="mt-1 h-4 w-4 rounded border-slate-300 text-slate-900"
            {...register('termsAccepted')}
          />
          <span>
            CampusConnect üyelik koşullarını ve gizlilik politikasını kabul
            ediyorum.
          </span>
        </label>
        {errors.termsAccepted?.message ? (
          <p className="text-xs text-red-500">{errors.termsAccepted.message}</p>
        ) : null}

        <SubmitButton isLoading={isSubmitting}>Kayıt ol</SubmitButton>

        <p className="text-center text-sm text-slate-600">
          Zaten hesabın var mı?{' '}
          <Link
            to="/login"
            className="font-semibold text-slate-900 hover:text-slate-700"
          >
            Giriş yap
          </Link>
        </p>
      </form>
    </AuthLayout>
  )
}

export default RegisterPage
