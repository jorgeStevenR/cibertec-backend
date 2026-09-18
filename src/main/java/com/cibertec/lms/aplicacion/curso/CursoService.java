package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.lms.aplicacion.programa.ProgramaService;
import com.cibertec.lms.dominio.comun.Pagina;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.CursoRepositorio;
import com.cibertec.lms.dominio.excepcion.CursoNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;
import com.cibertec.lms.dominio.usuario.Usuario;
import com.cibertec.lms.dominio.usuario.UsuarioRepositorio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepositorio cursoRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProgramaService programaService;
    // Repositorio (no MatriculaService) para no crear una dependencia circular: MatriculaService ya depende de este servicio.
    private final MatriculaRepositorio matriculaRepositorio;

    public Curso crear(CrearCursoComando comando) {
        Usuario profesor = usuarioRepositorio.buscarPorId(comando.profesorId())
                .orElseThrow(() -> new ReglaNegocioException("El profesor indicado no existe"));

        if (profesor.getRol() != Rol.PROFESOR) {
            throw new ReglaNegocioException("El usuario indicado no tiene rol PROFESOR");
        }

        programaService.buscarPorId(comando.programaId());

        exigirDatosAdministrativos(comando.codigo(), comando.periodo(), comando.cupoMaximo(), comando.fechaInicio(), comando.fechaFin());
        validarFechas(comando.fechaInicio(), comando.fechaFin());
        String codigo = normalizarCodigo(comando.codigo());
        if (cursoRepositorio.existePorCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un curso con el código " + codigo);
        }

        Curso curso = Curso.builder()
                .titulo(comando.titulo())
                .descripcion(comando.descripcion())
                .profesorId(comando.profesorId())
                .programaId(comando.programaId())
                .publicado(false)
                .fechaCreacion(LocalDateTime.now())
                .codigo(codigo)
                .periodo(comando.periodo().trim())
                .cupoMaximo(comando.cupoMaximo())
                .fechaInicio(comando.fechaInicio())
                .fechaFin(comando.fechaFin())
                .build();

        return cursoRepositorio.guardar(curso);
    }

    public Curso buscarPorId(Long id) {
        return cursoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new CursoNoEncontradoException(id));
    }

    /** Solo dentro de una transacción: toma un bloqueo de escritura sobre el curso hasta que ella termine. */
    public Curso buscarPorIdBloqueando(Long id) {
        return cursoRepositorio.buscarPorIdBloqueando(id)
                .orElseThrow(() -> new CursoNoEncontradoException(id));
    }

    public Pagina<Curso> listarPaginado(int pagina, int tamano, Long profesorId, Long programaId) {
        return cursoRepositorio.listarPaginado(pagina, tamano, profesorId, programaId);
    }

    public int contarMatriculados(Long cursoId) {
        return matriculaRepositorio.contarPorCurso(cursoId);
    }

    /** Una sola consulta agrupada para todos los cursos dados; los que no tienen matrículas no aparecen en el mapa. */
    public Map<Long, Integer> contarMatriculadosPorCurso(Collection<Long> cursoIds) {
        return matriculaRepositorio.contarPorCursos(cursoIds);
    }

    /**
     * ADMINISTRADOR: reemplazo completo; debe enviar codigo, periodo, fechaInicio y fechaFin (cupoMaximo null = sin límite).
     * PROFESOR (solo sus cursos): puede editar titulo, descripcion y publicado. Si envía un dato administrativo (codigo, periodo,
     * cupoMaximo, fechaInicio, fechaFin) distinto del actual responde 403, igual que con el programa; los que no envíe (null) o
     * envíe iguales se ignoran y se conservan los existentes.
     */
    @Transactional
    public Curso actualizar(Long id, ActualizarCursoComando comando, Long usuarioActualId, Rol rolActual) {
        // Con bloqueo para no competir con una matrícula concurrente al comparar el cupo con los matriculados.
        Curso existente = buscarPorIdBloqueando(id);
        validarPropietario(existente, usuarioActualId, rolActual);

        Long programaId = comando.programaId();
        String codigo;
        String periodo;
        Integer cupoMaximo;
        LocalDate fechaInicio;
        LocalDate fechaFin;

        if (rolActual == Rol.PROFESOR) {
            if (!programaId.equals(existente.getProgramaId())) {
                throw new AccessDeniedException("Solo un administrador puede cambiar el programa de un curso");
            }
            if (cambiaDatosAdministrativos(existente, comando)) {
                throw new AccessDeniedException("Solo un administrador puede cambiar el código, período, cupo o fechas de un curso");
            }
            codigo = existente.getCodigo();
            periodo = existente.getPeriodo();
            cupoMaximo = existente.getCupoMaximo();
            fechaInicio = existente.getFechaInicio();
            fechaFin = existente.getFechaFin();
        } else {
            exigirDatosAdministrativos(comando.codigo(), comando.periodo(), comando.cupoMaximo(), comando.fechaInicio(), comando.fechaFin());
            validarFechas(comando.fechaInicio(), comando.fechaFin());
            String codigoNuevo = normalizarCodigo(comando.codigo());
            cursoRepositorio.buscarPorCodigo(codigoNuevo)
                    .filter(otro -> !otro.getId().equals(existente.getId()))
                    .ifPresent(otro -> {
                        throw new ReglaNegocioException("Ya existe un curso con el código " + codigoNuevo);
                    });
            codigo = codigoNuevo;
            periodo = comando.periodo().trim();
            cupoMaximo = comando.cupoMaximo();
            fechaInicio = comando.fechaInicio();
            fechaFin = comando.fechaFin();

            if (cupoMaximo != null) {
                int matriculados = matriculaRepositorio.contarPorCurso(id);
                if (cupoMaximo < matriculados) {
                    throw new ReglaNegocioException(
                            "No se puede reducir el cupo máximo a %d: el curso ya tiene %s matriculados"
                                    .formatted(cupoMaximo, estudiantes(matriculados)));
                }
            }
        }
        programaService.buscarPorId(programaId);

        Curso actualizado = Curso.builder()
                .id(existente.getId())
                .titulo(comando.titulo())
                .descripcion(comando.descripcion())
                .profesorId(existente.getProfesorId())
                .programaId(programaId)
                .publicado(comando.publicado())
                .fechaCreacion(existente.getFechaCreacion())
                .codigo(codigo)
                .periodo(periodo)
                .cupoMaximo(cupoMaximo)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();

        return cursoRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id) {
        buscarPorId(id);
        cursoRepositorio.eliminar(id);
    }

    /** El administrador puede administrar cualquier curso; el profesor solo el suyo. */
    public void validarPropietario(Curso curso, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.PROFESOR && !curso.getProfesorId().equals(usuarioActualId)) {
            throw new AccessDeniedException("Solo puedes administrar tus propios cursos");
        }
    }

    private static boolean cambiaDatosAdministrativos(Curso existente, ActualizarCursoComando comando) {
        return (comando.codigo() != null && !Objects.equals(normalizarCodigo(comando.codigo()), existente.getCodigo()))
                || (comando.periodo() != null && !Objects.equals(comando.periodo().trim(), existente.getPeriodo()))
                || (comando.cupoMaximo() != null && !Objects.equals(comando.cupoMaximo(), existente.getCupoMaximo()))
                || (comando.fechaInicio() != null && !Objects.equals(comando.fechaInicio(), existente.getFechaInicio()))
                || (comando.fechaFin() != null && !Objects.equals(comando.fechaFin(), existente.getFechaFin()));
    }

    /** Los DTO ya lo exigen en el POST; acá se repite porque en el PUT solo el ADMINISTRADOR está obligado (el profesor puede omitirlos). */
    private static void exigirDatosAdministrativos(
            String codigo, String periodo, Integer cupoMaximo, LocalDate fechaInicio, LocalDate fechaFin) {
        if (codigo == null || codigo.isBlank()) {
            throw new SolicitudInvalidaException("El código es obligatorio");
        }
        if (periodo == null || periodo.isBlank()) {
            throw new SolicitudInvalidaException("El período es obligatorio");
        }
        if (fechaInicio == null) {
            throw new SolicitudInvalidaException("La fecha de inicio es obligatoria");
        }
        if (fechaFin == null) {
            throw new SolicitudInvalidaException("La fecha de fin es obligatoria");
        }
        if (cupoMaximo != null && cupoMaximo < 1) {
            throw new SolicitudInvalidaException("El cupo máximo debe ser al menos 1");
        }
    }

    private static void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ReglaNegocioException("La fecha de fin no puede ser anterior a la de inicio");
        }
    }

    /** "1 estudiante" / "3 estudiantes". */
    public static String estudiantes(int cantidad) {
        return cantidad + (cantidad == 1 ? " estudiante" : " estudiantes");
    }

    private static String normalizarCodigo(String codigo) {
        return codigo.trim().toUpperCase(Locale.ROOT);
    }
}
