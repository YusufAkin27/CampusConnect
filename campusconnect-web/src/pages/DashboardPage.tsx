import { useNavigate } from 'react-router-dom'
import HomePage from './HomePage'
import { useAuth } from '../context/AuthContext'

const DashboardPage = () => {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <HomePage
      userName={user?.username}
      userHandle={user?.username}
      onLogout={handleLogout}
    />
  )
}

export default DashboardPage
