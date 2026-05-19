import type { ReactNode } from 'react'

type AuthLayoutProps = {
  title: string
  subtitle: string
  children: ReactNode
}

const AuthLayout = ({ title, subtitle, children }: AuthLayoutProps) => {
  return (
    <div className="w-full max-w-xl">
      <div className="mb-8 text-center">
        <p className="text-sm uppercase tracking-[0.3em] text-slate-500">
          CampusConnect
        </p>
        <h1 className="font-display text-3xl font-semibold text-slate-900 md:text-4xl">
          {title}
        </h1>
        <p className="mt-2 text-sm text-slate-600 md:text-base">{subtitle}</p>
      </div>
      <div className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur md:p-8">
        {children}
      </div>
    </div>
  )
}

export default AuthLayout
