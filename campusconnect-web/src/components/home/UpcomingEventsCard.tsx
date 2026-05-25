import EmptyState from './EmptyState'
import type { EventSummary } from '../../types/home'

type UpcomingEventsCardProps = {
  events: EventSummary[]
}

const UpcomingEventsCard = ({ events }: UpcomingEventsCardProps) => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-5 shadow-card backdrop-blur">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold text-slate-900">Kampus etkinlikleri</h3>
        <span className="text-[10px] font-semibold text-slate-400">Yakinda</span>
      </div>
      {/* GET /v1/api/events/upcoming */}
      {events.length === 0 ? (
        <div className="mt-4">
          <EmptyState
            title="Etkinlik bulunamadi"
            description="Yeni etkinlikler paylasildiginda burada gorunecek."
          />
        </div>
      ) : (
        <ul className="mt-4 space-y-3">
          {events.map((event) => (
            <li
              key={event.id}
              className="rounded-2xl border border-slate-200 bg-slate-50 px-3 py-3"
            >
              <p className="text-sm font-semibold text-slate-800">{event.title}</p>
              <p className="text-xs text-slate-500">
                {event.startsAt} {event.location ? `• ${event.location}` : ''}
              </p>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}

export default UpcomingEventsCard
