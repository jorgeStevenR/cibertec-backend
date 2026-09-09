package com.cibertec.lms.aplicacion.curso;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.ContenidoModulo;
import com.cibertec.lms.dominio.curso.ContenidoRepositorio;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.curso.TipoContenido;
import com.cibertec.lms.dominio.excepcion.ContenidoNoEncontradoException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContenidoService {

    private final ContenidoRepositorio contenidoRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;

    public ContenidoModulo subirArchivo(
            Long moduloId, TipoContenido tipo, String nombreOriginal, InputStream contenido,
            long tamanio, String tipoContenidoHttp, Long usuarioActualId, Rol rolActual) {

        Curso curso = validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        String rutaDestino = "curso-%d/modulo-%d/%s-%s".formatted(
                curso.getId(), moduloId, UUID.randomUUID(), nombreOriginal);
        almacenamientoPuerto.subir(rutaDestino, contenido, tamanio, tipoContenidoHttp);

        ContenidoModulo nuevo = ContenidoModulo.builder()
                .moduloId(moduloId)
                .tipo(tipo)
                .nombre(nombreOriginal)
                .rutaArchivo(rutaDestino)
                .fechaSubida(LocalDateTime.now())
                .build();

        return contenidoRepositorio.guardar(nuevo);
    }

    public ContenidoModulo agregarEnlace(Long moduloId, String nombre, String url, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        ContenidoModulo nuevo = ContenidoModulo.builder()
                .moduloId(moduloId)
                .tipo(TipoContenido.ENLACE)
                .nombre(nombre)
                .url(url)
                .fechaSubida(LocalDateTime.now())
                .build();

        return contenidoRepositorio.guardar(nuevo);
    }

    public List<ContenidoModulo> listarPorModulo(Long moduloId) {
        moduloService.buscarPorId(moduloId);
        return contenidoRepositorio.listarPorModulo(moduloId);
    }

    public String obtenerUrlDescarga(Long contenidoId, Long usuarioActualId, Rol rolActual) {
        ContenidoModulo contenido = buscarPorId(contenidoId);
        Modulo modulo = moduloService.buscarPorId(contenido.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());

        validarAcceso(curso, usuarioActualId, rolActual);

        if (contenido.getTipo() == TipoContenido.ENLACE) {
            return contenido.getUrl();
        }
        return almacenamientoPuerto.generarUrlDescarga(contenido.getRutaArchivo());
    }

    public void eliminar(Long contenidoId, Long usuarioActualId, Rol rolActual) {
        ContenidoModulo contenido = buscarPorId(contenidoId);
        Modulo modulo = moduloService.buscarPorId(contenido.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        if (contenido.getTipo() != TipoContenido.ENLACE) {
            almacenamientoPuerto.eliminar(contenido.getRutaArchivo());
        }
        contenidoRepositorio.eliminar(contenidoId);
    }

    private ContenidoModulo buscarPorId(Long id) {
        return contenidoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ContenidoNoEncontradoException(id));
    }

    private Curso validarPropietarioDelModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        return curso;
    }

    /** A diferencia de validarPropietario (admin/profesor), aquí también se permite al estudiante matriculado. */
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
