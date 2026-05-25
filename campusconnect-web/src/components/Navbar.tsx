type NavbarProps = {
  userName?: string
  userEmail?: string
  onLogout: () => void
}

const Navbar = ({ userName, userEmail, onLogout }: NavbarProps) => {
  return (
    <header className="flex flex-col gap-4 rounded-3xl border border-slate-200/70 bg-white/90 px-6 py-4 shadow-card backdrop-blur md:flex-row md:items-center md:justify-between">
      <div>
        <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
          CampusConnect
        </p>
        <p className="text-lg font-semibold text-slate-900">
          Ogrenci sosyal agina hos geldin
        </p>
        {userEmail ? (
          <p className="text-xs text-slate-500">{userEmail}</p>
        ) : null}
      </div>
      <div className="flex items-center gap-3">
        {userName ? (
          <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
            {userName}
          </span>
        ) : null}
        <button
          type="button"
          onClick={onLogout}
          className="rounded-full border border-slate-200 px-4 py-2 text-xs font-semibold text-slate-900 transition hover:border-slate-300 hover:bg-slate-50"
        >
          Cikis yap
        </button>
      </div>
    </header>
  )
}

export default Navbar
