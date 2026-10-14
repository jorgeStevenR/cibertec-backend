package com.cibertec.lms.aplicacion.cuestionario;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cibertec.lms.dominio.cuestionario.Cuestionario;
import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;
import com.cibertec.lms.dominio.cuestionario.PreguntaRepositorio;
import com.cibertec.lms.dominio.cuestionario.TipoPregunta;
import com.cibertec.lms.dominio.excepcion.PreguntaNoEncontradaException;
import com.cibertec.lms.dominio.excepcion.ReglaNegocioException;
import com.cibertec.lms.dominio.usuario.Rol;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreguntaService {

    private final PreguntaRepositorio preguntaRepositorio;
    private final CuestionarioService cuestionarioService;

    public Pregunta crear(
            Long cuestionarioId, TipoPregunta tipo, String enunciado, int puntaje, List<Opcion> opciones,
            Long usuarioActualId, Rol rolActual) {
        Cuestionario cuestionario = cuestionarioService.buscarPorId(cuestionarioId);
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);

        int orden = preguntaRepositorio.listarPorCuestionario(cuestionarioId).size();

        Pregunta nueva = Pregunta.builder()
                .cuestionarioId(cuestionarioId)
                .tipo(tipo)
                .enunciado(enunciado)
                .puntaje(puntaje)
                .orden(orden)
                .opciones(tipo.esCerrada() ? opciones : List.of())
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
            Long id, TipoPregunta tipo, String enunciado, int puntaje, List<Opcion> opciones,
            Long usuarioActualId, Rol rolActual) {
        Pregunta existente = buscarPorId(id);
        Cuestionario cuestionario = cuestionarioService.buscarPorId(existente.getCuestionarioId());
        cuestionarioService.validarPropietarioDelModulo(cuestionario.getModuloId(), usuarioActualId, rolActual);

        // Si una pregunta ya respondida pasara de cerrada a abierta (o al revés), sus respuestas guardadas no
        // tendrían sentido para el nuevo tipo; para cambiarlo se borra y se crea otra.
        if (tipo != existente.getTipo()) {
            throw new ReglaNegocioException("El tipo de una pregunta no se puede cambiar; elimínala y crea una nueva");
        }

        Pregunta actualizada = Pregunta.builder()
                .id(existente.getId())
                .cuestionarioId(existente.getCuestionarioId())
                .tipo(tipo)
                .enunciado(enunciado)
                .puntaje(puntaje)
                .orden(existente.getOrden())
                .opciones(tipo.esCerrada() ? opciones : List.of())
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
