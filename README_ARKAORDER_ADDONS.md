# ArkaOrder – JWT Sync, Historial de Usuarios y CI/CD

## JWT sincronizado
- El filtro `JwtAuthFilter` valida el token `Bearer` con `security.jwt.secret` (mismo secreto que `user-service`).
- `FeignAuthForwardConfig` arrastra el header `Authorization` hacia `user-service` y `product-service`.
- Ajusta `application.yml` con `security.jwt.secret` real.

## Endpoints nuevos
- `GET /api/orders/history/order/{orderId}`: historial por orden (requiere autenticación).
- `GET /api/orders/history/users?userId=...`: historial por usuario (ROLE_ADMIN).
- `GET /api/orders/reports/weekly.csv?from=YYYY-MM-DD&to=YYYY-MM-DD`: reporte semanal CSV (ROLE_ADMIN).

## Eventos registrados
- CREATE: al crear una orden.
- UPDATE: al actualizar.
- STATUS_CHANGE: cambio de estado.

## CI/CD
- Workflow en `.github/workflows/ci.yml` (Gradle + Docker).
- Variables/Secrets necesarios en GitHub:
  - `DOCKERHUB_USERNAME`
  - `DOCKERHUB_TOKEN`

## Docker
```bash
./gradlew bootJar
docker build -t youruser/arkaorder:local .
docker run -p 8082:8082 youruser/arkaorder:local
```

## Notas
- Si usas `@PreAuthorize`, el JWT debe contener roles (claim `roles`).
- Hibernate creará `order_history` automáticamente si `ddl-auto=update`.
