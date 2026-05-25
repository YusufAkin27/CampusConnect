const PostComposer = () => {
  return (
    <section className="rounded-3xl border border-slate-200/70 bg-white/90 p-6 shadow-card backdrop-blur">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-900">Yeni paylasim</h2>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-[10px] font-semibold text-slate-600">
          Post servisi baglanacak
        </span>
      </div>
      <textarea
        className="mt-4 h-28 w-full resize-none rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 shadow-sm outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200"
        placeholder="Bugun kampuste neler oldu?"
        disabled
      />
      <div className="mt-4 flex items-center justify-end">
        <button
          type="button"
          disabled
          className="rounded-2xl bg-slate-900 px-5 py-2 text-xs font-semibold text-white opacity-60"
        >
          Paylas
        </button>
      </div>
      {/* POST /v1/api/posts endpoint'i geldikten sonra form submit baglanacak. */}
    </section>
  )
}

export default PostComposer
