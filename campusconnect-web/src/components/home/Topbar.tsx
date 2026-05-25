type TopbarProps = {
  userName?: string
  userHandle?: string
  onLogout?: () => void
}

const Topbar = ({ userName, userHandle, onLogout }: TopbarProps) => {
  return (
    <header className="flex flex-col gap-4 rounded-3xl border border-slate-200/70 bg-white/90 px-5 py-4 shadow-card backdrop-blur md:flex-row md:items-center md:justify-between">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-2xl bg-slate-900 text-xs font-semibold uppercase text-white">
          CC
        </div>
        <div>
          <p className="text-sm uppercase tracking-[0.3em] text-slate-500">
            CampusConnect
          </p>
          <p className="text-xs text-slate-500">Kampus toplulugun burada</p>
        </div>
      </div>

      <div className="flex flex-1 items-center gap-3 md:justify-center">
        <div className="relative w-full max-w-md">
          <span className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-400">
            <svg
              width="16"
              height="16"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.6"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.3-4.3" />
            </svg>
          </span>
          <input
            type="search"
            placeholder="Kampuste kisi, gonderi veya etkinlik ara..."
            className="w-full rounded-2xl border border-slate-200 bg-white px-9 py-2.5 text-sm text-slate-900 shadow-sm outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200"
          />
          {/* GET /v1/api/search?query= */}
        </div>
      </div>

      <div className="flex items-center gap-3">
        <button
          type="button"
          className="flex h-10 w-10 items-center justify-center rounded-2xl border border-slate-200 text-slate-600 transition hover:bg-slate-50"
          aria-label="Bildirimler"
        >
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.6"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M6 8a6 6 0 0 1 12 0c0 7 3 7 3 7H3s3 0 3-7" />
            <path d="M10 21a2 2 0 0 0 4 0" />
          </svg>
        </button>
        <button
          type="button"
          className="flex h-10 w-10 items-center justify-center rounded-2xl border border-slate-200 text-slate-600 transition hover:bg-slate-50"
          aria-label="Mesajlar"
        >
          <svg
            width="18"
            height="18"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="1.6"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M21 15a4 4 0 0 1-4 4H7l-4 4V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4z" />
          </svg>
        </button>
        <div className="hidden items-center gap-2 rounded-2xl border border-slate-200 px-3 py-2 text-xs text-slate-600 md:flex">
          <span className="flex h-7 w-7 items-center justify-center rounded-full bg-slate-100 text-xs font-semibold text-slate-700">
            {userName ? userName.slice(0, 1).toUpperCase() : 'U'}
          </span>
          <div>
            <p className="text-xs font-semibold text-slate-900">
              {userName || 'Campus User'}
            </p>
            <p className="text-[10px] text-slate-500">
              {userHandle ? `@${userHandle}` : 'profilini guncelle'}
            </p>
          </div>
        </div>
        <button
          type="button"
          onClick={onLogout}
          disabled={!onLogout}
          className="rounded-2xl border border-slate-200 px-4 py-2 text-xs font-semibold text-slate-900 transition hover:border-slate-300 hover:bg-slate-50 disabled:opacity-50"
        >
          Cikis
        </button>
      </div>
    </header>
  )
}

export default Topbar
