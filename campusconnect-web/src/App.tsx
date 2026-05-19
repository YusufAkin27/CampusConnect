import { Outlet } from 'react-router-dom'

const App = () => {
  return (
    <div className="min-h-screen bg-gradient-to-b from-slate-50 via-slate-100 to-sky-50 text-slate-900">
      <div className="pointer-events-none absolute left-1/2 top-0 h-64 w-[680px] -translate-x-1/2 rounded-full bg-sky-100/70 blur-3xl" />
      <div className="relative flex min-h-screen items-center justify-center px-4 py-12">
        <Outlet />
      </div>
    </div>
  )
}

export default App
