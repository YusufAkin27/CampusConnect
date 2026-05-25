import type { ReactNode } from 'react'

type SubmitButtonProps = {
  isLoading?: boolean
  disabled?: boolean
  children: ReactNode
}

const SubmitButton = ({ isLoading, disabled, children }: SubmitButtonProps) => {
  const isDisabled = Boolean(isLoading || disabled)
  return (
    <button
      type="submit"
      disabled={isDisabled}
      className="flex w-full items-center justify-center rounded-2xl bg-slate-900 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
    >
      {isLoading ? 'Yükleniyor...' : children}
    </button>
  )
}

export default SubmitButton
