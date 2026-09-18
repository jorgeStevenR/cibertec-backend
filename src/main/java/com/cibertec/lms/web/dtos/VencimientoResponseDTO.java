package com.cibertec.lms.web.dtos;

import java.time.LocalDateTime;

import com.cibertec.lms.dominio.estudiante.EstadoVencimiento;
import com.cibertec.lms.dominio.estudiante.TipoItem;
import com.cibertec.lms.dominio.estudiante.Vencimiento;

public record VencimientoResponseDTO(
        TipoItem tipo,
        Long id,
        String titulo,
        Long cursoId,
        String cursoTitulo,
        Long moduloId,
        LocalDateTime fechaLimite,
        EstadoVencimiento estado
) {
    public static VencimientoResponseDTO desde(Vencimiento vencimiento) {
        return new VencimientoResponseDTO(
                vencimiento.tipo(),
                vencimiento.id(),
                vencimiento.titulo(),
                vencimiento.cursoId(),
                vencimiento.cursoTitulo(),
                vencimiento.moduloId(),
                vencimiento.fechaLimite(),
                vencimiento.estado()
        );
    }
}
