# LMS CIBERCTEC — Backend

API REST del LMS de CIBERCTEC (Spring Boot 4 / Java 21, PostgreSQL en Supabase, archivos en Supabase Storage).
Roles: ADMINISTRADOR, GESTOR_USUARIOS, PROFESOR y ESTUDIANTE. El frontend (Angular) vive en otro repositorio.

## Correr en local

Requisitos: Java 21.

1. Crear `src/main/resources/application-local.yml` (no se sube a git) a partir de `application-local.yml.example`, con la base de Supabase, el secreto JWT y las claves de Storage.
2. `./mvnw spring-boot:run` (en Windows `mvnw.cmd spring-boot:run`).
3. Probar: `http://localhost:8080/actuator/health` debe responder `{"status":"UP"}`.

Tests: `./mvnw test`. `LmsApplicationTests` levanta el contexto contra la base real: apagar el servidor antes (el pooler de Supabase admite 15 conexiones).

## Docker

```bash
docker build -t cibertec-lms-backend .
docker run --env-file mis-variables.env -p 8080:8080 cibertec-lms-backend
```

La imagen corre con el perfil `prod`: **todo** se configura con variables de entorno (tabla de abajo) y si falta una obligatoria el servidor no arranca.

## Desplegar en Render

1. New → **Web Service** → conectar este repositorio. Runtime: **Docker** (usa el `Dockerfile` de la raíz). También se puede usar **Blueprint** con `render.yaml`.
2. Health check path: `/actuator/health`.
3. Cargar en *Environment* estas variables:

| Variable | Obligatoria | Valor |
|---|---|---|
| `SPRING_DATASOURCE_URL` | sí | `jdbc:postgresql://aws-0-us-west-2.pooler.supabase.com:5432/postgres` |
| `SPRING_DATASOURCE_USERNAME` | sí | usuario de Supabase (`postgres.<ref>`) |
| `SPRING_DATASOURCE_PASSWORD` | sí | contraseña de la base |
| `LMS_JWT_SECRET` | sí | texto aleatorio largo (≥ 32 caracteres) |
| `LMS_CORS_ORIGENES` | sí | URL pública del frontend, ej. `https://cibertec-lms.onrender.com` (varias separadas por coma) |
| `LMS_ALMACENAMIENTO_ENDPOINT` | sí | `https://<ref>.storage.supabase.co/storage/v1/s3` |
| `LMS_ALMACENAMIENTO_ACCESS_KEY_ID` | sí | clave S3 de Supabase Storage |
| `LMS_ALMACENAMIENTO_SECRET_ACCESS_KEY` | sí | secreto S3 de Supabase Storage |
| `LMS_ADMIN_PASSWORD` / `LMS_GESTOR_PASSWORD` | sí | contraseña inicial de esas cuentas (solo se usa si todavía no existen en la base) |
| `DB_POOL_MAX` | no | conexiones a la base (por defecto 5) |
| `PORT` | no | lo pone Render solo |

Después de desplegar, la URL del backend (`https://<servicio>.onrender.com/api`) es la que se configura en el frontend.

Notas:
- El plan gratuito de Render "duerme" el servicio sin tráfico: la primera petición después tarda ~1 minuto.
- Si el backend local y el desplegado usan la misma base de Supabase, comparten el límite de 15 conexiones.
- Decisiones de diseño, cambios de contrato e historial: ver `HISTORIAL.md`.
