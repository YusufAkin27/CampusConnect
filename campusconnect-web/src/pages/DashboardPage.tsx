import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import PostComposer from '../components/PostComposer'
import FeedPlaceholder from '../components/FeedPlaceholder'
import UserSummaryCard from '../components/UserSummaryCard'
import { useAuth } from '../context/AuthContext'

const DashboardPage = () => {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <div className="w-full max-w-5xl space-y-6">
      <Navbar
        userName={user?.username}
        userEmail={user?.email}
        onLogout={handleLogout}
      />
      <div className="grid gap-6 lg:grid-cols-[280px_1fr]">
        <UserSummaryCard
          name={user?.username}
          email={user?.email}
          role={user?.role}
        />
        <div className="space-y-6">
          <PostComposer />
          <FeedPlaceholder />
        </div>
      </div>
    </div>
  )
}

export default DashboardPage
