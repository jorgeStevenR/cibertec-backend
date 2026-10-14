package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cibertec.lms.dominio.cuestionario.EstadoIntento;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.IntentoCuestionarioRepositorio;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
class IntentoCuestionarioRepositorioImpl implements IntentoCuestionarioRepositorio {

    private final IntentoCuestionarioJpaRepository intentoJpaRepository;
    private final RespuestaIntentoJpaRepository respuestaJpaRepository;

    @Override
    public IntentoCuestionario guardar(IntentoCuestionario intento) {
        IntentoCuestionarioJpaEntity guardado = intentoJpaRepository.save(IntentoCuestionarioMapper.aEntidad(intento));
        Long intentoId = guardado.getId();

        List<RespuestaValor> respuestas = intento.getRespuestas().stream()
                .map(r -> guardarRespuesta(intentoId, r))
                .toList();

        return IntentoCuestionarioMapper.aDominio(guardado, respuestas);
    }

    @Override
    public Optional<IntentoCuestionario> buscarPorId(Long id) {
        return intentoJpaRepository.findById(id).map(this::conRespuestas);
    }

    @Override
    public Optional<IntentoCuestionario> buscarPorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId) {
        return intentoJpaRepository.findByCuestionarioIdAndEstudianteId(cuestionarioId, estudianteId)
                .map(this::conRespuestas);
    }

    @Override
    public boolean existePorCuestionarioYEstudiante(Long cuestionarioId, Long estudianteId) {
        return intentoJpaRepository.existsByCuestionarioIdAndEstudianteId(cuestionarioId, estudianteId);
    }

    @Override
    public List<IntentoCuestionario> listarPorCuestionario(Long cuestionarioId) {
        return intentoJpaRepository.buscarPorCuestionario(cuestionarioId).stream().map(this::conRespuestas).toList();
    }

    @Override
    @Transactional
    public IntentoCuestionario calificarRespuesta(Long intentoId, Long preguntaId, double puntajeObtenido, double nota, EstadoIntento estado) {
        RespuestaIntentoJpaEntity respuesta = respuestaJpaRepository.buscarPorIntentoYPregunta(intentoId, preguntaId).orElseThrow();
        respuesta.setPuntajeObtenido(puntajeObtenido);
        respuestaJpaRepository.save(respuesta);

        IntentoCuestionarioJpaEntity intento = intentoJpaRepository.findById(intentoId).orElseThrow();
        intento.setNota(nota);
        intento.setEstado(estado);
        return conRespuestas(intentoJpaRepository.save(intento));
    }

    @Override
    public void eliminarPorCuestionario(Long cuestionarioId) {
        for (IntentoCuestionarioJpaEntity intento : intentoJpaRepository.buscarPorCuestionario(cuestionarioId)) {
            respuestaJpaRepository.eliminarPorIntento(intento.getId());
            intentoJpaRepository.deleteById(intento.getId());
        }
    }

    private RespuestaValor guardarRespuesta(Long intentoId, RespuestaValor respuesta) {
        RespuestaIntentoJpaEntity entidad = new RespuestaIntentoJpaEntity();
        entidad.setIntentoId(intentoId);
        entidad.setPreguntaId(respuesta.preguntaId());
        entidad.setOpcionId(respuesta.opcionId());
        entidad.setTextoRespuesta(respuesta.texto());
        entidad.setPuntajeObtenido(respuesta.puntajeObtenido());
        respuestaJpaRepository.save(entidad);
        return respuesta;
    }

    private IntentoCuestionario conRespuestas(IntentoCuestionarioJpaEntity entidad) {
        List<RespuestaValor> respuestas = respuestaJpaRepository.buscarPorIntento(entidad.getId()).stream()
                .map(IntentoCuestionarioMapper::aDominio)
                .toList();
        return IntentoCuestionarioMapper.aDominio(entidad, respuestas);
    }
}
