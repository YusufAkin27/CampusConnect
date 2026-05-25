const FeedPlaceholder = () => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-900">Ana akis</h2>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-[10px] font-semibold text-slate-600">
          Feed hazir
        </span>
      </div>
      <div className="mt-4 rounded-2xl border border-dashed border-slate-200 bg-slate-50 px-4 py-6 text-center text-xs text-slate-500">
        Henuz veri yok. Feed endpoint'i baglandiginda paylasimlar burada listelenecek.
      </div>
      {/* GET /v1/api/posts veya /v1/api/feed endpoint'i ile liste doldurulacak. */}
    </section>
  )
}

export default FeedPlaceholder
