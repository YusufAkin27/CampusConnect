import type { ReactNode } from 'react'

type MainLayoutProps = {
  topbar: ReactNode
  sidebar: ReactNode
  rightPanel: ReactNode
  children: ReactNode
}

const MainLayout = ({ topbar, sidebar, rightPanel, children }: MainLayoutProps) => {
  return (
    <div className="w-full max-w-7xl space-y-6 pb-24 md:pb-10">
      {topbar}
      <div className="grid gap-6 md:grid-cols-[200px_minmax(0,1fr)] lg:grid-cols-[240px_minmax(0,1fr)_280px]">
        <aside className="md:sticky md:top-24 md:self-start">{sidebar}</aside>
        <main className="space-y-6">{children}</main>
        <aside className="lg:block md:col-span-2">{rightPanel}</aside>
      </div>
    </div>
  )
}

export default MainLayout
