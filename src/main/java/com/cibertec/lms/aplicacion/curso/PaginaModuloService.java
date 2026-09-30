package com.cibertec.lms.aplicacion.curso;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.BloqueRepositorio;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.ModuloRepositorio;
import com.cibertec.lms.dominio.curso.PaginaModulo;
import com.cibertec.lms.dominio.curso.PaginaModuloRepositorio;
import com.cibertec.lms.dominio.curso.TipoBloque;
import com.cibertec.lms.dominio.excepcion.ModuloNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.PaginaModuloNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

/**
 * Páginas de un módulo. Depende de ModuloRepositorio (no de ModuloService) porque ModuloService necesita a este
 * servicio para borrar en cascada las páginas de un módulo; y de BloqueRepositorio (no de BloqueService) porque
 * BloqueService usa este servicio para los bloques de página.
 *
 * Permisos: escribir solo el PROFESOR dueño del curso (el ADMINISTRADOR no crea contenido); leer el profesor
 * dueño, cualquier ADMINISTRADOR y los ESTUDIANTES matriculados.
 */
@Service
@RequiredArgsConstructor
public class PaginaModuloService {

    private static final Logger log = LoggerFactory.getLogger(PaginaModuloService.class);

    private final PaginaModuloRepositorio paginaModuloRepositorio;
    private final ModuloRepositorio moduloRepositorio;
    private final BloqueRepositorio bloqueRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;

    public List<PaginaModulo> listarPorModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Curso curso = cursoDelModulo(moduloId);
        validarLectura(curso, usuarioActualId, rolActual);
        return paginaModuloRepositorio.listarPorModulo(moduloId);
    }

    public PaginaModulo buscarPorId(Long id, Long usuarioActualId, Rol rolActual) {
        PaginaModulo pagina = buscarPorId(id);
        validarLectura(cursoDelModulo(pagina.getModuloId()), usuarioActualId, rolActual);
        return pagina;
    }

    public PaginaModulo crear(Long moduloId, String titulo, Long usuarioActualId, Rol rolActual) {
        validarEscritura(cursoDelModulo(moduloId), usuarioActualId, rolActual);

        int siguienteOrden = paginaModuloRepositorio.listarPorModulo(moduloId).stream()
                .mapToInt(PaginaModulo::getOrden)
                .max()
                .orElse(0) + 1;

        PaginaModulo nueva = PaginaModulo.builder()
                .moduloId(moduloId)
                .titulo(titulo.trim())
                .orden(siguienteOrden)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return paginaModuloRepositorio.guardar(nueva);
    }

    public PaginaModulo actualizar(Long id, String titulo, Long usuarioActualId, Rol rolActual) {
        PaginaModulo existente = buscarPorId(id);
        validarEscritura(cursoDelModulo(existente.getModuloId()), usuarioActualId, rolActual);

        PaginaModulo actualizada = PaginaModulo.builder()
                .id(existente.getId())
                .moduloId(existente.getModuloId())
                .titulo(titulo.trim())
                .orden(existente.getOrden())
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return paginaModuloRepositorio.guardar(actualizada);
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        PaginaModulo pagina = buscarPorId(id);
        validarEscritura(cursoDelModulo(pagina.getModuloId()), usuarioActualId, rolActual);
        eliminarConBloques(pagina.getId());
    }

    /** Orden 1..N según la posición en la lista; la lista debe traer exactamente las páginas del módulo, sin repetir. */
    public void reordenar(Long moduloId, List<Long> idsEnOrden, Long usuarioActualId, Rol rolActual) {
        validarEscritura(cursoDelModulo(moduloId), usuarioActualId, rolActual);

        Map<Long, PaginaModulo> porId = paginaModuloRepositorio.listarPorModulo(moduloId).stream()
                .collect(Collectors.toMap(PaginaModulo::getId, Function.identity()));

        if (idsEnOrden.size() != porId.size()
                || new HashSet<>(idsEnOrden).size() != idsEnOrden.size()
                || !porId.keySet().containsAll(idsEnOrden)) {
            throw new SolicitudInvalidaException("La lista de orden no coincide con las páginas del módulo");
        }

        for (int i = 0; i < idsEnOrden.size(); i++) {
            PaginaModulo actual = porId.get(idsEnOrden.get(i));
            paginaModuloRepositorio.guardar(PaginaModulo.builder()
                    .id(actual.getId())
                    .moduloId(actual.getModuloId())
                    .titulo(actual.getTitulo())
                    .orden(i + 1)
                    .fechaCreacion(actual.getFechaCreacion())
                    .build());
        }
    }

    /** Borrado en cascada al eliminar un módulo; el permiso ya lo validó quien llama. */
    public void eliminarPorModulo(Long moduloId) {
        paginaModuloRepositorio.listarPorModulo(moduloId).forEach(p -> eliminarConBloques(p.getId()));
    }

    /** Sin validar permisos: para uso de BloqueService, que decide si hace falta lectura o escritura. */
    public PaginaModulo buscarPorId(Long id) {
        return paginaModuloRepositorio.buscarPorId(id)
                .orElseThrow(() -> new PaginaModuloNoEncontradaException(id));
    }

    public Curso cursoDelModulo(Long moduloId) {
        Modulo modulo = moduloRepositorio.buscarPorId(moduloId)
                .orElseThrow(() -> new ModuloNoEncontradoException(moduloId));
        return cursoService.buscarPorId(modulo.getCursoId());
    }

    public void validarEscritura(Curso curso, Long usuarioActualId, Rol rolActual) {
        if (rolActual != Rol.PROFESOR || !curso.getProfesorId().equals(usuarioActualId)) {
            throw new AccessDeniedException("Solo el profesor del curso puede editar sus páginas");
        }
    }

    public void validarLectura(Curso curso, Long usuarioActualId, Rol rolActual) {
        boolean permitido = switch (rolActual) {
            case ADMINISTRADOR -> true;
            case PROFESOR -> curso.getProfesorId().equals(usuarioActualId);
            case ESTUDIANTE -> matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), usuarioActualId);
            default -> false;
        };
        if (!permitido) {
            throw new AccessDeniedException("No tienes acceso a las páginas de este curso");
        }
    }

    /**
     * Borra primero las filas (bloques y página) y recién después los archivos del storage: si falla el storage
     * queda un archivo huérfano (inofensivo), en vez de un bloque que apunta a un archivo que ya no existe.
     */
    private void eliminarConBloques(Long paginaId) {
        List<String> rutas = bloqueRepositorio.listarPorPagina(paginaId).stream()
                .filter(b -> b.getTipo() == TipoBloque.IMAGEN || b.getTipo() == TipoBloque.ARCHIVO)
                .map(BloqueContenido::getRutaArchivo)
                .toList();

        bloqueRepositorio.eliminarPorPagina(paginaId);
        paginaModuloRepositorio.eliminar(paginaId);

        for (String ruta : rutas) {
            try {
                almacenamientoPuerto.eliminar(ruta);
            } catch (RuntimeException e) {
                log.warn("No se pudo borrar del storage el archivo {} de la página {}", ruta, paginaId, e);
            }
        }
    }
}
