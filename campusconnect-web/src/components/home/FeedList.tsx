import EmptyState from './EmptyState'
import LoadingState from './LoadingState'
import PostCard from './PostCard'
import type { PostSummary } from '../../types/home'

type FeedListProps = {
  posts: PostSummary[]
  isLoading?: boolean
}

const FeedList = ({ posts, isLoading }: FeedListProps) => {
  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-900">Kampus akisi</h2>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-[10px] font-semibold text-slate-600">
          Feed hazir
        </span>
      </div>
      {/* GET /v1/api/posts/feed */}
      {isLoading ? <LoadingState /> : null}
      {!isLoading && posts.length === 0 ? (
        <EmptyState
          title="Henuz gonderi yok. Ilk paylasimi sen yap."
          description="Feed endpoint'i baglandiginda gonderiler burada listelenecek."
        />
      ) : null}
      {!isLoading && posts.length > 0 ? (
        <div className="space-y-4">
          {posts.map((post) => (
            <PostCard key={post.id} post={post} />
          ))}
        </div>
      ) : null}
    </section>
  )
}

export default FeedList
