package com.cibertec.lms.aplicacion.cuestionario;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.excepcion.PreguntaNoEncontradaException;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreguntaService {

    private final PreguntaRepositorio preguntaRepositorio;
    private final CuestionarioService cuestionarioService;

    public Pregunta crear(
            Long cuestionarioId, String enunciado, int puntaje, List<Opcion> opciones,
            Long usuarioActualId, Rol rolActual) {
        Cuestionario cuestionario = cuestionarioService.buscarPorId(cuestionarioId);
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);

        int orden = preguntaRepositorio.listarPorCuestionario(cuestionarioId).size();

        Pregunta nueva = Pregunta.builder()
                .cuestionarioId(cuestionarioId)
                .enunciado(enunciado)
                .puntaje(puntaje)
                .orden(orden)
                .opciones(opciones)
                .build();

        return preguntaRepositorio.guardar(nueva);
    }

    public Pregunta buscarPorId(Long id) {
        return preguntaRepositorio.buscarPorId(id)
                .orElseThrow(() -> new PreguntaNoEncontradaException(id));
    }

    public List<Pregunta> listarPorCuestionario(Long cuestionarioId) {
        return preguntaRepositorio.listarPorCuestionario(cuestionarioId);
    }

    public Pregunta actualizar(
            Long id, String enunciado, int puntaje, List<Opcion> opciones,
            Long usuarioActualId, Rol rolActual) {
        Pregunta existente = buscarPorId(id);
        Cuestionario cuestionario = cuestionarioService.buscarPorId(existente.getCuestionarioId());
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);

        Pregunta actualizada = Pregunta.builder()
                .id(existente.getId())
                .cuestionarioId(existente.getCuestionarioId())
                .enunciado(enunciado)
                .puntaje(puntaje)
                .orden(existente.getOrden())
                .opciones(opciones)
                .build();

        return preguntaRepositorio.guardar(actualizada);
    }

    public void eliminar(Long id, Long usuarioActualId, Rol rolActual) {
        Pregunta existente = buscarPorId(id);
        Cuestionario cuestionario = cuestionarioService.buscarPorId(existente.getCuestionarioId());
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);
        preguntaRepositorio.eliminar(id);
    }
}
