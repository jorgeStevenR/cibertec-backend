package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;

import com.cibertec.lms.dominio.cuestionario.IntentoCuestionario;
import com.cibertec.lms.dominio.cuestionario.RespuestaValor;

class IntentoCuestionarioMapper {

    private IntentoCuestionarioMapper() {
    }

    static IntentoCuestionario aDominio(IntentoCuestionarioJpaEntity entidad, List<RespuestaValor> respuestas) {
        return IntentoCuestionario.builder()
                .id(entidad.getId())
                .cuestionarioId(entidad.getCuestionarioId())
                .estudianteId(entidad.getEstudianteId())
                .nota(entidad.getNota())
                .puntajeMaximo(entidad.getPuntajeMaximo())
                .fechaEntrega(entidad.getFechaEntrega())
                .respuestas(respuestas)
                .build();
    }

    static IntentoCuestionarioJpaEntity aEntidad(IntentoCuestionario intento) {
        IntentoCuestionarioJpaEntity entidad = new IntentoCuestionarioJpaEntity();
        entidad.setId(intento.getId());
        entidad.setCuestionarioId(intento.getCuestionarioId());
        entidad.setEstudianteId(intento.getEstudianteId());
        entidad.setNota(intento.getNota());
        entidad.setPuntajeMaximo(intento.getPuntajeMaximo());
        entidad.setFechaEntrega(intento.getFechaEntrega());
        return entidad;
    }
}
