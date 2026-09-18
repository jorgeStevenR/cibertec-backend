package com.cibertec.lms.dominio.estudiante;

import java.time.LocalDateTime;

public record Vencimiento(
        TipoItem tipo,
        Long id,
        String titulo,
        Long cursoId,
        String cursoTitulo,
        Long moduloId,
        LocalDateTime fechaLimite,
        EstadoVencimiento estado
) {
}
