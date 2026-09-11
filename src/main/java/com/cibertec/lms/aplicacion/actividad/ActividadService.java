package com.cibertec.lms.aplicacion.actividad;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.lms.aplicacion.curso.CursoService;
import com.cibertec.lms.aplicacion.curso.ModuloService;
import com.cibertec.lms.dominio.actividad.Actividad;
import com.cibertec.lms.dominio.actividad.ActividadRepositorio;
import com.cibertec.lms.dominio.curso.Curso;
import com.cibertec.lms.dominio.curso.Modulo;
import com.cibertec.lms.dominio.excepcion.ActividadNoEncontradaException;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepositorio actividadRepositorio;
    private final ModuloService moduloService;
    private final CursoService cursoService;

    public Actividad crear(Long moduloId, String titulo, String descripcion, LocalDateTime fechaEntrega,
            double puntajeMaximo, Long usuarioActualId, Rol rolActual) {
        validarPropietarioDelModulo(moduloId, usuarioActualId, rolActual);

        Actividad nueva = Actividad.builder()
                .moduloId(moduloId)
                .titulo(titulo)
                .descripcion(descripcion)
                .fechaEntrega(fechaEntrega)
                .puntajeMaximo(puntajeMaximo)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return actividadRepositorio.guardar(nueva);
    }

    public Actividad buscarPorId(Long id) {
        return actividadRepositorio.buscarPorId(id)
                .orElseThrow(() -> new ActividadNoEncontradaException(id));
    }

    public List<Actividad> listarPorModulo(Long moduloId) {
        moduloService.buscarPorId(moduloId);
        return actividadRepositorio.listarPorModulo(moduloId);
    }

    public Actividad actualizar(Long id, String titulo, String descripcion, LocalDateTime fechaEntrega,
            double puntajeMaximo, Long usuarioActualId, Rol rolActual) {
        Actividad existente = buscarPorId(id);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);

        Actividad actualizada = Actividad.builder()
                .id(existente.getId())
                .moduloId(existente.getModuloId())
                .titulo(titulo)
                .descripcion(descripcion)
                .fechaEntrega(fechaEntrega)
                .puntajeMaximo(puntajeMaximo)
                .fechaCreacion(existente.getFechaCreacion())
                .build();

        return actividadRepositorio.guardar(actualizada);
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        Actividad existente = buscarPorId(id);
        validarPropietarioDelModulo(existente.getModuloId(), usuarioActualId, rolActual);
        actividadRepositorio.eliminar(id);
    }

    private void validarPropietarioDelModulo(Long moduloId, Long usuarioActualId, Rol rolActual) {
        Modulo modulo = moduloService.buscarPorId(moduloId);
        Curso curso = cursoService.buscarPorId(modulo.getCursoId());
        cursoService.validarPropietario(curso, usuarioActualId, rolActual);
    }
}
