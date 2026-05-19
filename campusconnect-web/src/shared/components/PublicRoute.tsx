import type { ReactNode } from 'react'
import { Navigate } from 'react-router-dom'
import { tokenStorage } from '../utils/tokenStorage'

type PublicRouteProps = {
  children: ReactNode
}

const PublicRoute = ({ children }: PublicRouteProps) => {
  if (tokenStorage.isAuthenticated()) {
    return <Navigate to="/dashboard" replace />
  }

  return <>{children}</>
}

export default PublicRoute
