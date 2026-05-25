import { useEffect, useMemo, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '../components/AuthLayout'
import AuthInput from '../components/AuthInput'
import SubmitButton from '../components/SubmitButton'
import { registerSchema, type RegisterFormValues } from '../schemas/authSchemas'
import { contractService, type ContractDetail, type ContractSummary } from '../../../services/contractService'
import { useAuth } from '../../../context/AuthContext'

const RegisterPage = () => {
  const navigate = useNavigate()
  const { register: registerUser, logout } = useAuth()
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [successMessage, setSuccessMessage] = useState<string | null>(null)
  const [contracts, setContracts] = useState<ContractSummary[]>([])
  const [contractsLoading, setContractsLoading] = useState(true)
  const [contractsError, setContractsError] = useState<string | null>(null)
  const [selectedContractIds, setSelectedContractIds] = useState<Set<string>>(
    new Set()
  )
  const [activeContract, setActiveContract] = useState<ContractDetail | null>(null)
  const [contractDetailError, setContractDetailError] = useState<string | null>(null)
  const [contractDetailLoading, setContractDetailLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  })

  useEffect(() => {
    let isMounted = true
    const fetchContracts = async () => {
      setContractsLoading(true)
      setContractsError(null)
      try {
        const data = await contractService.getActiveContracts()
        if (isMounted) {
          setContracts(data)
        }
      } catch (error) {
        if (isMounted) {
          setContractsError(
            error instanceof Error
              ? error.message
              : 'Sozlesmeler getirilemedi.'
          )
        }
      } finally {
        if (isMounted) {
          setContractsLoading(false)
        }
      }
    }

    fetchContracts()

    return () => {
      isMounted = false
    }
  }, [])

  const requiredContracts = useMemo(
    () => contracts.filter((contract) => contract.isRequired),
    [contracts]
  )

  const hasAllRequiredContracts = useMemo(
    () =>
      requiredContracts.every((contract) =>
        selectedContractIds.has(contract.id)
      ),
    [requiredContracts, selectedContractIds]
  )

  const toggleContract = (contractId: string) => {
    setSelectedContractIds((current) => {
      const next = new Set(current)
      if (next.has(contractId)) {
        next.delete(contractId)
      } else {
        next.add(contractId)
      }
      return next
    })
  }

  const openContractDetail = async (contractId: string) => {
    setContractDetailError(null)
    setContractDetailLoading(true)
    const summary = contracts.find((contract) => contract.id === contractId)
    if (summary) {
      setActiveContract(summary)
    }
    try {
      const detail = await contractService.getContractDetail(contractId)
      setActiveContract(detail)
    } catch (error) {
      setContractDetailError(
        error instanceof Error ? error.message : 'Sozlesme detayi getirilemedi.'
      )
    } finally {
      setContractDetailLoading(false)
    }
  }

  const onSubmit = handleSubmit(async (values) => {
    setErrorMessage(null)
    setSuccessMessage(null)
    if (!hasAllRequiredContracts) {
      setErrorMessage('Zorunlu sozlesmeleri kabul etmeden kayit olamazsiniz.')
      return
    }
    try {
      const { confirmPassword, ...payload } = values
      const response = await registerUser(payload)
      const acceptedContractIds = Array.from(selectedContractIds)

      if (!response.user?.id) {
        throw new Error('Kullanici bilgisi alinmadi. Lutfen tekrar deneyin.')
      }

      if (acceptedContractIds.length > 0) {
        await contractService.acceptContracts({
          userId: String(response.user.id),
          acceptedContractIds,
        })
      }

      setSuccessMessage('Kayit basarili. Ana sayfaya yonlendiriliyorsunuz...')
      setTimeout(() => navigate('/home'), 1200)
    } catch (error) {
      await logout()
      setErrorMessage(error instanceof Error ? error.message : 'Bir hata olustu.')
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

        <div className="space-y-3 rounded-2xl border border-slate-200 bg-slate-50/70 p-4">
          <div className="text-sm font-semibold text-slate-800">Sozlesmeler</div>
          {contractsLoading ? (
            <p className="text-xs text-slate-500">Sozlesmeler yukleniyor...</p>
          ) : null}
          {contractsError ? (
            <p className="text-xs text-red-500">{contractsError}</p>
          ) : null}
          {!contractsLoading && !contractsError && contracts.length === 0 ? (
            <p className="text-xs text-slate-500">
              Aktif sozlesme bulunamadi.
            </p>
          ) : null}
          {!contractsLoading && !contractsError && contracts.length > 0 ? (
            <div className="space-y-3">
              {contracts.map((contract) => (
                <div
                  key={contract.id}
                  className="flex items-start justify-between gap-3 rounded-2xl border border-slate-200 bg-white px-3 py-3"
                >
                  <label className="flex flex-1 items-start gap-3 text-sm text-slate-700">
                    <input
                      type="checkbox"
                      className="mt-1 h-4 w-4 rounded border-slate-300 text-slate-900"
                      checked={selectedContractIds.has(contract.id)}
                      onChange={() => toggleContract(contract.id)}
                    />
                    <span>
                      <span className="block font-medium text-slate-900">
                        {contract.title}
                      </span>
                      <span className="text-xs text-slate-500">
                        Versiyon: {contract.version}
                      </span>
                      {contract.isRequired ? (
                        <span className="ml-2 inline-flex rounded-full bg-amber-100 px-2 py-0.5 text-[10px] font-semibold text-amber-700">
                          Zorunlu
                        </span>
                      ) : null}
                    </span>
                  </label>
                  <button
                    type="button"
                    onClick={() => openContractDetail(contract.id)}
                    className="text-xs font-semibold text-slate-700 transition hover:text-slate-900"
                  >
                    Detay
                  </button>
                </div>
              ))}
            </div>
          ) : null}
          {!hasAllRequiredContracts ? (
            <p className="text-xs text-red-500">
              Zorunlu sozlesmeleri kabul etmelisiniz.
            </p>
          ) : null}
        </div>

        <SubmitButton
          isLoading={isSubmitting}
          disabled={contractsLoading || Boolean(contractsError) || !hasAllRequiredContracts}
        >
          Kayit ol
        </SubmitButton>

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

      {activeContract ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4">
          <div className="w-full max-w-2xl rounded-3xl bg-white p-6 shadow-card">
            <div className="flex items-start justify-between gap-4">
              <div>
                <h2 className="text-lg font-semibold text-slate-900">
                  {activeContract.title}
                </h2>
                <p className="text-xs text-slate-500">
                  Versiyon: {activeContract.version}
                </p>
              </div>
              <button
                type="button"
                onClick={() => setActiveContract(null)}
                className="text-sm font-semibold text-slate-500 transition hover:text-slate-700"
              >
                Kapat
              </button>
            </div>
            <div className="mt-4 max-h-[50vh] overflow-y-auto rounded-2xl border border-slate-200 bg-slate-50/70 p-4 text-sm text-slate-700">
              {contractDetailLoading ? (
                <p>Sozlesme metni yukleniyor...</p>
              ) : null}
              {contractDetailError ? (
                <p className="text-red-500">{contractDetailError}</p>
              ) : null}
              {!contractDetailLoading && !contractDetailError ? (
                <p className="whitespace-pre-wrap">
                  {activeContract.content ||
                    'Sozlesme metni bu servis tarafindan saglandiginda burada goruntulenecek.'}
                </p>
              ) : null}
            </div>
          </div>
        </div>
      ) : null}
    </AuthLayout>
  )
}

export default RegisterPage
