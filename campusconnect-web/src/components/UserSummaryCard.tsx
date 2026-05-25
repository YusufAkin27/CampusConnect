type UserSummaryCardProps = {
  name?: string
  email?: string
  role?: string
}

const UserSummaryCard = ({ name, email, role }: UserSummaryCardProps) => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur">
      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">Profil</p>
      <h2 className="mt-2 text-lg font-semibold text-slate-900">
        {name || 'CampusConnect kullanicisi'}
      </h2>
      <p className="mt-1 text-xs text-slate-500">{email || 'E-posta bilgisi yok'}</p>
      <div className="mt-4 rounded-2xl bg-slate-50 px-4 py-3 text-xs text-slate-600">
        Rol: {role || 'USER'}
      </div>
      {/* Kullanici profili /v1/api/users/{id} endpoint'i ile genisletilecek. */}
    </section>
  )
}

export default UserSummaryCard
