import EmptyState from './EmptyState'
import type { FriendSuggestion } from '../../types/home'

type FriendSuggestionsCardProps = {
  suggestions: FriendSuggestion[]
}

const FriendSuggestionsCard = ({ suggestions }: FriendSuggestionsCardProps) => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-5 shadow-card backdrop-blur">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-slate-900">Arkadas onerileri</h3>
        <span className="text-[10px] font-semibold text-slate-400">Oneri</span>
      </div>
      {/* GET /v1/api/users/suggestions */}
      {suggestions.length === 0 ? (
        <div className="mt-4">
          <EmptyState
            title="Oneri bulunamadi"
            description="Yeni oneriler geldiginde burada listelenecek."
          />
        </div>
      ) : (
        <ul className="mt-4 space-y-3">
          {suggestions.map((user) => (
            <li
              key={user.id}
              className="flex items-center justify-between gap-3 rounded-2xl border border-slate-200 bg-slate-50 px-3 py-3"
            >
              <div>
                <p className="text-sm font-semibold text-slate-800">{user.name}</p>
                <p className="text-xs text-slate-500">@{user.username}</p>
                {user.mutualCount ? (
                  <p className="text-[10px] text-slate-400">
                    {user.mutualCount} ortak baglanti
                  </p>
                ) : null}
              </div>
              <button
                type="button"
                className="rounded-full border border-slate-200 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:bg-slate-50"
              >
                Takip et
              </button>
            </li>
          ))}
        </ul>
      )}
      {/* POST /v1/api/follows/{userId} */}
    </section>
  )
}

export default FriendSuggestionsCard
