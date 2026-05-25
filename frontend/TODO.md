# TODO - Módulo de Autenticación (Frontend)

- [x] Entender estructura actual (App.tsx vacío, sin router/guards existentes)
- [x] Crear tipos de auth en `src/types/auth.ts`
- [x] Crear helpers de persistencia en `src/utils/storage.ts`
- [x] Crear Axios base en `src/api/http.ts`
- [x] Crear API auth en `src/api/authApi.ts`
- [x] Crear interceptores JWT + refresh automático en `src/api/interceptors.ts`
- [x] Crear store Zustand de autenticación en `src/store/authStore.ts`
- [x] Crear `ProtectedRoute` en `src/routes/ProtectedRoute.tsx`
- [x] Crear `RoleGuard` en `src/routes/RoleGuard.tsx`
- [x] Crear `AuthProvider` en `src/app/providers/AuthProvider.tsx` (bootstrap)
- [x] Crear `AppRouter` en `src/app/router/AppRouter.tsx` (rutas auth + protected + redirección por rol)
- [x] Crear `LoginPage` premium en `src/features/auth/pages/LoginPage.tsx`
- [ ] Ajustar setup de `interceptors.ts` para que no deje errores TypeScript (si aparecen)
- [ ] Ejecutar `npm run dev` y validar flujo login/refresh/logout

