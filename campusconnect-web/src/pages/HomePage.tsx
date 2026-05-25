import MainLayout from '../components/home/MainLayout'
import Sidebar from '../components/home/Sidebar'
import Topbar from '../components/home/Topbar'
import CreatePostCard from '../components/home/CreatePostCard'
import FeedList from '../components/home/FeedList'
import RightPanel from '../components/home/RightPanel'
import WelcomeCard from '../components/home/WelcomeCard'
import type { ClubSummary, EventSummary, FriendSuggestion, PostSummary } from '../types/home'

type HomePageProps = {
  userName?: string
  userHandle?: string
  onLogout?: () => void
}

const HomePage = ({ userName, userHandle, onLogout }: HomePageProps) => {
  const posts: PostSummary[] = []
  const events: EventSummary[] = []
  const suggestions: FriendSuggestion[] = []
  const clubs: ClubSummary[] = []

  return (
    <MainLayout
      topbar={<Topbar userName={userName} userHandle={userHandle} onLogout={onLogout} />}
      sidebar={<Sidebar />}
      rightPanel={<RightPanel events={events} suggestions={suggestions} clubs={clubs} />}
    >
      <WelcomeCard userName={userName} />
      <CreatePostCard />
      <FeedList posts={posts} isLoading={false} />
    </MainLayout>
  )
}

export default HomePage
