import { useState } from 'react'
import type { FieldValues, Path, UseFormRegister } from 'react-hook-form'

type AuthInputProps<T extends FieldValues> = {
  label: string
  name: Path<T>
  register: UseFormRegister<T>
  error?: string
  type?: string
  placeholder?: string
  autoComplete?: string
  isPassword?: boolean
}

const AuthInput = <T extends FieldValues>({
  label,
  name,
  register,
  error,
  type = 'text',
  placeholder,
  autoComplete,
  isPassword,
}: AuthInputProps<T>) => {
  const [visible, setVisible] = useState(false)
  const resolvedType = isPassword ? (visible ? 'text' : 'password') : type

  return (
    <div>
      <label className="text-sm font-medium text-slate-700" htmlFor={name}>
        {label}
      </label>
      <div className="relative mt-2">
        <input
          id={name}
          type={resolvedType}
          placeholder={placeholder}
          autoComplete={autoComplete}
          className="w-full rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200"
          {...register(name)}
        />
        {isPassword ? (
          <button
            type="button"
            onClick={() => setVisible((current) => !current)}
            className="absolute right-3 top-1/2 -translate-y-1/2 text-xs font-medium text-slate-500 transition hover:text-slate-800"
          >
            {visible ? 'Gizle' : 'Göster'}
          </button>
        ) : null}
      </div>
      {error ? <p className="mt-2 text-xs text-red-500">{error}</p> : null}
    </div>
  )
}

export default AuthInput
