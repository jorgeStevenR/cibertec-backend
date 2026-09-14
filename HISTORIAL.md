# Historial del proyecto — LMS Backend (CIBERCTEC)

> Se actualiza después de cada feature terminada, no al "final de la sesión" (ese momento no es detectable de forma confiable). El contrato de API completo y siempre-correcto son los controllers en `src/main/java/com/cibertec/lms/web/controllers/` — la sección de abajo es un changelog de cambios recientes, no un catálogo completo.

## Resumen del proyecto

API REST en Java 21 / Spring Boot 4.1 para el LMS de CIBERCTEC: gestión de usuarios, cursos, programas académicos, módulos con contenido (bloques, lienzo tipo Canva, cuestionarios), matrículas y evaluación. Frontend en Angular consume esta API.

## Estado actual

Funcionando y probado en vivo:

- **Usuarios / Auth**: registro, login JWT, roles (ADMINISTRADOR/PROFESOR/ESTUDIANTE), paginación + búsqueda.
- **Cursos**: CRUD, paginación + búsqueda, filtro por profesor y por programa, vinculado a `Programa` vía `programaId`.
- **Programas**: CRUD (solo admin), no se puede borrar uno con cursos asociados (409).
- **Matrículas**: a curso (existente desde el inicio) y a programa (`MatriculaPrograma`, separada).
- **Módulos**: CRUD dentro de un curso.
- **Bloques de contenido** de módulo: texto / imagen / video / archivo, con reordenamiento.
- **Lienzo** (editor libre tipo Canva) por módulo: **varias hojas** por módulo (no una sola), con texto/imagen/video/personaje; URLs de imagen firmadas al vuelo en cada GET (el bucket es privado).
- **Actividades + Entregas**: talleres con subida de archivo y calificación manual.
- **Cuestionarios**: quiz de opción múltiple con corrección automática, preguntas/opciones editables, ventana de disponibilidad (`fechaApertura`/`fechaEntrega` con campo `disponible` calculado), un solo intento por estudiante.

## Próximos pasos

- **Deuda de tests**: no hay tests unitarios de services/controllers (solo `LmsApplicationTests`, arranque del contexto). Anotado en `CLAUDE.md`, no bloqueante pero real.
- **"Cerrar curso"**: se discutió (bloquear edición/entregas del profesor y estudiante tras cerrar un curso) pero no se implementó — el usuario quería definir el diseño antes de construirlo (ver Decisiones).
- **LSP para Java**: no hay ningún plugin de LSP disponible en el marketplace `anthropics/claude-code` agregado a este proyecto. Si aparece uno en el futuro, agregarlo.

## Decisiones importantes

- **Lienzo — URLs firmadas, no permanentes**: el bucket de Supabase Storage es privado, así que las URLs de imagen expiran a los 15 min. En vez de guardar la URL firmada tal cual, se guarda la ruta y se re-firma una URL fresca en cada `GET` del lienzo — si no, las imágenes se habrían roto solas 15 min después de guardar.
- **MatriculaPrograma no denormaliza nombre/email del estudiante**: el pedido original lo sugería, pero el patrón real ya existente para `Matricula` (curso) resuelve nombre/email al vuelo contra `UsuarioRepositorio` en el DTO de respuesta, no los persiste. Se siguió ese mismo patrón para no arriesgar datos desactualizados si el estudiante cambia su nombre/email.
- **`Curso.programaId` nullable en la base, obligatorio en el DTO**: la columna quedó nullable para no romper los cursos que ya existían antes de este campo; la obligatoriedad se exige solo en la validación de `CursoRequestDTO`/`CursoActualizarRequestDTO` para cursos nuevos/editados.
- **Solo ADMIN puede reasignar el programa de un curso**: si un PROFESOR hace `PUT` de un curso propio cambiando `programaId` a un valor distinto del actual, se rechaza con 403 — el resto de los campos del curso sí los puede editar.
- **Excepción propia para mensajes 403 específicos**: `AccessDeniedException` en este proyecto siempre devuelve el mismo mensaje genérico ("No tienes permiso..."), por diseño del `GlobalExceptionHandler`. Cuando un caso necesita un mensaje 403 distinto (ventana de disponibilidad de un cuestionario: "todavía no abrió" vs "ya venció"), se crea una excepción de dominio propia con su propio `@ExceptionHandler`, en vez de forzar el mensaje genérico.
- **Sin relaciones JPA**: todo el proyecto usa entidades planas enlazadas por FK manual (`Long xxxId`) + mappers manuales, nunca `@OneToMany`/`@ManyToOne`. Se mantuvo esa convención en todas las features nuevas (Lienzo, Programa, Cuestionario) aunque JPA lo hubiera hecho más corto.
- **`puntajeMaximo` de un Cuestionario no se guarda como columna**: se calcula sumando el puntaje de las preguntas cada vez que se pide, para que nunca quede desincronizado tras editar/borrar preguntas. En cambio, el `puntajeMaximo` de un `IntentoCuestionario` **sí** se guarda como copia fija al momento de responder, para que la nota histórica de un estudiante no cambie de escala si el profesor edita el cuestionario después.

## Cambios recientes al contrato de API

**2026-09-14 — Cuestionario: fechaApertura + disponibilidad**
- `POST/PUT` de cuestionario ahora aceptan `fechaApertura` (antes solo `fechaEntrega`).
- `GET` (lista y detalle) ahora devuelven `fechaApertura` y `disponible` (boolean, calculado).
- `GET /api/cuestionarios/{id}`: para un ESTUDIANTE, si el cuestionario todavía no abrió, `preguntas` viene `[]`.
- `POST /api/cuestionarios/{id}/intentos` puede rechazar con 403 y mensaje específico ("todavía no está disponible" / "el plazo ya venció").

**2026-09-14 — Cuestionario (nuevo)**
- 6 endpoints nuevos: `GET/POST /api/modulos/{id}/cuestionarios`, `GET/PUT/DELETE /api/cuestionarios/{id}`, `POST /api/cuestionarios/{id}/preguntas`, `PUT/DELETE /api/preguntas/{id}`, `POST /api/cuestionarios/{id}/intentos`, `GET /api/cuestionarios/{id}/mi-intento`, `GET /api/cuestionarios/{id}/intentos`.
- `esCorrecta` de una opción se omite del JSON (no solo se oculta en frontend) cuando responde un estudiante que no vio la respuesta todavía.

**2026-09-11 — Programa + Curso.programaId**
- 10 endpoints nuevos bajo `/api/programas` y `/api/matriculas-programa`.
- `Curso`: `POST`/`PUT` ahora exigen `programaId`; `GET` devuelve `programaId`/`programaNombre`; nuevo filtro `?programaId=`.

**2026-09-11 — Lienzo: de 1 por módulo a varias hojas**
- Reemplazados `GET/PUT /api/modulos/{id}/lienzo` por 6 endpoints bajo `/api/modulos/{id}/lienzos` y `/api/lienzos/{id}` (una hoja = un id propio, ya no cuelga del módulo directamente).

## Log de sesiones

### 2026-09-11
- Verificado el contrato real de bloques de contenido (texto/imagen/video) contra lo reportado por el frontend — coincidía exactamente, no había bug.
- Implementado lienzo libre por módulo (editor tipo Canva), después migrado a modelo multi-hoja (varias hojas por módulo, como pestañas).
- Implementada entidad `Programa` + `Curso.programaId` + `MatriculaPrograma`.
- Subido todo a git en ambos repos (backend y frontend).

### 2026-09-14
- Diagnosticados y resueltos 2 reportes de bug del frontend (500 genérico, campos "fantasma" en la respuesta) — en ambos casos el servidor local no se había reiniciado tras el último cambio, no eran bugs de código.
- Implementado `Cuestionario`: preguntas de opción múltiple con corrección automática, ocultamiento de la respuesta correcta hasta que el estudiante responde.
- Agregada ventana de disponibilidad (`fechaApertura`/`fechaEntrega`, campo `disponible` calculado) a `Cuestionario`.
- Configurados plugins `code-review` y `security-guidance`; creado `CLAUDE.md` con criterios de desarrollo senior Spring Boot; creado este archivo.
