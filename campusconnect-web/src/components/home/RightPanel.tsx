import type { ClubSummary, EventSummary, FriendSuggestion } from '../../types/home'
import UpcomingEventsCard from './UpcomingEventsCard'
import FriendSuggestionsCard from './FriendSuggestionsCard'
import EmptyState from './EmptyState'

type RightPanelProps = {
  events: EventSummary[]
  suggestions: FriendSuggestion[]
  clubs: ClubSummary[]
}

const RightPanel = ({ events, suggestions, clubs }: RightPanelProps) => {
  return (
    <div className="space-y-6">
      <UpcomingEventsCard events={events} />
      <FriendSuggestionsCard suggestions={suggestions} />
      <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-5 shadow-card backdrop-blur">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900">Populer kulupler</h3>
          <span className="text-[10px] font-semibold text-slate-400">Kampus</span>
        </div>
        {clubs.length === 0 ? (
          <div className="mt-4">
            <EmptyState
              title="Kulup bulunamadi"
              description="Kampus kulupleri hazirlandiginda burada gorunecek."
            />
          </div>
        ) : (
          <ul className="mt-4 space-y-3">
            {clubs.map((club) => (
              <li
                key={club.id}
                className="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-3"
              >
                <p className="text-sm font-semibold text-slate-800">{club.name}</p>
                {club.description ? (
                  <p className="text-xs text-slate-500">{club.description}</p>
                ) : null}
              </li>
            ))}
          </ul>
        )}
      </section>
    </div>
  )
}

export default RightPanel
