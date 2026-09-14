package com.cibertec.lms.infraestructura.persistencia.cuestionario;

import java.util.List;

import com.cibertec.lms.dominio.cuestionario.Opcion;
import com.cibertec.lms.dominio.cuestionario.Pregunta;

class PreguntaMapper {

    private PreguntaMapper() {
    }

    static Opcion aDominio(OpcionJpaEntity entidad) {
        return Opcion.builder()
                .id(entidad.getId())
                .preguntaId(entidad.getPreguntaId())
                .texto(entidad.getTexto())
                .esCorrecta(entidad.isEsCorrecta())
                .build();
    }

    static Pregunta aDominio(PreguntaJpaEntity entidad, List<Opcion> opciones) {
        return Pregunta.builder()
                .id(entidad.getId())
                .cuestionarioId(entidad.getCuestionarioId())
                .enunciado(entidad.getEnunciado())
                .puntaje(entidad.getPuntaje())
                .orden(entidad.getOrden())
                .opciones(opciones)
                .build();
    }

    static PreguntaJpaEntity aEntidad(Pregunta pregunta) {
        PreguntaJpaEntity entidad = new PreguntaJpaEntity();
        entidad.setId(pregunta.getId());
        entidad.setCuestionarioId(pregunta.getCuestionarioId());
        entidad.setEnunciado(pregunta.getEnunciado());
        entidad.setPuntaje(pregunta.getPuntaje());
        entidad.setOrden(pregunta.getOrden());
        return entidad;
    }
}
