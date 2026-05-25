const navigationItems = [
  { label: 'Ana Sayfa', icon: 'home', active: true },
  { label: 'Profilim', icon: 'user' },
  { label: 'Arkadaslar', icon: 'users' },
  { label: 'Mesajlar', icon: 'messages' },
  { label: 'Etkinlikler', icon: 'calendar' },
  { label: 'Kulupler', icon: 'star' },
  { label: 'Bildirimler', icon: 'bell' },
  { label: 'Ayarlar', icon: 'settings' },
]

const iconMap: Record<string, JSX.Element> = {
  home: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="m3 10 9-7 9 7" />
      <path d="M9 22V12h6v10" />
    </svg>
  ),
  user: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M20 21a8 8 0 0 0-16 0" />
      <circle cx="12" cy="7" r="4" />
    </svg>
  ),
  users: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17 21a5 5 0 0 0-10 0" />
      <circle cx="12" cy="8" r="4" />
      <path d="M22 21a4 4 0 0 0-6-3" />
    </svg>
  ),
  messages: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M21 15a4 4 0 0 1-4 4H7l-4 4V7a4 4 0 0 1 4-4h10a4 4 0 0 1 4 4z" />
    </svg>
  ),
  calendar: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <rect x="3" y="4" width="18" height="18" rx="2" />
      <path d="M16 2v4M8 2v4M3 10h18" />
    </svg>
  ),
  star: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="m12 2 3 7 7 1-5 5 1 7-6-4-6 4 1-7-5-5 7-1z" />
    </svg>
  ),
  bell: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M6 8a6 6 0 0 1 12 0c0 7 3 7 3 7H3s3 0 3-7" />
      <path d="M10 21a2 2 0 0 0 4 0" />
    </svg>
  ),
  settings: (
    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <circle cx="12" cy="12" r="3" />
      <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06A1.65 1.65 0 0 0 15 19.4a1.65 1.65 0 0 0-1 .6 1.65 1.65 0 0 0-.33 1.82l.02.08a2 2 0 0 1-3.38 0l.02-.08A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82-.33l-.08.02a2 2 0 0 1-2.83-2.83l.06-.06A1.65 1.65 0 0 0 4.6 15a1.65 1.65 0 0 0-1.82-.33l-.08.02a2 2 0 0 1 0-3.38l.08.02A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 2.83-2.83l.06.06A1.65 1.65 0 0 0 9 4.6a1.65 1.65 0 0 0 1-.6 1.65 1.65 0 0 0 .33-1.82l-.02-.08a2 2 0 0 1 3.38 0l-.02.08A1.65 1.65 0 0 0 15 4.6a1.65 1.65 0 0 0 1.82.33l.08-.02a2 2 0 0 1 2.83 2.83l-.06.06A1.65 1.65 0 0 0 19.4 9c.12.52.12 1.06 0 1.58" />
    </svg>
  ),
}

const Sidebar = () => {
  return (
    <div className="space-y-4">
      <nav className="hidden rounded-3xl border border-slate-200/70 bg-white/90 p-4 shadow-card backdrop-blur md:block">
        <p className="text-xs font-semibold uppercase tracking-[0.3em] text-slate-400">
          Menu
        </p>
        <ul className="mt-4 space-y-2">
          {navigationItems.map((item) => (
            <li key={item.label}>
              <button
                type="button"
                className={`flex w-full items-center gap-3 rounded-2xl px-3 py-2 text-sm transition ${
                  item.active
                    ? 'bg-slate-900 text-white'
                    : 'text-slate-600 hover:bg-slate-50'
                }`}
              >
                <span className="text-base">{iconMap[item.icon]}</span>
                <span className="font-medium">{item.label}</span>
              </button>
            </li>
          ))}
        </ul>
      </nav>

      <div className="md:hidden">
        <div className="fixed bottom-4 left-1/2 z-40 w-[calc(100%-2rem)] -translate-x-1/2 rounded-3xl border border-slate-200/70 bg-white/95 px-4 py-3 shadow-card backdrop-blur">
          <div className="grid grid-cols-4 gap-2 text-xs text-slate-600">
            {navigationItems.slice(0, 4).map((item) => (
              <button
                key={item.label}
                type="button"
                className={`flex flex-col items-center gap-1 rounded-2xl py-2 ${
                  item.active ? 'bg-slate-900 text-white' : 'hover:bg-slate-50'
                }`}
              >
                {iconMap[item.icon]}
                <span className="text-[10px] font-medium">{item.label}</span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Sidebar
