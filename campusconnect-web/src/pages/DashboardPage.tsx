import { useNavigate } from 'react-router-dom'
import { authService } from '../features/auth/services/authService'

const DashboardPage = () => {
  const navigate = useNavigate()

  const handleLogout = async () => {
    await authService.logout()
    navigate('/login')
  }

  return (
    <div className="w-full max-w-3xl rounded-3xl border border-slate-200/70 bg-white/90 p-8 text-center shadow-card backdrop-blur">
      <p className="text-sm uppercase tracking-[0.3em] text-slate-500">
        CampusConnect
      </p>
      <h1 className="mt-4 font-display text-3xl font-semibold text-slate-900 md:text-4xl">
        Hoş geldin!
      </h1>
      <p className="mt-2 text-sm text-slate-600 md:text-base">
        Auth modülü başarıyla çalışıyor. Şimdi topluluğuna katılma zamanı.
      </p>
      <button
        onClick={handleLogout}
        className="mt-8 rounded-2xl border border-slate-200 px-6 py-3 text-sm font-semibold text-slate-900 transition hover:border-slate-300 hover:bg-slate-50"
      >
        Çıkış yap
      </button>
    </div>
  )
}

export default DashboardPage
