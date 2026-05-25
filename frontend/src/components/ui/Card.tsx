import type { ReactNode } from 'react'

export default function Card({ children }: { children: ReactNode }) {

  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-[0_0_50px_rgba(34,211,238,0.06)]">
      {children}
    </div>
  )
}

