import type { PostSummary } from '../../types/home'

type PostCardProps = {
  post: PostSummary
}

const PostCard = ({ post }: PostCardProps) => {
  return (
    <article className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur">
      <div className="flex items-center gap-3">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 text-xs font-semibold text-slate-600">
          {post.authorName.slice(0, 1).toUpperCase()}
        </div>
        <div className="flex-1">
          <p className="text-sm font-semibold text-slate-900">{post.authorName}</p>
          <p className="text-xs text-slate-500">@{post.authorUsername}</p>
        </div>
        <span className="text-xs text-slate-400">{post.createdAt}</span>
      </div>
      <p className="mt-4 text-sm text-slate-700">{post.content}</p>
      {post.mediaUrls && post.mediaUrls.length > 0 ? (
        <div className="mt-4 rounded-2xl border border-slate-200 bg-slate-50 px-4 py-6 text-center text-xs text-slate-500">
          Medya burada gosterilecek
        </div>
      ) : null}
      <div className="mt-5 flex flex-wrap items-center gap-2 text-xs text-slate-600">
        <button type="button" className="rounded-full border border-slate-200 px-3 py-1.5 hover:bg-slate-50">
          Begen
        </button>
        <button type="button" className="rounded-full border border-slate-200 px-3 py-1.5 hover:bg-slate-50">
          Yorum
        </button>
        <button type="button" className="rounded-full border border-slate-200 px-3 py-1.5 hover:bg-slate-50">
          Paylas
        </button>
        <button type="button" className="rounded-full border border-slate-200 px-3 py-1.5 hover:bg-slate-50">
          Kaydet
        </button>
      </div>
      {/* like-service: POST /v1/api/likes */}
      {/* comment-service: POST /v1/api/comments */}
    </article>
  )
}

export default PostCard
