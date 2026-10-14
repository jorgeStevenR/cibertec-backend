package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.actividad.Entrega;
import com.cibertec.lms.dominio.comun.Calificacion;
import com.cibertec.lms.dominio.usuario.Usuario;

public record EntregaResponseDTO(
        Long id,
        Long actividadId,
        Long estudianteId,
        String estudianteNombre,
        String estudianteEmail,
        String nombreArchivo,
        LocalDateTime fechaEntrega,
        Double nota,
        /** nota en escala 0,0–5,0 sobre el puntaje máximo de la actividad; null mientras no esté calificada. */
        Double notaSobre5,
        String comentarioDocente,
        LocalDateTime fechaCalificacion
) {
    public static EntregaResponseDTO desde(Entrega entrega, Usuario estudiante, double puntajeMaximoActividad) {
        return new EntregaResponseDTO(
                entrega.getId(),
                entrega.getActividadId(),
                entrega.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                entrega.getNombreArchivo(),
                entrega.getFechaEntrega(),
                entrega.getNota(),
                Calificacion.sobre5(entrega.getNota(), puntajeMaximoActividad),
                entrega.getComentarioDocente(),
                entrega.getFechaCalificacion()
        );
    }
}
