import { motion } from 'framer-motion'
import Card from '../../components/ui/Card.tsx'
import EmptyState from '../../components/ui/EmptyState'

export default function InstructorDashboardPlaceholder() {
  return (
    <motion.div initial={{ opacity: 0, y: 8 }} animate={{ opacity: 1, y: 0 }} className="space-y-4">
      <Card>
        <div className="flex items-start justify-between gap-4">
          <div>
            <h1 className="text-2xl font-bold text-white">INSTRUCTOR Dashboard</h1>
            <p className="mt-1 text-sm text-slate-300">Coming soon · base UI lista para clases y rutinas.</p>
          </div>
          <div className="h-10 w-10 rounded-2xl bg-gradient-to-br from-cyan-500/40 to-fuchsia-500/30" />
        </div>
      </Card>

      <EmptyState
        title="Sección en construcción"
        description="Pronto conectaremos rutinas, progreso y asistencia (sin lógica de negocio todavía)."
      />
    </motion.div>
  )
}

