import type { ReactNode } from 'react'

export default function EmptyState({

  title,
  description,
  icon,
  action,
}: {
  title: string
  description?: string
  icon?: ReactNode
  action?: ReactNode
}) {
  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 p-6">
      <div className="flex items-start gap-4">
        {icon ? <div className="text-cyan-200">{icon}</div> : null}
        <div className="flex-1">
          <div className="text-base font-semibold text-white">{title}</div>
          {description ? <div className="mt-1 text-sm text-slate-300">{description}</div> : null}
          {action ? <div className="mt-4">{action}</div> : null}
        </div>
      </div>
    </div>
  )
}

