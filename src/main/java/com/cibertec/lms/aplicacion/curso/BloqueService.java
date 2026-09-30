package com.cibertec.lms.aplicacion.curso;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.BloqueRepositorio;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.PaginaModulo;
import com.cibertec.lms.dominio.curso.TipoBloque;
import com.cibertec.lms.dominio.excepcion.BloqueNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.excepcion.SolicitudInvalidaException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

/**
 * Bloques de contenido, tanto sueltos de un módulo (paginaId null) como de una página. La lógica de creación y
 * orden es la misma; lo único que cambia es el destino y los permisos: los de módulo conservan sus reglas de
 * siempre (profesor dueño o ADMINISTRADOR escriben), los de página siguen las de PaginaModuloService (solo el profesor
 * dueño escribe; lee también el ADMINISTRADOR y el estudiante matriculado).
 */
@Service
@RequiredArgsConstructor
public class BloqueService {

    private final BloqueRepositorio bloqueRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final PaginaModuloService paginaModuloService;
    private final MatriculaRepositorio matriculaRepositorio;

    /** Dónde se crea un bloque: moduloId siempre, paginaId solo si es de página. */
    private record Destino(Long moduloId, Long paginaId, Curso curso) {
    }

    public BloqueContenido crearTexto(Long moduloId, String texto, Long usuarioActualId, Rol rolActual) {
        return crearTexto(destinoModulo(moduloId, usuarioActualId, rolActual), texto);
    }

    public BloqueContenido crearTextoEnPagina(Long paginaId, String texto, Long usuarioActualId, Rol rolActual) {
        return crearTexto(destinoPagina(paginaId, usuarioActualId, rolActual), texto);
    }

    public BloqueContenido crearVideo(Long moduloId, String urlVideo, Long usuarioActualId, Rol rolActual) {
        return crearVideo(destinoModulo(moduloId, usuarioActualId, rolActual), urlVideo);
    }

    public BloqueContenido crearVideoEnPagina(Long paginaId, String urlVideo, Long usuarioActualId, Rol rolActual) {
        return crearVideo(destinoPagina(paginaId, usuarioActualId, rolActual), urlVideo);
    }

    public BloqueContenido subirArchivo(
            Long moduloId, TipoBloque tipo, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp, Long usuarioActualId, Rol rolActual) {
        validarTipoArchivo(tipo);
        return subirArchivo(destinoModulo(moduloId, usuarioActualId, rolActual),
                tipo, nombreOriginal, contenido, tamanio, tipoContenidoHttp);
    }

    public BloqueContenido subirArchivoEnPagina(
            Long paginaId, TipoBloque tipo, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp, Long usuarioActualId, Rol rolActual) {
        validarTipoArchivo(tipo);
        return subirArchivo(destinoPagina(paginaId, usuarioActualId, rolActual),
                tipo, nombreOriginal, contenido, tamanio, tipoContenidoHttp);
    }

    public List<BloqueContenido> listarPorModulo(Long moduloId) {
        moduloService.buscarPorId(moduloId);
        return bloqueRepositorio.listarPorModulo(moduloId);
    }

    public List<BloqueContenido> listarPorPagina(Long paginaId, Long usuarioActualId, Rol rolActual) {
        PaginaModulo pagina = paginaModuloService.buscarPorId(paginaId, usuarioActualId, rolActual);
        return bloqueRepositorio.listarPorPagina(pagina.getId());
    }

    public BloqueContenido actualizarTexto(Long bloqueId, String texto, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        if (bloque.getTipo() != TipoBloque.TEXTO) {
            throw new ReglaNegocioException("Solo los bloques de texto se pueden editar así");
        }
        validarEscrituraDelBloque(bloque, usuarioActualId, rolActual);

        BloqueContenido actualizado = BloqueContenido.builder()
                .id(bloque.getId())
                .moduloId(bloque.getModuloId())
                .paginaId(bloque.getPaginaId())
                .tipo(bloque.getTipo())
                .texto(texto)
                .rutaArchivo(bloque.getRutaArchivo())
                .nombreArchivo(bloque.getNombreArchivo())
                .urlVideo(bloque.getUrlVideo())
                .orden(bloque.getOrden())
                .fechaCreacion(bloque.getFechaCreacion())
                .build();

        return bloqueRepositorio.guardar(actualizado);
    }

    public void reordenar(Long moduloId, List<Long> idsEnOrden, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);
        reordenar(bloqueRepositorio.listarPorModulo(moduloId), idsEnOrden,
                () -> new ReglaNegocioException("La lista de orden no coincide con los bloques del módulo"));
    }

    public void reordenarEnPagina(Long paginaId, List<Long> idsEnOrden, Long usuarioActualId, Rol rolActual) {
        destinoPagina(paginaId, usuarioActualId, rolActual);
        reordenar(bloqueRepositorio.listarPorPagina(paginaId), idsEnOrden,
                () -> new SolicitudInvalidaException("La lista de orden no coincide con los bloques de la página"));
    }

    public String obtenerUrlDescarga(Long bloqueId, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        Curso curso = paginaModuloService.cursoDelModulo(bloque.getModuloId());
        if (bloque.getPaginaId() != null) {
            paginaModuloService.validarLectura(curso, usuarioActualId, rolActual);
        } else {
            validarAcceso(curso, usuarioActualId, rolActual);
        }

        if (bloque.getTipo() != TipoBloque.IMAGEN && bloque.getTipo() != TipoBloque.ARCHIVO) {
            throw new ReglaNegocioException("Este bloque no tiene un archivo descargable");
        }
        return almacenamientoPuerto.generarUrlDescarga(bloque.getRutaArchivo());
    }

    public void eliminar(Long bloqueId, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        validarEscrituraDelBloque(bloque, usuarioActualId, rolActual);

        if (bloque.getTipo() == TipoBloque.IMAGEN || bloque.getTipo() == TipoBloque.ARCHIVO) {
            almacenamientoPuerto.eliminar(bloque.getRutaArchivo());
        }
        bloqueRepositorio.eliminar(bloqueId);
    }

    private BloqueContenido crearTexto(Destino destino, String texto) {
        return bloqueRepositorio.guardar(BloqueContenido.builder()
                .moduloId(destino.moduloId())
                .paginaId(destino.paginaId())
                .tipo(TipoBloque.TEXTO)
                .texto(texto)
                .orden(siguienteOrden(destino))
                .fechaCreacion(LocalDateTime.now())
                .build());
    }

    private BloqueContenido crearVideo(Destino destino, String urlVideo) {
        return bloqueRepositorio.guardar(BloqueContenido.builder()
                .moduloId(destino.moduloId())
                .paginaId(destino.paginaId())
                .tipo(TipoBloque.VIDEO)
                .urlVideo(urlVideo)
                .orden(siguienteOrden(destino))
                .fechaCreacion(LocalDateTime.now())
                .build());
    }

    private BloqueContenido subirArchivo(
            Destino destino, TipoBloque tipo, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp) {

        String carpeta = destino.paginaId() == null
                ? "curso-%d/modulo-%d/bloques".formatted(destino.curso().getId(), destino.moduloId())
                : "curso-%d/modulo-%d/pagina-%d/bloques".formatted(destino.curso().getId(), destino.moduloId(), destino.paginaId());
        String rutaDestino = "%s/%s-%s".formatted(carpeta, UUID.randomUUID(), nombreOriginal);
        almacenamientoPuerto.subir(rutaDestino, contenido, tamanio, tipoContenidoHttp);

        return bloqueRepositorio.guardar(BloqueContenido.builder()
                .moduloId(destino.moduloId())
                .paginaId(destino.paginaId())
                .tipo(tipo)
                .rutaArchivo(rutaDestino)
                .nombreArchivo(nombreOriginal)
                .orden(siguienteOrden(destino))
                .fechaCreacion(LocalDateTime.now())
                .build());
    }

    private void reordenar(List<BloqueContenido> actuales, List<Long> idsEnOrden, Supplier<RuntimeException> siNoCoincide) {
        Map<Long, BloqueContenido> porId = actuales.stream()
                .collect(java.util.stream.Collectors.toMap(BloqueContenido::getId, b -> b));

        if (idsEnOrden.size() != actuales.size()
                || new HashSet<>(idsEnOrden).size() != idsEnOrden.size()
                || !porId.keySet().containsAll(idsEnOrden)) {
            throw siNoCoincide.get();
        }

        for (int i = 0; i < idsEnOrden.size(); i++) {
            BloqueContenido actual = porId.get(idsEnOrden.get(i));
            BloqueContenido actualizado = BloqueContenido.builder()
                    .id(actual.getId())
                    .moduloId(actual.getModuloId())
                    .paginaId(actual.getPaginaId())
                    .tipo(actual.getTipo())
                    .texto(actual.getTexto())
                    .rutaArchivo(actual.getRutaArchivo())
                    .nombreArchivo(actual.getNombreArchivo())
                    .urlVideo(actual.getUrlVideo())
                    .orden(i)
                    .fechaCreacion(actual.getFechaCreacion())
                    .build();
            bloqueRepositorio.guardar(actualizado);
        }
    }

    private BloqueContenido buscarPorId(Long id) {
        return bloqueRepositorio.buscarPorId(id)
                .orElseThrow(() -> new BloqueNoEncontradoException(id));
    }

    private int siguienteOrden(Destino destino) {
        List<BloqueContenido> hermanos = destino.paginaId() == null
                ? bloqueRepositorio.listarPorModulo(destino.moduloId())
                : bloqueRepositorio.listarPorPagina(destino.paginaId());
        return hermanos.size();
    }

    private static void validarTipoArchivo(TipoBloque tipo) {
        if (tipo != TipoBloque.IMAGEN && tipo != TipoBloque.ARCHIVO) {
            throw new ReglaNegocioException("El tipo debe ser IMAGEN o ARCHIVO para subir un archivo");
        }
    }

    private Destino destinoModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        return new Destino(moduloId, null, validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual));
    }

    private Destino destinoPagina(Long paginaId, Long usuarioActualId, Rol rolActual) {
        PaginaModulo pagina = paginaModuloService.buscarPorId(paginaId);
        Curso curso = paginaModuloService.cursoDelModulo(pagina.getModuloId());
        paginaModuloService.validarEscritura(curso, usuarioActualId, rolActual);
        return new Destino(pagina.getModuloId(), pagina.getId(), curso);
    }

    private void validarEscrituraDelBloque(BloqueContenido bloque, Long usuarioActualId, Rol rolActual) {
        Curso curso = paginaModuloService.cursoDelModulo(bloque.getModuloId());
        if (bloque.getPaginaId() != null) {
            paginaModuloService.validarEscritura(curso, usuarioActualId, rolActual);
        } else {
            cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        }
    }

    private Curso validarPropietarioDelModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        return curso;
    }

    private void validarAcceso(Curso curso, Long usuarioActualId, Rol rolActual) {
        if (rolActual == Rol.ESTUDIANTE) {
            if (!matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), usuarioActualId)) {
                throw new AccessDeniedException("No estás matriculado en este curso");
            }
            return;
        }
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
    }
}
