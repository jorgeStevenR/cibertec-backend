# LMS Backend — CIBERCTEC

Backend Spring Boot 4.1.1 / Java 21 para el LMS. Trabajá acá con criterio de desarrollador Java senior especializado en Spring Boot: priorizá corrección, claridad y consistencia con lo que ya existe por sobre "hacerlo andar" a secas.

**Antes de empezar a trabajar, leé `HISTORIAL.md`** (raíz del proyecto) — tiene el estado actual, decisiones tomadas y por qué, y los cambios recientes al contrato de API. Después de terminar una feature (no al "final de la sesión" — ese momento no es detectable), actualizalo vos mismo: no esperes a que te lo pidan.

## Arquitectura (ya establecida — seguila, no la reinventes)

Cada feature se organiza en 4 paquetes paralelos, uno por capa:

- `dominio/<feature>/` — clases planas (Lombok `@Getter @Builder @AllArgsConstructor`), interfaces de repositorio (`XxxRepositorio`), sin ninguna dependencia de Spring/JPA/web. Es el núcleo, no sabe que existe HTTP ni Hibernate.
- `dominio/excepcion/` — excepciones de dominio. `RecursoNoEncontradoException` (abstracta) → 404, `ReglaNegocioException` → 409, `AccessDeniedException` de Spring Security → 403 con mensaje genérico fijo. Si un caso necesita un mensaje 403 específico (no el genérico), no uses `AccessDeniedException` — creá una excepción propia con su propio `@ExceptionHandler` en `GlobalExceptionHandler` (ver `VentanaCuestionarioException` como referencia).
- `aplicacion/<feature>/` — `@Service`, un service por agregado/recurso REST (no metas todo en un service gigante: mirá cómo `Actividad`/`Entrega` o `Cuestionario`/`Pregunta`/`IntentoCuestionario` son servicios separados aunque estén relacionados). Acá vive toda la validación de permisos (dueño del curso, matriculado, rol) y las reglas de negocio. Cuidado con las dependencias circulares entre servicios de la misma feature — si dos servicios se necesitarían mutuamente, uno de los dos debe depender del *repositorio* del otro en vez de su *service* (ver comentario en `CuestionarioService`).
- `infraestructura/persistencia/<feature>/` — `XxxJpaEntity` (JPA puro, sin lógica), `XxxJpaRepository` (Spring Data, preferir `@Query` en JPQL explícito antes que derivar métodos raros por nombre — mantiene las queries portables y legibles), `XxxMapper` (clase estática, conversión manual entidad↔dominio, sin librería de mapeo), `XxxRepositorioImpl` (`@Repository` package-private, implementa la interfaz de `dominio`). **No se usan relaciones JPA (`@OneToMany`/`@ManyToOne`)** en este proyecto — todo son entidades planas enlazadas por FK manual (`Long xxxId`), con el repositorio orquestando las escrituras relacionadas (ver `PreguntaRepositorioImpl` para el patrón de reemplazo total de una colección hija con diff de ids).
- `web/controllers/` + `web/dtos/` — `@RestController`, DTOs como `record` de Java. Un DTO de request se reutiliza para crear y actualizar cuando el shape es idéntico; si difieren (ej. `CursoRequestDTO` vs `CursoActualizarRequestDTO`), se separan. Los DTO de respuesta resuelven datos de otras entidades (nombre de profesor, nombre de programa, etc.) en un factory `desde(...)` que recibe el objeto ya resuelto — la resolución (repo lookup) pasa en el controller o el service, nunca dentro del propio record.

### Seguridad y permisos

- `@PreAuthorize("hasRole('ADMINISTRADOR')")` / `hasAnyRole(...)` a nivel de método del controller para reglas de rol simples.
- Reglas más finas (dueño del curso, estudiante matriculado) se validan **dentro del service**, no en el controller — patrón repetido: resolver `moduloId → cursoId → profesorId` y comparar contra `usuarioActualId`, o `matriculaRepositorio.existePorCursoYEstudiante(...)`.
- Nunca expongas del lado del servidor algo que el rol no debería ver "solo ocultándolo en el frontend" — si un campo es sensible (ej. `esCorrecta` de una opción antes de que el estudiante responda), se omite del JSON server-side (`@JsonInclude(NON_NULL)` + valor `null`), no confíes en que el cliente no lo muestre.
- Nunca dejes que un mensaje de excepción no controlada llegue al cliente con detalle interno — `GlobalExceptionHandler` ya tiene un catch-all que loguea y devuelve un mensaje genérico; no lo rodees con `printStackTrace` ni devuelvas `ex.getMessage()` de una excepción no prevista.

### Jackson 3.x — cuidado con los paquetes

Este proyecto usa Spring Boot 4 → Jackson 3.x. `ObjectMapper`, `JsonNode`, `ObjectNode`, etc. viven en `tools.jackson.databind.*` (no `com.fasterxml.jackson.databind.*`). Las **anotaciones** (`@JsonInclude`, `@JsonProperty`, etc.) siguen en `com.fasterxml.jackson.annotation.*` — Jackson mantuvo ese módulo en su groupId 2.x a propósito. Mezclar mal estos dos genera errores de compilación confusos; si dudás, verificá el paquete real inspeccionando el jar en `~/.m2` antes de asumir.

### Paginación y listados

`Pagina<T>` (dominio, propio) / `PaginaResponseDTO<T>` (web) para cualquier listado paginado — no expongas `org.springframework.data.domain.Page` fuera de la capa de infraestructura. Filtros opcionales de listado son parámetros nullable con precedencia explícita documentada (ver `CursoRepositorioImpl.listarPaginado`), no combines silenciosamente varios filtros sin decidir qué pasa si vienen todos a la vez.

## Checklist al escribir o revisar código acá

- **Capas**: ¿la lógica de negocio está en el service, no en el controller ni en el mapper? ¿el controller solo traduce HTTP↔dominio?
- **Errores**: ¿usás la jerarquía de excepciones existente (`RecursoNoEncontradoException`, `ReglaNegocioException`) en vez de devolver `null`, un booleano de éxito, o un `ResponseEntity` armado a mano en el service?
- **Validación**: ¿los DTO de request tienen `@NotNull`/`@NotBlank`/`@Positive` donde corresponde? ¿una regla cross-field (como "exactamente una opción correcta") está como validador custom de Bean Validation si necesitás que dé 400, no como excepción de servicio que daría 409?
- **Tests**: hoy este proyecto **no tiene tests unitarios de servicios ni controllers** — solo `LmsApplicationTests` (arranque del contexto). Es una deuda real, no asumas cobertura que no existe. Si agregás lógica no trivial (cálculo, validación de reglas, branching de permisos), considerá agregar un test unitario con Mockito para esa clase, no lo des por hecho que "ya se prueba a mano". No es obligatorio en cada cambio chico, pero sí quiero que lo tengas presente y lo ofrezcas cuando la lógica lo amerite.
- **Rendimiento**: ¿una query nueva puede generar N+1 (resolver algo por id dentro de un `.map()` sobre una lista)? Es un patrón ya existente en varios controllers de este proyecto (aceptado como deuda menor dado el volumen de datos actual) — no lo repliques sin pensarlo si la lista puede crecer; para listados grandes preferí resolver en batch o paginar.
- **Nombres**: el dominio y las URLs están en español (`Cuestionario`, `Matricula`, `/api/cuestionarios`), el código Java usa convención estándar. No mezcles inglés a mitad de camino en nombres nuevos.
- **Antes de dar algo por "ya funciona"**: si vas a levantar el servidor para probar en vivo, mirá si ya hay un proceso corriendo en el 8080 (`netstat -ano | grep 8080`) y su `StartTime` — este proyecto reincide en el bug de "el server corre código viejo porque nadie lo reinició tras el último cambio". Ante un 500 genérico o un campo que "no aparece" sin explicación, sospechá primero de esto antes de asumir un bug de código.

## Cosas que NO hacer

- No introduzcas relaciones JPA (`@OneToMany`, `@ManyToOne`, cascadas de Hibernate) — rompe la convención de entidades planas + mappers manuales de todo el proyecto.
- No uses SQL nativo salvo necesidad real — JPQL vía `@Query` es la norma acá.
- No agregues Flyway/Liquibase sin que te lo pidan — el proyecto usa `ddl-auto: update` deliberadamente en esta etapa.
- No captures excepciones solo para loguearlas y volver a lanzarlas sin agregar valor.
- No agregues un campo "por si acaso" a una entidad o DTO sin que lo pida el feature actual.
