import { AnimatePresence, motion } from 'framer-motion'
import { useAuthStore } from '../../../store/authStore'
import Sidebar from '../SideNav/Sidebar'

export default function MobileSidebarDrawer({
  open,
  onOpenChange,
}: {
  open: boolean
  onOpenChange: (v: boolean) => void
}) {
  const role = useAuthStore((s) => s.role)

  return (
    <AnimatePresence>
      {open ? (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 md:hidden"
        >
          <button
            className="absolute inset-0 bg-black/70"
            onClick={() => onOpenChange(false)}
            aria-label="Close menu"
          />

          <motion.div
            initial={{ x: -20, opacity: 0 }}
            animate={{ x: 0, opacity: 1 }}
            exit={{ x: -20, opacity: 0 }}
            transition={{ type: 'spring', stiffness: 300, damping: 30 }}
            className="absolute left-0 top-0 h-full w-72 border-r border-white/10 bg-black/90 backdrop-blur"
          >
            <Sidebar role={role} mobile onNavigate={() => onOpenChange(false)} />
          </motion.div>
        </motion.div>
      ) : null}
    </AnimatePresence>
  )
}

