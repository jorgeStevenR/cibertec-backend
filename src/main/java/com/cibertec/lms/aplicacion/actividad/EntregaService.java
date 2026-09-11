package com.cibertec.lms.aplicacion.actividad;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.actividad.Actividad;
import com.cibertec.lms.dominio.actividad.Entrega;
import com.cibertec.lms.dominio.actividad.EntregaRepositorio;
import com.cibertec.lms.dominio.almacenamiento.AlmacenamientoPuerto;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.excepcion.EntregaNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepositorio entregaRepositorio;
    private final ActividadService actividadService;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;
    private final AlmacenamientoPuerto almacenamientoPuerto;

    public Entrega entregar(Long actividadId, String nombreOriginal, InputStream contenido, long tamanio,
            String tipoContenidoHttp, Long estudianteId) {
        Actividad actividad = actividadService.buscarPorId(actividadId);
        Curso curso = validarMatriculado(actividad, estudianteId);

        Entrega existente = entregaRepositorio
                .buscarPorActividadYEstudiante(actividadId, estudianteId)
                .orElse(null);

        if (existente != null && existente.getNota() != null) {
            throw new ReglaNegocioException("Esta entrega ya fue calificada, no puedes reemplazarla");
        }

        String rutaDestino = "curso-%d/actividad-%d/%s-%s".formatted(
                curso.getId(), actividadId, UUID.randomUUID(), nombreOriginal);
        almacenamientoPuerto.subir(rutaDestino, contenido, tamanio, tipoContenidoHttp);

        if (existente != null) {
            almacenamientoPuerto.eliminar(existente.getRutaArchivo());
        }

        Entrega nueva = Entrega.builder()
                .id(existente != null ? existente.getId() : null)
                .actividadId(actividadId)
                .estudianteId(estudianteId)
                .nombreArchivo(nombreOriginal)
                .rutaArchivo(rutaDestino)
                .fechaEntrega(LocalDateTime.now())
                .nota(null)
                .comentarioDocente(null)
                .fechaCalificacion(null)
                .build();

        return entregaRepositorio.guardar(nueva);
    }

    public List<Entrega> listarPorActividad(Long actividadId, Long usuarioActualId, Rol rolActual) {
        Actividad actividad = actividadService.buscarPorId(actividadId);
        Modulo modulo = moduloService.buscarPorId(actividad.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        return entregaRepositorio.listarPorActividad(actividadId);
    }

    public Entrega obtenerMiEntrega(Long actividadId, Long estudianteId) {
        return entregaRepositorio.buscarPorActividadYEstudiante(actividadId, estudianteId).orElse(null);
    }

    public Entrega calificar(Long entregaId, double nota, String comentario, Long usuarioActualId, Rol rolActual) {
        Entrega entrega = buscarPorId(entregaId);
        Actividad actividad = actividadService.buscarPorId(entrega.getActividadId());
        Modulo modulo = moduloService.buscarPorId(actividad.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);

        if (nota < 0 || nota > actividad.getPuntajeMaximo()) {
            throw new ReglaNegocioException("La nota debe estar entre 0 y " + actividad.getPuntajeMaximo());
        }

        Entrega calificada = Entrega.builder()
                .id(entrega.getId())
                .actividadId(entrega.getActividadId())
                .estudianteId(entrega.getEstudianteId())
                .nombreArchivo(entrega.getNombreArchivo())
                .rutaArchivo(entrega.getRutaArchivo())
                .fechaEntrega(entrega.getFechaEntrega())
                .nota(nota)
                .comentarioDocente(comentario)
                .fechaCalificacion(LocalDateTime.now())
                .build();

        return entregaRepositorio.guardar(calificada);
    }

    public String obtenerUrlDescarga(Long entregaId, Long usuarioActualId, Rol rolActual) {
        Entrega entrega = buscarPorId(entregaId);

        if (rolActual == Rol.ESTUDIANTE) {
            if (!entrega.getEstudianteId().equals(usuarioActualId)) {
                throw new AccessDeniedException("No puedes ver la entrega de otro estudiante");
            }
        } else {
            Actividad actividad = actividadService.buscarPorId(entrega.getActividadId());
            Modulo modulo = moduloService.buscarPorId(actividad.getModuloId());
            Curso curso = cursoService.buscarPorId(modulo.getCursoId());
            cursoService.validarPropietario(curso, usuarioActualId, rolActual);
        }

        return almacenamientoPuerto.generarUrlDescarga(entrega.getRutaArchivo());
    }

    private Entrega buscarPorId(Long id) {
        return entregaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new EntregaNoEncontradaException(id));
    }

    private Curso validarMatriculado(Actividad actividad, Long estudianteId) {
        Modulo modulo = moduloService.buscarPorId(actividad.getModuloId());
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        if (!matriculaRepositorio.existePorCursoYEstudiante(curso.getId(), estudianteId)) {
            throw new AccessDeniedException("No estás matriculado en este curso");
        }
        return curso;
    }
}
