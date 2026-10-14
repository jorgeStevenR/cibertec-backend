package com.cibertec.lms.aplicacion.cuestionario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.CuestionarioRepositorio;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionarioRepositorio;
import com.cibertec.lms.dominio.excepcion.CuestionarioNoEncontradoException;
import com.cibertec.lms.dominio.matricula.MatriculaRepositorio;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

/** Depende del repositorio de intentos directamente (no de IntentoCuestionarioService) para evitar
 *  una dependencia circular: IntentoCuestionarioService a su vez depende de este servicio. */
@Service
@RequiredArgsConstructor
public class CuestionarioService {

    private final CuestionarioRepositorio cuestionarioRepositorio;
    private final PreguntaRepositorio preguntaRepositorio;
    private final IntentoCuestionarioRepositorio intentoCuestionarioRepositorio;
    private final ModuloService moduloService;
    private final CursoService cursoService;
    private final MatriculaRepositorio matriculaRepositorio;

    public Cuestionario crear(
            Long moduloId, String titulo, String descripcion, LocalDateTime fechaApertura, LocalDateTime fechaEntrega,
            Integer tiempoLimiteMinutos, boolean mostrarNota, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        Cuestionario nuevo = Cuestionario.builder()
                .moduloId(moduloId)
                .titulo(titulo)
                .descripcion(descripcion)
                .fechaApertura(fechaApertura)
                .fechaEntrega(fechaEntrega)
                .tiempoLimiteMinutos(tiempoLimiteMinutos)
                .mostrarNota(mostrarNota)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return cuestionarioRepositorio.guardar(nuevo);
    }

    public Cuestionario buscarPorId(Long id) {
        return cuestionarioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new CuestionarioNoEncontradoException(id));
    }

    public List<Cuestionario> listarPorModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        validarAcceso(curso, usuarioActualId, rolActual);
        return cuestionarioRepositorio.listarPorModulo(moduloId);
    }

    public Cuestionario actualizar(
            Long id, String titulo, String descripcion, LocalDateTime fechaApertura, LocalDateTime fechaEntrega,
            Integer tiempoLimiteMinutos, boolean mostrarNota, Long usuarioActualId, Rol rolActual) {
        Cuestionario existente = buscarPorId(id);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);

        Cuestionario actualizado = Cuestionario.builder()
                .id(existente.getId())
                .moduloId(existente.getModuloId())
                .titulo(titulo)
                .descripcion(descripcion)
                .fechaApertura(fechaApertura)
                .fechaEntrega(fechaEntrega)
                .tiempoLimiteMinutos(tiempoLimiteMinutos)
                .mostrarNota(mostrarNota)
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return cuestionarioRepositorio.guardar(actualizado);
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        Cuestionario existente = buscarPorId(id);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);

        intentoCuestionarioRepositorio.eliminarPorCuestionario(id);
        preguntaRepositorio.listarPorCuestionario(id).forEach(p -> preguntaRepositorio.eliminar(p.getId()));
        cuestionarioRepositorio.eliminar(id);
    }

    public double calcularPuntajeMaximo(Long cuestionarioId) {
        return preguntaRepositorio.listarPorCuestionario(cuestionarioId).stream()
                .mapToInt(Pregunta::getPuntaje)
                .sum();
    }

    /** Dentro de la ventana [fechaApertura, fechaEntrega]; un límite en null se trata como "sin restricción". */
    public boolean estaDisponible(Cuestionario cuestionario) {
        return !aunNoAbre(cuestionario) && !yaVencio(cuestionario);
    }

    public boolean aunNoAbre(Cuestionario cuestionario) {
        return cuestionario.getFechaApertura() != null && LocalDateTime.now().isBefore(cuestionario.getFechaApertura());
    }

    public boolean yaVencio(Cuestionario cuestionario) {
        return cuestionario.getFechaEntrega() != null && LocalDateTime.now().isAfter(cuestionario.getFechaEntrega());
    }

    void validarPropietarioDelModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
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
