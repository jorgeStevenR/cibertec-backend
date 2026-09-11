package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.actividad.Entrega;
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
        String comentarioDocente,
        LocalDateTime fechaCalificacion
) {
    public static EntregaResponseDTO desde(Entrega entrega, Usuario estudiante) {
        return new EntregaResponseDTO(
                entrega.getId(),
                entrega.getActividadId(),
                entrega.getEstudianteId(),
                estudiante != null ? estudiante.getNombre() + " " + estudiante.getApellido() : null,
                estudiante != null ? estudiante.getEmail() : null,
                entrega.getNombreArchivo(),
                entrega.getFechaEntrega(),
                entrega.getNota(),
                entrega.getComentarioDocente(),
                entrega.getFechaCalificacion()
        );
    }
}
