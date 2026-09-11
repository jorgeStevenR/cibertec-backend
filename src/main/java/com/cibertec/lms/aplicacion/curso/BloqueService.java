package com.cibertec.lms.aplicacion.curso;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.BloqueContenido;
import com.cibertec.lms.dominio.curso.BloqueRepositorio;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.TipoBloque;
import com.cibertec.lms.dominio.excepcion.BloqueNoEncontradoException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BloqueService {

    private final BloqueRepositorio bloqueRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;

    public BloqueContenido crearTexto(Long moduloId, String texto, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        BloqueContenido nuevo = BloqueContenido.builder()
                .moduloId(moduloId)
                .tipo(TipoBloque.TEXTO)
                .texto(texto)
                .orden(siguienteOrden(moduloId))
                .fechaCreacion(LocalDateTime.now())
                .build();

        return bloqueRepositorio.guardar(nuevo);
    }

    public BloqueContenido crearVideo(Long moduloId, String urlVideo, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        BloqueContenido nuevo = BloqueContenido.builder()
                .moduloId(moduloId)
                .tipo(TipoBloque.VIDEO)
                .urlVideo(urlVideo)
                .orden(siguienteOrden(moduloId))
                .fechaCreacion(LocalDateTime.now())
                .build();

        return bloqueRepositorio.guardar(nuevo);
    }

    public BloqueContenido subirArchivo(
            Long moduloId, TipoBloque tipo, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp, Long usuarioActualId, Rol rolActual) {

        if (tipo != TipoBloque.IMAGEN && tipo != TipoBloque.ARCHIVO) {
            throw new ReglaNegocioException("El tipo debe ser IMAGEN o ARCHIVO para subir un archivo");
        }

        Curso curso = validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        String rutaDestino = "curso-%d/modulo-%d/bloques/%s-%s".formatted(
                curso.getId(), moduloId, UUID.randomUUID(), nombreOriginal);
        almacenamientoPuerto.subir(rutaDestino, contenido, tamanio, tipoContenidoHttp);

        BloqueContenido nuevo = BloqueContenido.builder()
                .moduloId(moduloId)
                .tipo(tipo)
                .rutaArchivo(rutaDestino)
                .nombreArchivo(nombreOriginal)
                .orden(siguienteOrden(moduloId))
                .fechaCreacion(LocalDateTime.now())
                .build();

        return bloqueRepositorio.guardar(nuevo);
    }

    public List<BloqueContenido> listarPorModulo(Long moduloId) {
        moduloService.buscarPorId(moduloId);
        return bloqueRepositorio.listarPorModulo(moduloId);
    }

    public BloqueContenido actualizarTexto(Long bloqueId, String texto, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        if (bloque.getTipo() != TipoBloque.TEXTO) {
            throw new ReglaNegocioException("Solo los bloques de texto se pueden editar así");
        }
        Modulo modulo = moduloService.buscarPorId(bloque.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        BloqueContenido actualizado = BloqueContenido.builder()
                .id(bloque.getId())
                .moduloId(bloque.getModuloId())
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

        List<BloqueContenido> actuales = bloqueRepositorio.listarPorModulo(moduloId);
        Map<Long, BloqueContenido> porId = actuales.stream()
                .collect(java.util.stream.Collectors.toMap(BloqueContenido::getId, b -> b));

        if (idsEnOrden.size() != actuales.size() || !porId.keySet().containsAll(idsEnOrden)) {
            throw new ReglaNegocioException("La lista de orden no coincide con los bloques del módulo");
        }

        for (int i = 0; i < idsEnOrden.size(); i++) {
            BloqueContenido actual = porId.get(idsEnOrden.get(i));
            BloqueContenido actualizado = BloqueContenido.builder()
                    .id(actual.getId())
                    .moduloId(actual.getModuloId())
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

    public String obtenerUrlDescarga(Long bloqueId, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        Modulo modulo = moduloService.buscarPorId(bloque.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        validarAcceso(curso, usuarioActualId, rolActual);

        if (bloque.getTipo() != TipoBloque.IMAGEN && bloque.getTipo() != TipoBloque.ARCHIVO) {
            throw new ReglaNegocioException("Este bloque no tiene un archivo descargable");
        }
        return almacenamientoPuerto.generarUrlDescarga(bloque.getRutaArchivo());
    }

    public void eliminar(Long bloqueId, Long usuarioActualId, Rol rolActual) {
        BloqueContenido bloque = buscarPorId(bloqueId);
        Modulo modulo = moduloService.buscarPorId(bloque.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        if (bloque.getTipo() == TipoBloque.IMAGEN || bloque.getTipo() == TipoBloque.ARCHIVO) {
            almacenamientoPuerto.eliminar(bloque.getRutaArchivo());
        }
        bloqueRepositorio.eliminar(bloqueId);
    }

    private BloqueContenido buscarPorId(Long id) {
        return bloqueRepositorio.buscarPorId(id)
                .orElseThrow(() -> new BloqueNoEncontradoException(id));
    }

    private int siguienteOrden(Long moduloId) {
        return bloqueRepositorio.listarPorModulo(moduloId).size();
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
