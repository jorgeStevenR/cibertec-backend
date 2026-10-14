package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.comun.Calificacion;
import com.cibertec.lms.dominio.estudiante.EstadoCalificacion;
import com.cibertec.lms.dominio.estudiante.ItemCalificacion;
import com.cibertec.lms.dominio.estudiante.TipoItem;

/** nota, fechaEntrega, fechaCalificacion y comentarioDocente viajan como null explícito cuando todavía no aplican. */
public record CalificacionItemResponseDTO(
        TipoItem tipo,
        Long id,
        String titulo,
        Long moduloId,
        String moduloTitulo,
        double puntajeMaximo,
        Double nota,
        /** nota en escala 0,0–5,0; null cuando nota es null. */
        Double notaSobre5,
        EstadoCalificacion estado,
        LocalDateTime fechaEntrega,
        LocalDateTime fechaCalificacion,
        String comentarioDocente
) {
    public static CalificacionItemResponseDTO desde(ItemCalificacion item) {
        return new CalificacionItemResponseDTO(
                item.tipo(),
                item.id(),
                item.titulo(),
                item.moduloId(),
                item.moduloTitulo(),
                item.puntajeMaximo(),
                item.nota(),
                Calificacion.sobre5(item.nota(), item.puntajeMaximo()),
                item.estado(),
                item.fechaEntrega(),
                item.fechaCalificacion(),
                item.comentarioDocente()
        );
    }
}
