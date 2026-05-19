import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { tokenStorage } from '../utils/tokenStorage'

type ProtectedRouteProps = {
  children: ReactNode
}

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  if (!tokenStorage.isAuthenticated()) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

export default ProtectedRoute
