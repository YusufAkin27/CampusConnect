type WelcomeCardProps = {
  userName?: string
}

const WelcomeCard = ({ userName }: WelcomeCardProps) => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur">
      <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
        Hos geldin
      </p>
      <h1 className="mt-2 text-2xl font-semibold text-slate-900">
        Merhaba {userName || 'kampus sakini'}, kampuste bugun neler oluyor?
      </h1>
      <p className="mt-2 text-sm text-slate-600">
        Kulup etkinlikleri, ders paylasimlari ve kampus duyurulari burada.
      </p>
      {/* GET /v1/api/users/me */}
    </section>
  )
}

export default WelcomeCard
