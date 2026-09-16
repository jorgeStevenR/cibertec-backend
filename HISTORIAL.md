# Historial del proyecto — LMS Backend (CIBERCTEC)

> Se actualiza después de cada feature terminada, no al "final de la sesión" (ese momento no es detectable de forma confiable). El contrato de API completo y siempre-correcto son los controllers en `src/main/java/com/cibertec/lms/web/controllers/` — la sección de abajo es un changelog de cambios recientes, no un catálogo completo.

## Resumen del proyecto

API REST en Java 21 / Spring Boot 4.1 para el LMS de CIBERCTEC: gestión de usuarios, cursos, programas académicos, módulos con contenido (bloques, lienzo tipo Canva, cuestionarios), matrículas y evaluación. Frontend en Angular consume esta API.

## Estado actual

Funcionando y probado en vivo:

- **Usuarios / Auth**: registro, login JWT, roles (ADMINISTRADOR/GESTOR_USUARIOS/PROFESOR/ESTUDIANTE), paginación + búsqueda. `GESTOR_USUARIOS` es el único rol con escritura sobre `/api/usuarios` (crear/editar/eliminar/resetear password), limitado a cuentas PROFESOR/ESTUDIANTE; ADMINISTRADOR quedó de solo lectura ahí (lo sigue necesitando para elegir profesor/estudiante al crear cursos y matrículas), pero conserva el resto de la API sin cambios (programas, cursos, matrículas, etc.).
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
- **Excepción propia para mensajes 403 específicos**: `AccessDeniedException` en este proyecto siempre devuelve el mismo mensaje genérico ("No tienes permiso..."), por diseño del `GlobalExceptionHandler`. Cuando un caso necesita un mensaje 403 distinto (ventana de disponibilidad de un cuestionario: "todavía no abrió" vs "ya venció"; ahora también `GestionCuentaNoPermitidaException` para GESTOR_USUARIOS), se crea una excepción de dominio propia con su propio `@ExceptionHandler`, en vez de forzar el mensaje genérico.
- **Cómo se crean cuentas ADMINISTRADOR/GESTOR_USUARIOS**: ninguno de esos dos roles se puede asignar vía `/api/usuarios` (ni al crear ni al editar — `UsuarioService.validarRolGestionable` lo rechaza con 403 sin importar quién llame). El único mecanismo es un `CommandLineRunner` seeder por rol (`AdminSeeder`, `GestorUsuariosSeeder`) que crea la cuenta al arrancar si el email configurado (`lms.admin.*` / `lms.gestor.*`) todavía no existe — mismo patrón, ya existía para el admin. Si hace falta una segunda cuenta de alguno de estos roles, se inserta directo en la base (no hay Flyway/Liquibase todavía); no hay endpoint para esto a propósito, para no abrir una puerta de escalamiento de privilegios en la API.
- **`ddl-auto: update` no actualiza CHECK constraints de un enum existente**: al agregar `GESTOR_USUARIOS` a `Rol`, el arranque se cayó al insertar la fila seed — Postgres rechazó el insert con `violates check constraint "usuarios_rol_check"`. Hibernate había generado ese constraint automáticamente al crear la tabla `usuarios` (columna `rol` con `@Enumerated(STRING)`), listando los 3 valores que existían en ese momento; `ddl-auto: update` agrega columnas/tablas nuevas pero no toca constraints ya creados, así que quedó desincronizado con el enum de Java. Se corrigió a mano en Supabase (`DROP CONSTRAINT` + `ADD CONSTRAINT` con la lista de roles actualizada) — **hay que repetir este mismo ajuste manual cada vez que se agregue un valor nuevo a `Rol`** (o a cualquier otro enum mapeado con `@Enumerated(STRING)` en una tabla ya existente), si no el seeder/insert correspondiente va a fallar igual en el próximo arranque.
- **Sin relaciones JPA**: todo el proyecto usa entidades planas enlazadas por FK manual (`Long xxxId`) + mappers manuales, nunca `@OneToMany`/`@ManyToOne`. Se mantuvo esa convención en todas las features nuevas (Lienzo, Programa, Cuestionario) aunque JPA lo hubiera hecho más corto.
- **`puntajeMaximo` de un Cuestionario no se guarda como columna**: se calcula sumando el puntaje de las preguntas cada vez que se pide, para que nunca quede desincronizado tras editar/borrar preguntas. En cambio, el `puntajeMaximo` de un `IntentoCuestionario` **sí** se guarda como copia fija al momento de responder, para que la nota histórica de un estudiante no cambie de escala si el profesor edita el cuestionario después.

## Cambios recientes al contrato de API

**2026-09-16 — Rol GESTOR_USUARIOS (rompe permisos previos de /api/usuarios)**
- Nuevo valor de enum `Rol`: `GESTOR_USUARIOS` (además de ADMINISTRADOR, PROFESOR, ESTUDIANTE).
- `POST/PUT/DELETE /api/usuarios/{id}` y `POST /api/usuarios/{id}/resetear-password`: ya no los puede llamar ADMINISTRADOR (ahora dan 403) — pasan a ser exclusivos de GESTOR_USUARIOS.
- `GET /api/usuarios` y `GET /api/usuarios/{id}`: ahora los puede llamar ADMINISTRADOR o GESTOR_USUARIOS (sin cambios para ADMINISTRADOR).
- `POST /api/usuarios` y `PUT /api/usuarios/{id}`: si el `rol` del body es ADMINISTRADOR o GESTOR_USUARIOS, 403 con mensaje `"No tienes permiso para gestionar cuentas con este rol."` (`GestionCuentaNoPermitidaException`). Mismo 403 si `PUT`/`DELETE`/reseteo apuntan a un usuario que YA es ADMINISTRADOR o GESTOR_USUARIOS.
- Cuentas ADMINISTRADOR/GESTOR_USUARIOS ya no se pueden crear por API bajo ninguna combinación de rol/endpoint — ver "Decisiones importantes" (seeders `AdminSeeder`/`GestorUsuariosSeeder`).
- Pendiente coordinar con frontend Angular: pantalla de Usuarios del panel ADMINISTRADOR pasa a solo lectura; falta el panel nuevo de GESTOR_USUARIOS (con su guard de ruta) — no implementado en esta sesión, solo el backend.

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

### 2026-09-16
- Diagnosticado un falso "el server no arranca": en realidad tardaba ~50s en levantar (conexión a Postgres remoto en Supabase) y la salida de `mvnw.cmd spring-boot:run` quedaba bufferizada en Windows hasta el cierre del proceso — no había ningún bug.
- Implementado el rol `GESTOR_USUARIOS`: separa la gestión de cuentas (`/api/usuarios`) del rol ADMINISTRADOR, que la pierde por completo salvo lectura. GESTOR_USUARIOS solo puede tocar cuentas PROFESOR/ESTUDIANTE (no ADMINISTRADOR ni GESTOR_USUARIOS, ni para crear ni para reasignar rol). Cuentas de esos dos roles privilegiados se siembran por `CommandLineRunner` (`GestorUsuariosSeeder`, mismo patrón que `AdminSeeder` ya existente), no por API.

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
